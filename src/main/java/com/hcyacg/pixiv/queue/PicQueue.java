package com.hcyacg.pixiv.queue;

import com.hcyacg.pixiv.constant.AppConstant;
import com.hcyacg.pixiv.service.IllustService;
import com.hcyacg.pixiv.utils.NumberUtils;
import com.hcyacg.pixiv.utils.RedisUtils;
import org.apache.tomcat.jni.Buffer;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created: 黄智文
 * Desc: 图片爬取消息队列
 * Date: 2020/6/27 11:17
 */
@Component
public class PicQueue {

    @Autowired
    private IllustService illustService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private NumberUtils numberUtils;

    @RabbitListener(queues = "pic")
    public void receive(Message message) {
        try {
            String messageId = message.getMessageProperties().getMessageId();
            if (redisUtils.hasKey(messageId)) {
                String illust = String.valueOf(redisUtils.get(messageId));
                redisUtils.del(messageId);

                if (redisUtils.hasKey(AppConstant.PIC_INFO_NUMBER)) {
                    redisUtils.decr(AppConstant.PIC_INFO_NUMBER, 1);
                }


                try {
                    //使用读取图片超过50次
                    if (redisUtils.hasKey(AppConstant.URl_LOOK_NUMBER_REDIS) && Integer.parseInt(String.valueOf(redisUtils.get(AppConstant.URl_LOOK_NUMBER_REDIS))) >= 50) {
                        redisUtils.del(AppConstant.URl_LOOK_NUMBER_REDIS);
                        Thread.sleep(60000);

                    } else if (Integer.parseInt(String.valueOf(redisUtils.get(AppConstant.PIC_INFO_NUMBER))) % 50 == 0) {
                        Thread.sleep(60000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                illustService.detail(illust,null);


            }
        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }

}
