package com.hcyacg.pixiv.queue;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hcyacg.pixiv.entity.Token;
import com.hcyacg.pixiv.mapper.TokenMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Author Nekoer
 * @Date 2020/9/2 21:36
 * @Desc
 */
//@Component
public class TokenQueue {

    @Autowired
    private TokenMapper tokenMapper;


    //@RabbitListener(queues = "tokenTotal")
    public void receive(Object token) {
        try {
            if (tokenMapper.selectCount(new QueryWrapper<Token>().eq("token", String.valueOf(token))) > 0) {
                Token tokenData = tokenMapper.selectOne(new QueryWrapper<Token>().eq("token", String.valueOf(token)));
                tokenData.setTotal(tokenData.getTotal() == 0 ? 1 : tokenData.getTotal() + 1);
                if (tokenMapper.updateById(tokenData) < 1) {
                    throw new RuntimeException("添加失败");
                }
                System.out.println("用户id" + tokenData.getAccountId() + "添加次数");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
