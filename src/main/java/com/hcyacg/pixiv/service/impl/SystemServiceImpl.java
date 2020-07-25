package com.hcyacg.pixiv.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.hcyacg.pixiv.constant.AppConstant;
import com.hcyacg.pixiv.dto.Result;
import com.hcyacg.pixiv.entity.Illust;
import com.hcyacg.pixiv.entity.Log;
import com.hcyacg.pixiv.mapper.IllustMapper;
import com.hcyacg.pixiv.mapper.LogMapper;
import com.hcyacg.pixiv.service.IllustService;
import com.hcyacg.pixiv.service.PublicService;
import com.hcyacg.pixiv.service.SystemService;
import com.hcyacg.pixiv.task.PicTask;
import com.hcyacg.pixiv.utils.HttpUtils;
import com.hcyacg.pixiv.utils.RedisUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

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
    private IllustMapper illustMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private IllustService illustService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private PublicService publicRankingService;
    @Autowired
    private HttpServletResponse res;
    @Autowired
    private LogMapper logMapper;
    @Autowired
    private HttpUtils httpUtils;

    @Override
    public Result runCrawlData(String time) {
        String T2 = null;
        try{

            if(StringUtils.isBlank(time)){
                Date dd = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dd);
                calendar.add(Calendar.DAY_OF_MONTH, -2);
                T2 = AppConstant.SDF.format(calendar.getTime());
            }else {
                T2 = time;
            }


            //参数
            List<String> rankingType = Arrays.asList("illust", "all", "manga", "ugoira");
            List<String> mode = new ArrayList<>();
            List<Integer> list = new ArrayList<>();

            //初始化爬取数量
            redisUtils.set(AppConstant.PIC_INFO_NUMBER,0);

            for (int w = 0; w < rankingType.size(); w++) {

                switch (w) {
                    case 0:
                        mode = Arrays.asList("daily", "weekly", "monthly", "male", "female", "daily_r18");
                        break;
                    case 1:
                        mode = Arrays.asList("daily", "weekly", "monthly", "daily_r18");
                        break;
                    case 2:
                        mode = Arrays.asList("daily", "weekly", "monthly", "daily_r18");
                        break;
                    case 3:
                        mode = Arrays.asList("daily", "weekly");
                        break;
                }

                for (String s : mode) {

                    String page = "1";
                    String perPage = "30";

                    String data = publicRankingService.ranking(rankingType.get(w), s, page, perPage, T2,true);
                    JSONObject jsonObject = JSONObject.parseObject(data);
                    String pagination = jsonObject.getString("pagination");
                    jsonObject = JSONObject.parseObject(pagination);
                    if (null != jsonObject) {
                        Integer pages = jsonObject.getInteger("pages");

                        for (int i = 0; i < pages; i++) {
                            data = publicRankingService.ranking(rankingType.get(w), s, String.valueOf(Integer.parseInt(page) + i), perPage, T2,true);
                            jsonObject = JSONObject.parseObject(data);
                            String response = jsonObject.getString("response");
                            JSONArray jsonArray = JSONArray.parseArray(response);
                            if (CollectionUtils.isNotEmpty(jsonArray)) {
                                jsonObject = JSONObject.parseObject(String.valueOf(jsonArray.get(0)));
                                jsonArray = JSONArray.parseArray(jsonObject.getString("works"));
                                for (int j = 0; j < jsonArray.size(); j++) {
                                    jsonObject = JSONObject.parseObject(jsonArray.getString(j));
                                    String work = jsonObject.getString("work");
                                    jsonObject = JSONObject.parseObject(work);

                                    if (illustMapper.selectCount(new QueryWrapper<Illust>().eq("illust", jsonObject.getInteger("id"))) < 1) {
                                        list.add(jsonObject.getInteger("id"));
                                    }

                                }
                            }


                        }
                    }

                }


            }

            //list = new ArrayList<Integer>(new LinkedHashSet<>(new TreeSet<Integer>(list)));

            //去重 保持加入顺序
            Set<Integer> set = new HashSet<>();
            List<Integer> newList = new ArrayList<>();
            for (Integer str : list) {
                if (set.add(str)) {
                    newList.add(str);
                }
            }
            list.clear();
            list.addAll(newList);

            System.out.println(list.size());

            //数据总量加入redis
            redisUtils.incr(AppConstant.PIC_INFO_NUMBER,list.size());


            list.forEach(i -> {
                String messageId = UUID.randomUUID() + "";
                Message message = MessageBuilder.withBody(String.valueOf(i).getBytes())
                        .setContentType(MessageProperties.CONTENT_TYPE_JSON).setContentEncoding("utf-8")
                        .setMessageId(messageId)
                        .build();
                redisUtils.set(messageId,i,5 * 60 * 60L);
                rabbitTemplate.convertAndSend("pic", message);
            });

            list.clear();

            return httpUtils.setBuild(res,new Result(201,"启动成功",null,null));
        }catch (Exception e){
            e.printStackTrace();
            return httpUtils.setBuild(res,new Result(500,"启动失败",null,e.getMessage()));
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
