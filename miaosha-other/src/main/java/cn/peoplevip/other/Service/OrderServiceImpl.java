package cn.peoplevip.other.Service;

import cn.peoplevip.common.Utils.UUIDUtil;
import cn.peoplevip.common.api.OrderService;
import cn.peoplevip.common.api.RedisService;
import cn.peoplevip.common.domain.MiaoshaOrder;
import cn.peoplevip.common.domain.MiaoshaUser;
import cn.peoplevip.common.domain.OrderInfo;
import cn.peoplevip.common.domain.OrderInfoList;
import cn.peoplevip.common.redisKey.OrderKey;
import cn.peoplevip.common.vo.GoodsVo;
import cn.peoplevip.common.vo.OrderInfoVo;
import cn.peoplevip.other.dao.OrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Date;
import java.util.List;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/2/27 11:37
 */
@Service
@org.apache.dubbo.config.annotation.Service
public class OrderServiceImpl implements OrderService {

    @Autowired(required = false)
    OrderDao orderDao;

    @Autowired
    RedisService redisService;


    @Override
    public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(Long userid, long goodsId) {
        /*MiaoshaOrder order = redisService.get(OrderKey.getMiaoshaOrderByUidGid, "" + userid + "_" + goodsId, MiaoshaOrder.class);
        //这里判断防止Redis数据重置后导致异常
        if (order == null) {
            order = orderDao.getMiaoshaOrderByUserIdGoodsId(userid, goodsId);
            //将订单写入Redis
            if (order != null) {
                redisService.set(OrderKey.getMiaoshaOrderByUidGid, "" + userid + "_" + goodsId, order);
            }
        }
        return order;*/
        return orderDao.getMiaoshaOrderByUserIdGoodsId(userid,goodsId);
    }

    @Override
    @Transactional
    public OrderInfo createOrder(MiaoshaUser user, GoodsVo goods) {
        OrderInfo orderInfo = new OrderInfo();
        long orderId = UUIDUtil.uuid();
        //订单ID
        orderInfo.setId(orderId);
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getMiaoshaPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(user.getId());
        //long orderId = orderDao.insert(orderInfo);
        // 不再使用返回值
        orderDao.insert(orderInfo);
        MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
        miaoshaOrder.setOrderId(orderId);
        miaoshaOrder.setUserId(user.getId());
        miaoshaOrder.setGoodsId(goods.getId());
        long result = orderDao.insertMiaoshaOrder(miaoshaOrder);

        if (result == 0) {
            //插入失败
            //手动回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
        //将订单写入Redis
        redisService.set(OrderKey.getMiaoshaOrderByUidGid, "" + user.getId() + "_" + goods.getId(), miaoshaOrder);
        //将订单详情存入Redis
        redisService.set(OrderKey.getMiaoshaOrderInfoByUidGid, "" + orderId, orderInfo);
        return orderInfo;
    }

    @Override
    public OrderInfo getMiaoshaOrderByOrderId(long orderId) {
        OrderInfo orderInfo = redisService.get(OrderKey.getMiaoshaOrderInfoByUidGid, "" + orderId, OrderInfo.class);
        if (orderInfo != null) {
            return orderInfo;
        }
        return orderDao.getOrderByOrderId(orderId);
    }

    @Override
    public void deleteOrders() {
        orderDao.deleteOrders();
        orderDao.deleteMiaoshaOrders();
    }
    //查看全部订单
    @Override
    public List<OrderInfoVo> getOrderList(){
        return orderDao.getAllOrderInfo();
    }


    @Override
    public int updateCloseOrder(Long ids) {
        return orderDao.updateCloseOrder(ids);
    }

    //获取用户的订单列表
    @Override
    public List<OrderInfoList> getMiaoshaOrders(MiaoshaUser user) {
        return orderDao.getUserOrderInfos(user);
    }
}
