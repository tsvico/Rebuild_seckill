package cn.peoplevip.miaosha.Service;

import cn.peoplevip.common.Utils.MD5Util;
import cn.peoplevip.common.Utils.UUIDUtil;
import cn.peoplevip.common.api.GoodsService;
import cn.peoplevip.common.api.MiaoshaService;
import cn.peoplevip.common.api.OrderService;
import cn.peoplevip.common.api.RedisService;
import cn.peoplevip.common.domain.MiaoshaOrder;
import cn.peoplevip.common.domain.MiaoshaUser;
import cn.peoplevip.common.domain.OrderInfo;
import cn.peoplevip.common.redisKey.MiaoshaKey;
import cn.peoplevip.common.redisKey.OrderKey;
import cn.peoplevip.common.vo.GoodsVo;
import cn.peoplevip.miaosha.dao.MiaoshaDao;
import org.apache.commons.collections.MapUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/4/13 20:47
 * 功能
 */
@Service
public class MiaoshaServiceImpl implements MiaoshaService {

    private static final Logger log = LoggerFactory.getLogger(MiaoshaServiceImpl.class);

    @Autowired
    RedisService redisService;

    @Autowired(required = false)
    MiaoshaDao miaoshaDao;

    @Reference
    GoodsService goodsService;

    @Reference
    OrderService orderService;

    //使用存储过程进行秒杀
    @Override
    public int executeMiaosha(long userId, GoodsVo goods) {
        OrderInfo orderInfo = new OrderInfo();
        long orderId = UUIDUtil.uuid();
        //存储过程减库存，建立订单
        //-1 重复秒杀 //-2 系统异常  // 1  成功  // 0 秒杀结束
        int res = executeMiaosha(userId, goods.getId(), orderId);
        if (res == 1) {
            //订单ID
            orderInfo.setGoodsId(goods.getId());
            orderInfo.setUserId(userId);
            orderInfo.setGoodsName(goods.getGoodsName());
            orderInfo.setGoodsPrice(goods.getMiaoshaPrice());
            OrderInfo orderinfo = createOrderExecte(orderId, orderInfo);
        }
        return res;
    }

    //存储过程插入订单详情
    public OrderInfo createOrderExecte(long orderId, OrderInfo orderInfo){
        //订单ID
        orderInfo.setId(orderId);
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);

        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        // 不再使用返回值
        miaoshaDao.OrderInfoInsert(orderInfo);
        //将订单详情存入Redis
        redisService.set(OrderKey.getMiaoshaOrderInfoByUidGid, "" + orderId, orderInfo);
        return orderInfo;
    }
    //存储过程秒杀
    //使用存储过程减库存
    public int executeMiaosha(long userId, long goodsId,
                              long orderId){
        HashMap<String,Object> map = new HashMap<>(6);
        map.put("userId",userId);
        map.put("goodsId",goodsId);
        map.put("orderId",orderId);
        map.put("result",null);
        //存储过程执行完，result被赋值
        try {
            miaoshaDao.executeMiaosha(map);
            //获取result
            return MapUtils.getInteger(map,"result",-2);
        }catch (Exception e){
            e.printStackTrace();
        }
        return -2;
    }


    @Override
    public long getMiaoshaResult(Long userid, long goodsId) {
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(userid, goodsId);
        if (order != null) {
            //秒杀成功
            log.info(order.toString());
            return order.getOrderId();
        } else {
            //两种情况
            boolean isOver = getGoodsOver(goodsId);
            if (isOver) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    @Override
    public void reset(List<GoodsVo> goodsList) {
        goodsService.resetStock(goodsList);
        orderService.deleteOrders();
    }

    @Override
    public void setGoodsOver(Long id) {
        redisService.set(MiaoshaKey.isGoodsOver, "" + id, true);
    }

    private boolean getGoodsOver(long goodsId) {
        //有这个key说明卖完了
        return redisService.exists(MiaoshaKey.isGoodsOver, "" + goodsId);
    }

    @Override
    public boolean checkPath(MiaoshaUser user, long goodsId, String path) {
        if (user == null || path == null) {
            return false;
        }
        String pathOld = redisService.get(MiaoshaKey.getMiaoshaPath, user.getId() + "_" + goodsId, String.class);
        return path.equals(pathOld);
    }

    @Override
    public String createMiaoshaPath(MiaoshaUser user, long goodsId) {
        String hash = MD5Util.getMD5(UUIDUtil.uuid() + "");
        redisService.set(MiaoshaKey.getMiaoshaPath,
                user.getId() + "_" + goodsId, hash);
        return hash;
    }

}
