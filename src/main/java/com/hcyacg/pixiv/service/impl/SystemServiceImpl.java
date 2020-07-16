package com.hcyacg.pixiv.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hcyacg.pixiv.dto.Result;
import com.hcyacg.pixiv.entity.Log;
import com.hcyacg.pixiv.mapper.LogMapper;
import com.hcyacg.pixiv.service.SystemService;
import com.hcyacg.pixiv.task.PicTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created: 黄智文
 * Desc:
 * Date: 2020/7/4 18:01
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SystemServiceImpl implements SystemService {

    @Autowired
    private PicTask picTask;

    @Autowired
    private LogMapper logMapper;

    @Override
    public Result runCrawlData() {
        try{
            picTask.CrawlMorningData();
            return new Result(201,"启动成功",null,null);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(500,"启动失败",null,e.getMessage());
        }
    }

    @Override
    public Result hasRole() {
        return new Result(201,"获取成功",true,null);
    }

    @Override
    public Result log() {
        try{

            List<Log> logs = logMapper.selectList(null);
            Map<String,Object> map = new HashMap<>();
            logs.forEach(log -> map.put(String.valueOf(log.getDay().replace("-","")),log.getFrequency()));
            return new Result(201,"获取成功",map,null);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(500,"获取失败",null,e.getMessage());
        }
    }
}
