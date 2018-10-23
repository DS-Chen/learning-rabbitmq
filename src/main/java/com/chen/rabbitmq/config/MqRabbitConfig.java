package com.chen.rabbitmq.config;

import com.chen.rabbitmq.listener.MqRabbitListener;
import com.rabbitmq.client.Channel;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.IOException;

import javax.annotation.Resource;

/**
 * @author chendongsuo
 * @create 2018-09-19 13:30
 * @email dongsuo.chen@nvr-china.com
 * @description mqConfig
 */
@Configuration
public class MqRabbitConfig {

    @Resource
    private Environment environment;

    @Autowired
    private MqRabbitListener mqRabbitListener;

    @Bean
    public ConnectionFactory connectionFactory() throws IOException {
        /**创建连接工厂(带有缓存模式)*/
        CachingConnectionFactory cachingConnectionFactory=new CachingConnectionFactory();
        /**
         * 主机
         * 端口号
         * 用户名
         * 密码
         * 缓存信道大小
         */
        cachingConnectionFactory.setHost(getRabbitmqHost());
        cachingConnectionFactory.setPort(getRabbitmqPort());
        cachingConnectionFactory.setUsername(getRabbitmqUserName());
        cachingConnectionFactory.setPassword(getRabbitmqPassword());
        cachingConnectionFactory.setChannelCacheSize(25);
        /**创建连接*/
        Connection connection=cachingConnectionFactory.createConnection();
        /**创建信道*/
        Channel channel=connection.createChannel(true);
        /**
         * 声明交换机
         * 参数一：交换机名称，
         * 参数二：type交换机类型  direct/topic/fanout
         * 参数三：是否持久化，
         * 参数四：是否自动删除交换机，
         * 参数五：其他参数
         */
        //channel.exchangeDeclare(getRabbitmqExchange(),"direct");
        channel.exchangeDeclare(getMqttExchange(),"topic",true,false,null);
        /**
         * 声明队列
         * 参数一：队列名称，
         * 参数二：是否持久化，
         * 参数三：是否独占模式，
         * 参数四：消费者断开连接时是否删除队列，
         * 参数五：消息其他参数
         */
        channel.queueDeclare(getChenQueue(),true,false,false,null);
        /**
         * 将交换机与队列通过路由键绑定
         * 参数一：队列名
         * 参数二：交换机
         * 参数三：路由键
         */
        channel.queueBind(getChenQueue(),getMqttExchange(),"CHEN.Harvesters.*");
        return cachingConnectionFactory;
    }

    @Bean
    public SimpleMessageListenerContainer messageListenerContainer() throws IOException {
        /**创建监听容器*/
        SimpleMessageListenerContainer container= new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory());
        /**监听队列*/
        container.setQueueNames(getChenQueue());
        /**设置多个并发消费者一起消费*/
        container.setConcurrentConsumers(10);
        container.setMessageListener(mqRabbitListener);
        return container;
    }

    /**convertAndSend(String exchange, String routingKey, Object object)*/
    //RabbitmqTemplate 和AmqpTemplate的区别和联系  RabbitMQ是AMQP（高级消息队列协议）的标准实现

    public String getRabbitmqHost(){
        return environment.getRequiredProperty("spring.rabbitmq.host");
    }
    public int getRabbitmqPort(){
        environment.getProperty("sssss",Integer.TYPE);
        return environment.getRequiredProperty("spring.rabbitmq.port",Integer.TYPE);
    }
    public String getRabbitmqUserName(){
        return environment.getRequiredProperty("spring.rabbitmq.username");
    }
    public String getRabbitmqPassword(){
        return environment.getRequiredProperty("spring.rabbitmq.password");
    }
    public String getRabbitmqExchange(){
        return environment.getRequiredProperty("spring.rabbitmq.template.exchange");
    }
    public String getMqttExchange(){
        return environment.getRequiredProperty("chen.mqtt.exchange");
    }
    public String getChenQueue(){
        return environment.getRequiredProperty("mqtt.chen.queue");
    }

}
