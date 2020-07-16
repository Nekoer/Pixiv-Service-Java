package com.hcyacg.pixiv.utils;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created: 黄智文
 * Desc:
 * Date: 2020/6/30 15:49
 */
@Component
public class RabbitUtils {

    @Resource
    private RabbitTemplate rabbitTemplate;

    public RabbitUtils(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }


    public void convertAndSend(String routingKey, Object object){
        rabbitTemplate.convertAndSend(routingKey,object);
    }
}
