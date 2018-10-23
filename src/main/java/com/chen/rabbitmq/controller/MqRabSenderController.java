package com.chen.rabbitmq.controller;

import com.chen.rabbitmq.config.MqRabbitConfig;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chendongsuo
 * @create 2018-09-20 9:07
 * @email dongsuo.chen@nvr-china.com
 * @description mqController
 */
@RestController
@RequestMapping(path = "/mqRb")
public class MqRabSenderController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private MqRabbitConfig mqRabbitConfig;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = "/msg",method = RequestMethod.GET,produces = {"application/json"})
    public void senderMsg(){

//      交换机
        final String mqExchange=mqRabbitConfig.getMqttExchange();
        final MessageProperties properties= new MessageProperties();

        String msg="这是我的第一个 rabbitmq 程序";

        properties.setHeader("NO1","ONE");

        //序列化
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        Message message=new Message(msg.toString().getBytes(),properties);
        rabbitTemplate.convertAndSend(mqExchange,"CHEN.Harvesters.msg",message);
    }
}