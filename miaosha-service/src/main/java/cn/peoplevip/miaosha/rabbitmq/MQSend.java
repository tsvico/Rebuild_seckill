package cn.peoplevip.miaosha.rabbitmq;

import cn.peoplevip.common.api.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author tsvico
 * @email tsxygwj@gmail.com
 * @time 2020/3/4 16:01
 * 发送者
 */
@Service
public class MQSend {

    private static final Logger log = LoggerFactory.getLogger(MQSend.class);

	@Autowired
    RedisService redisService;
    @Autowired
    private AmqpTemplate amqpTemplate;

	public void sendMiaoshaMessage(MiaoshaMessage message) {
		String msg = redisService.beanToString(message);
		log.info("send message:" + msg);
		amqpTemplate.convertAndSend(MQConfig.MIAOSHA_QUEUE, msg);
	}

//	public void send(Object message) {
//		String msg = RedisService.beanToString(message);
//		log.info("send message:"+msg);
//		amqpTemplate.convertAndSend(MQConfig.QUEUE, msg);
//	}
//
//	public void sendTopic(Object message) {
//		String msg = RedisService.beanToString(message);
//		log.info("send topic message:"+msg);
//		amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key1", msg+"1");
//		amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key2", msg+"2");
//	}
//
//	public void sendFanout(Object message) {
//		String msg = RedisService.beanToString(message);
//		log.info("send fanout message:"+msg);
//		amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE, "", msg);
//	}
//
//	public void sendHeader(Object message) {
//		String msg = RedisService.beanToString(message);
//		log.info("send fanout message:"+msg);
//		MessageProperties properties = new MessageProperties();
//		properties.setHeader("header1", "value1");
//		properties.setHeader("header2", "value2");
//		Message obj = new Message(msg.getBytes(), properties);
//		amqpTemplate.convertAndSend(MQConfig.HEADERS_EXCHANGE, "", obj);
//	}


}
