package com.hcyacg.pixiv.queue;

import com.hcyacg.pixiv.utils.EmailUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Author: Nekoer
 * @Desc: 邮箱消息队列
 * @Date: 2020/6/27 11:15
 */
@Component
public class EmailQueue {

    @Autowired
    private EmailUtils emailUtils;

    @RabbitListener(queues = "code")
    public void receive(Map<String, Object> map) {
        try {
            emailUtils.sendHtmlCodeMail("code@acgmx.com", String.valueOf(map.get("email")), "验证码", String.valueOf(map.get("code")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
