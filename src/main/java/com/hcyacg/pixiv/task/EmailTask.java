package com.hcyacg.pixiv.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hcyacg.pixiv.constant.AppConstant;
import com.hcyacg.pixiv.entity.Log;
import com.hcyacg.pixiv.mapper.LogMapper;
import com.hcyacg.pixiv.utils.EmailUtils;
import com.hcyacg.pixiv.utils.RedisUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @Author: Nekoer
 * @Desc: 每天凌晨定时发送邮箱，关于接口调用的信息
 * @Date: 2020/5/17 10:47
 */
@Component
public class EmailTask {

    @Autowired
    private EmailUtils emailUtils;
    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private LogMapper logMapper;

    @Scheduled(cron = "0 0 0 * * ?")
    public void sendEmail() throws Exception {
        //获取当前的前一天
        // 0/5 * * * * ?
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        Log log = new Log();
        log.setDay(AppConstant.SDF.format(calendar.getTime()));
        log.setFrequency(0);
        logMapper.insert(log);


        calendar.add(Calendar.DAY_OF_MONTH, -15);

        StringBuilder sb = new StringBuilder();


        for (int i = 0; i < 14; i++) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            List<Log> logs = logMapper.selectList(new QueryWrapper<Log>().eq("day", AppConstant.SDF.format(calendar.getTime())));
            sb.append(AppConstant.SDF.format(calendar.getTime())).append("的调用次数").append(logs.size() > 0 ?  logs.get(0).getFrequency() : 0).append("\r\n");
            if (i == 0){
                logMapper.delete(new QueryWrapper<Log>().eq("day",AppConstant.SDF.format(calendar.getTime())));
            }
        }

        emailUtils.sendEmail("code@acgmx.com", "243462032@qq.com", "过去7天接口调用信息", String.valueOf(sb));
    }


}
