package com.hcyacg.pixiv.task;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.hcyacg.pixiv.constant.AppConstant;
import com.hcyacg.pixiv.entity.Illust;
import com.hcyacg.pixiv.mapper.IllustMapper;
import com.hcyacg.pixiv.service.IllustService;
import com.hcyacg.pixiv.service.PublicService;
import com.hcyacg.pixiv.utils.RedisUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created: 黄智文
 * Desc: 定时爬取图片数据存入数据库
 * Date: 2020/6/25 11:50
 */
@Component
public class PicTask {

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


    /**
     * rankingType [all, illust, manga, ugoira]
     * mode [daily, weekly, monthly, rookie, original, male, female, daily_r18, weekly_r18, male_r18, female_r18, r18g]
     * for 'illust' & 'manga': [daily, weekly, monthly, rookie, daily_r18, weekly_r18, r18g]
     * for 'ugoira': [daily, weekly, daily_r18, weekly_r18],
     * page [1-n]
     * date '2015-04-01' (仅过去排行榜)
     */
    @Scheduled(cron = "0 1 0 * * ?")
    public void CrawlMorningData() {
        Date dd = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dd);
        calendar.add(Calendar.DAY_OF_MONTH, -2);
        String T2 = AppConstant.SDF.format(calendar.getTime());

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
    }
}
