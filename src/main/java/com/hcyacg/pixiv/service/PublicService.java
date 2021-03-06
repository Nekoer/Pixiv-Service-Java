package com.hcyacg.pixiv.service;

import com.hcyacg.pixiv.dto.Result;

import java.util.List;
import java.util.Map;

/**
 * @Author: Nekoer
 * @Desc: 公共接口
 * @Date: 2020/5/13 20:19
 */
public interface PublicService {

    /**
     * 排行榜/过去排行榜
     * @param rankingType [all, illust, manga, ugoira]
     * @param mode [daily, weekly, monthly, rookie, original, male, female, daily_r18, weekly_r18, male_r18, female_r18, r18g]
     *             for 'illust' & 'manga': [daily, weekly, monthly, rookie, daily_r18, weekly_r18, r18g]
     *             for 'ugoira': [daily, weekly, daily_r18, weekly_r18],
     * @param page [1-n]
     * @param date '2015-04-01' (仅过去排行榜)
     * @param delCache 是否删除缓存
     * @return 返回json数据
     */
    String ranking(String rankingType, String mode, String page, String perPage , String date,Boolean delCache);


    /**
     * 搜索 (Search)
     *  search_target - 搜索类型
     *  partial_match_for_tags  - 标签部分一致
     *  exact_match_for_tags    - 标签完全一致
     *  title_and_caption       - 标题说明文
     *  sort: [date_desc, date_asc]
     *  duration: [within_last_day, within_last_week, within_last_month]
     * @return
     */
    String search(String word,String offset);


    /**
     * 标签
     * @return 返回数据
     */
    String tags();

    /**
     *
     * @return 返回数据
     */
    Result amazingPic(String token);

    /**
     * 搜索作者
     * @param word 作者名
     * @return 返回作者信息
     */
    String searchUser(String word,Integer offset);

    /**
     * 根据ID查找作者
     * @param userId 作者id
     * @return 返回作者信息
     */
    String userDetails(String userId);
    /**
     * 根据 ID查找作者的作品
     * @param userId 作者id
     * @return 返回作者信息
     */
    String userIllusts(String userId,Integer offset);
}
