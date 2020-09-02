package com.hcyacg.pixiv.controller;

import com.hcyacg.pixiv.dto.Result;
import com.hcyacg.pixiv.service.PublicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: Nekoer
 * @Desc: 公共接口
 * @Date: 2020/5/13 21:06
 */
@RestController
@RequestMapping("public")
public class PublicController {

    @Autowired
    private PublicService publicService;

    /**
     * @api {GET} /public/ranking 排行榜
     * @apiVersion 1.0.0
     * @apiGroup 公共接口
     * @apiName ranking
     * @apiDescription 排行榜
     * @apiParam (请求参数) {String} ranking_type 数据类型：all, illust, manga, ugoira
     * @apiParam (请求参数) {String} mode 类型范围：[daily, weekly, monthly, rookie, original, male, female, daily_r18, weekly_r18, male_r18, female_r18, r18g]
     *          参数为 'illust' & 'manga': [daily, weekly, monthly, rookie, daily_r18, weekly_r18, r18g]
     *          参数为 'ugoira': [daily, weekly, daily_r18, weekly_r18]
     * @apiParam (请求参数) {String} page 页码
     * @apiParam (请求参数) {String} per_page 当页数量
     * @apiParam (请求参数) {String} date 日期：2020-04-13
     * @apiParamExample 请求参数示例
     * ranking_type=illust&mode=daily&date=2020-04-13&per_page=50&page=1
     * @apiSuccess (响应结果) {String} response
     * @apiSuccessExample 响应结果示例
     * "太长了不显示..."
     */
    @RequestMapping(value = "ranking", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String ranking(@RequestParam(value = "ranking_type",required = false) String rankingType, @RequestParam(value = "mode",required = false) String mode, @RequestParam(value = "page",required = false) String page, @RequestParam(value = "per_page",required = false) String perPage, @RequestParam(value = "date",required = false) String date)   {
        return publicService.ranking(rankingType, mode, page, perPage, date,false);
    }

    @RequestMapping(value = "search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String search(@RequestParam(value = "q") String q,@RequestParam(value = "offset")String offset){
        return publicService.search(q,offset);
    }

    @RequestMapping(value = "tags", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String tags(){
        return publicService.tags();
    }

    @RequestMapping(value = "setu", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result amazingPic(@RequestHeader(value = "token") String token){
        return publicService.amazingPic(token);
    }
}
