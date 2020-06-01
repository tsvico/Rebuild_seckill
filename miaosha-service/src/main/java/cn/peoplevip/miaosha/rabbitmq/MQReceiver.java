package cn.peoplevip.miaosha.rabbitmq;

import cn.peoplevip.common.api.GoodsService;
import cn.peoplevip.common.api.MiaoshaService;
import cn.peoplevip.common.api.OrderService;
import cn.peoplevip.common.api.RedisService;
import cn.peoplevip.common.domain.MiaoshaOrder;
import cn.peoplevip.common.vo.GoodsVo;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/3/4 16:02
 * 接收者
 */
@Service
public class MQReceiver {

    private static Logger log = LoggerFactory.getLogger(MQReceiver.class);


	@Reference
	GoodsService goodsService;

	@Reference
	OrderService orderService;

	@Autowired
	MiaoshaService miaoshaService;

	@Autowired
	RedisService redisService;


    @RabbitListener(queues = MQConfig.MIAOSHA_QUEUE)
    public void receive(String message) {
        log.info("receive message:" + message);
        MiaoshaMessage mm = redisService.strToBean(message, MiaoshaMessage.class);
        if (mm.getUserId()==0){
        	mm.setUserId(mm.getUser().getId());
		}
        long userId = mm.getUserId();
        long goodsId = mm.getGoodsId();

		GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
		if (goods == null) {
			return;
		}
		int stock = goods.getStockCount();
		//判断库存
		if (stock <= 0) {
			miaoshaService.setGoodsOver(goodsId);
			return;
		}
		//判断是否已经秒杀到了
		MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);
		if (miaoshaOrder != null) {
			//已经秒杀过
			return;
		}
		//事务  1.减库存 2.下订单 3.写入秒杀订单
		//OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
		//使用存储过程
		miaoshaService.executeMiaosha(userId, goods);
    }

}
