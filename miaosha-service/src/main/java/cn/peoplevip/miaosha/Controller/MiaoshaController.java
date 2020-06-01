package cn.peoplevip.miaosha.Controller;

import cn.peoplevip.common.api.*;
import cn.peoplevip.common.domain.MiaoshaOrder;
import cn.peoplevip.common.domain.MiaoshaUser;
import cn.peoplevip.common.redisKey.GoodsKey;
import cn.peoplevip.common.redisKey.MiaoshaKey;
import cn.peoplevip.common.redisKey.OrderKey;
import cn.peoplevip.common.result.CodeMsg;
import cn.peoplevip.common.result.Result;
import cn.peoplevip.common.vo.GoodsDetailVo;
import cn.peoplevip.common.vo.GoodsVo;
import cn.peoplevip.miaosha.Bean.LocalOverMap;
import cn.peoplevip.miaosha.access.AccessLimit;
import cn.peoplevip.miaosha.rabbitmq.MQSend;
import cn.peoplevip.miaosha.rabbitmq.MiaoshaMessage;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/2/24 11:07
 */
@Controller
@RequestMapping("/seckill")
public class MiaoshaController implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(MiaoshaController.class);

    @Autowired
    RedisService redisService;

    @Autowired
    MiaoshaService miaoshaService;

    @Reference
    GoodsService goodsService;

    @Reference
    OrderService orderService;

    @Autowired
    MQSend mqSend;

    @Reference
    KaptchaService kaptchaService;


    /**
     * 标记秒杀是否结束
     */
    @Autowired
    private LocalOverMap localOverMap;

    /**
     * 系统初始化之后自动做一些事情
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
        if (goodsVoList == null) {
            return;
        }
        for (GoodsVo goods : goodsVoList) {
            //写入库存
            redisService.set(GoodsKey.getMiaoshaGoodsStock, "" + goods.getId(), goods.getStockCount());
            localOverMap.put(goods.getId(), false);
        }
    }

    /**
     * 数据重置
     *
     * @param user
     * @return
     */
    @ApiOperation(value = "重置测试数据")
    @RequestMapping(value = "/reset", method = RequestMethod.GET)
    @ResponseBody
    public Result<Boolean> reset(MiaoshaUser user) {
        if (user == null) {
            return Result.error(CodeMsg.USER_NOT_LOGIN);
        }

        if (user.getId() != 15588537323L) {
            return Result.error(CodeMsg.USER_NOT_ADMIN);
        }
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        for (GoodsVo goods : goodsList) {
            goods.setStockCount(10);
            redisService.set(GoodsKey.getMiaoshaGoodsStock, "" + goods.getId(), 10);
            localOverMap.put(goods.getId(), false);
        }
        redisService.delete(OrderKey.getMiaoshaOrderByUidGid);
        redisService.delete(MiaoshaKey.isGoodsOver);
        redisService.delete(GoodsKey.getGoodsDetail);
        // miaoshaService.reset(goodsList);
//        Map<Long, Boolean> localOver =  localOverMap.getLocalOverMap();
//        for (Map.Entry<Long, Boolean> longBooleanEntry : localOver.entrySet()) {
//            Map.Entry<Long,Boolean> entry = longBooleanEntry;
//            Long key = (Long) entry.getKey();
//            entry.setValue(false);
//        }
//        localOverMap.setLocalOverMap(localOver);
        log.info("数据已重置");
        return Result.success(true);
    }

    @ApiOperation(value = "获取商品详情")
    @GetMapping("/to_detail/{goodsId}")
    @AccessLimit(seconds = 2, maxCount = 5, needLogin = true)
    @ResponseBody
    public Result<Object> detail(MiaoshaUser user,
                                 @PathVariable("goodsId") String goodsId) {
        long gooIdsd = 0;
        try {
            gooIdsd = Long.parseLong(goodsId);
        } catch (Exception e) {
            Result.error(CodeMsg.MIAO_SHA_NOFIND);
        }
        //放入缓存减压
        GoodsVo good = redisService.get(GoodsKey.getGoodsDetail, goodsId + "", GoodsVo.class);
        if (good == null) {
            good = goodsService.getGoodsVoByGoodsId(gooIdsd);
            if (good.getStockCount() > 0) {
                //重新设置内存值
                log.info("重新设置商品内存false，商品ID:{}",goodsId);
                localOverMap.put(good.getId(), false);
            }
            redisService.set(GoodsKey.getGoodsDetail, goodsId + "", good);
        }

        GoodsDetailVo goodsDetailVo = new GoodsDetailVo();

        //model.addAttribute("user", user);
        //model.addAttribute("good", good);
        //状态
        int miaoshaStatus;
        //还有多久开始
        int remainSeconds;

        if (good == null) {
            return Result.error(CodeMsg.MIAO_SHA_NOFIND);
        }
        long startAt = good.getStartDate().getTime();
        long endAt = good.getEndDate().getTime();
        long now = System.currentTimeMillis();

        if (now < startAt) {
            //未开始
            miaoshaStatus = 0;
            remainSeconds = (int) ((startAt - now) / 1000);
        } else if (now > endAt) {
            //已结束
            miaoshaStatus = 2;
            remainSeconds = -1;
        } else {
            //秒杀进行中
            miaoshaStatus = 1;
            remainSeconds = 0;
        }

        //model.addAttribute("miaoshaStatus", miaoshaStatus);
        //model.addAttribute("remainSeconds", remainSeconds);
        goodsDetailVo.setGoodsVo(good);
        user.setLoginCount(null);
        goodsDetailVo.setUser(user);
        goodsDetailVo.setMiaoshaStatus(miaoshaStatus);
        goodsDetailVo.setRemainSeconds(remainSeconds);
        return Result.success(goodsDetailVo);
    }


    @ApiOperation(value = "请求秒杀")
    @PostMapping("/{path}")
    @ResponseBody
    public Result<Object> miaosha(MiaoshaUser user,
                                  @RequestParam("goodsId") long goodsId,
                                  @PathVariable("path") String path) {
        if (user == null) {
            return Result.error(CodeMsg.USER_NOT_LOGIN);
        }
        //验证path
        boolean checkpath = miaoshaService.checkPath(user, goodsId, path);
        if (!checkpath) {
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        /*优化代码*/
        log.info("商品ID:{},内存标记{}",goodsId,localOverMap.get(goodsId));
        //内存标记，减少Redis访问
        if (localOverMap.get(goodsId)) {
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //验证秒杀时间
        GoodsVo good = redisService.get(GoodsKey.getGoodsDetail, goodsId + "", GoodsVo.class);
        if(good == null){
            return Result.error(CodeMsg.MIAO_SHA_FLASH);
        }
        long startAt = good.getStartDate().getTime();
        long endAt = good.getEndDate().getTime();
        long now = System.currentTimeMillis();

        if (now < startAt) {
            //未开始
            return Result.error(CodeMsg.MIAO_SHA_START);
        } else if (now > endAt) {
            //已结束
           return Result.error(CodeMsg.MIAO_SHA_END);
        }
        //预减库存
        //Redis Decr 命令将 key 中储存的数字值减一。
        //如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 DECR 操作。
        //如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。
        //本操作的值限制在 64 位(bit)有符号数字表示之内。
        long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, "" + goodsId);
        if (stock < 0) {
            localOverMap.put(goodsId, true);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //判断是否已经秒杀到了
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if (miaoshaOrder != null) {
            //已经秒杀过
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        //入队
        MiaoshaMessage message = new MiaoshaMessage(user.getId(), goodsId);
        mqSend.sendMiaoshaMessage(message);
        //排队中
        return Result.success(0);
    }

    @ApiOperation(value = "请求秒杀,压测方法，不验证path")
    @PostMapping("/vfwcfesavc")
    @ResponseBody
    public Result<Object> test(MiaoshaUser user,
                                  @RequestParam("goodsId") long goodsId) {
        if (user == null) {
            return Result.error(CodeMsg.USER_NOT_LOGIN);
        }
        /*优化代码*/
        //内存标记，减少Redis访问
        if (localOverMap.get(goodsId)) {
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //预减库存
        //Redis Decr 命令将 key 中储存的数字值减一。
        //如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 DECR 操作。
        //如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。
        //本操作的值限制在 64 位(bit)有符号数字表示之内。
        long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, "" + goodsId);
        if (stock < 0) {
            localOverMap.put(goodsId, true);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //判断是否已经秒杀到了
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if (miaoshaOrder != null) {
            //已经秒杀过
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        //入队
        MiaoshaMessage message = new MiaoshaMessage(user.getId(), goodsId);
        mqSend.sendMiaoshaMessage(message);
        //排队中
        return Result.success(0);
    }

    /**
     * 秒杀成功返回订单ID orderId
     * -1 秒杀失败   0 排队中
     * @param user 用户
     * @param goodsId 商品ID
     * @return
     */
    @ApiOperation(value = "获取秒杀结果",
            notes = "成功返回订单ID orderId,-1 秒杀失败,0 排队中")
    @GetMapping("/result")
    @ResponseBody
    public Result<Object> miaoshaResult(MiaoshaUser user,
                                        @RequestParam("goodsId") long goodsId) {
        //秒杀结果
        if (user == null) {
            return Result.error(CodeMsg.USER_NOT_LOGIN);
        }
        return Result.success(miaoshaService.getMiaoshaResult(user.getId(), goodsId)+"");
    }


    @ApiOperation(value = "获取秒杀地址")
    @AccessLimit(seconds = 4, maxCount = 5, needLogin = true)
    @GetMapping("/path")
    @ResponseBody
    public Result<Object> path(MiaoshaUser user,
                               @RequestParam("goodsId") long goodsId,
                               @RequestParam(value = "verifyCode", defaultValue = "0") int verifyCode) {
        if (!kaptchaService.checkVerifyCode(user, goodsId, verifyCode)) {
            return Result.error(CodeMsg.VERIFY_ERR);
        }
        String path = miaoshaService.createMiaoshaPath(user, goodsId);
        HashMap<String, String> map = new HashMap<>();
        map.put("hash", path);
        return Result.success(map);
    }

}
