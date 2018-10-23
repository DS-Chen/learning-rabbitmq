package com.chen.rabbitmq.listener;

import com.chen.rabbitmq.config.MqRabbitConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

/**
 * @author chendongsuo
 * @create 2018-09-20 13:21
 * @email dongsuo.chen@nvr-china.com
 * @description mqListener
 */
@Component
public class MqRabbitListener implements ChannelAwareMessageListener {

    private static final Logger logger= LoggerFactory.getLogger(MqRabbitListener.class);
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        if (message ==null ||message.getBody()==null || message.getBody().length<=0){
            logger.warn("message is null");
        }
        processMessage(message);
    }

    @RabbitHandler
    private void processMessage(Message message) throws UnsupportedEncodingException {
        String topicKey=message.getMessageProperties().getReceivedRoutingKey();

        //将byte转换成UTF-8的字符串
        String msg=new String(message.getBody(),"UTF-8");
        logger.info("message data routingKey == [{}]",topicKey);
        logger.info("message header NO1== [{}]",message.getMessageProperties().getHeaders().get("NO1"));
        logger.info("message data body ==[{}]",msg);
    }
}
