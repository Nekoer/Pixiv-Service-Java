package com.hcyacg.pixiv.queue;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hcyacg.pixiv.constant.AppConstant;
import com.hcyacg.pixiv.entity.Log;
import com.hcyacg.pixiv.mapper.LogMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author: Nekoer
 * @Desc: 记录访问次数
 * @Date: 2020/6/30 13:21
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class LogQueue {

    @Autowired
    private LogMapper logMapper;

    @RabbitListener(queues = "log")
    public void receive(Object number) {
        try {
            //添加访问次数
            Calendar calendar = Calendar.getInstance();
            Calendar.getInstance().setTime(new Date());

            if (logMapper.selectList(new QueryWrapper<Log>().eq("day", AppConstant.SDF.format(calendar.getTime()))).size() < 1) {
                Log log = new Log();
                log.setDay(AppConstant.SDF.format(calendar.getTime()));
                log.setFrequency(1);
                logMapper.insert(log);
            } else {
                List<Log> log = logMapper.selectList(new QueryWrapper<Log>().eq("day", AppConstant.SDF.format(calendar.getTime())));
                log.get(0).setFrequency(log.get(0).getFrequency() + 1);
                logMapper.updateById(log.get(0));
                for (int i = 1; i < log.size(); i++) {
                    logMapper.deleteById(log.get(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
