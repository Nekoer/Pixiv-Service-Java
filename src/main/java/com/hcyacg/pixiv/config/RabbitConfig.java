package com.hcyacg.pixiv.config;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.SerializerMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @Author: Nekoer
 * @Desc:
 * @Date: 2020/6/15 14:03
 */

@Configuration
public class RabbitConfig {

    @Bean
    public Queue CodeDirectQueue() {
        return new Queue("code",true);
    }

    @Bean
    DirectExchange CodeDirectExchange() {
        return new DirectExchange("code",true,false);
    }

    @Bean
    Binding bindingCodeDirect() {
        return BindingBuilder.bind(CodeDirectQueue()).to(CodeDirectExchange()).with("code");
    }

    @Bean
    public Queue picDirectQueue() {
        return new Queue("pic",true);
    }

    @Bean
    DirectExchange picDirectExchange() {
        return new DirectExchange("pic",true,false);
    }

    @Bean
    Binding bindingPicDirect() {
        return BindingBuilder.bind(picDirectQueue()).to(picDirectExchange()).with("pic");
    }



    @Bean
    public Queue logDirectQueue() {
        return new Queue("log",true);
    }

    @Bean
    DirectExchange logDirectExchange() {
        return new DirectExchange("log",true,false);
    }

    @Bean
    Binding bindingLogDirect() {
        return BindingBuilder.bind(picDirectQueue()).to(picDirectExchange()).with("log");
    }


    @Bean
    public Queue avatarDirectQueue() {
        return new Queue("avatar",true);
    }

    @Bean
    DirectExchange avatarDirectExchange() {
        return new DirectExchange("avatar",true,false);
    }

    @Bean
    Binding bindingAvatarDirect() {
        return BindingBuilder.bind(picDirectQueue()).to(picDirectExchange()).with("avatar");
    }

    @Bean
    DirectExchange lonelyDirectExchange() {
        return new DirectExchange("lonelyDirectExchange");
    }

    @Bean("customContainerFactory")
    public SimpleRabbitListenerContainerFactory containerFactory(SimpleRabbitListenerContainerFactoryConfigurer configurer, ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConcurrentConsumers(2);  //设置线程数
        factory.setMaxConcurrentConsumers(3); //最大线程数
        configurer.configure(factory, connectionFactory);
        return factory;
    }

}
