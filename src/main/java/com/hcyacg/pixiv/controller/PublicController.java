package com.hcyacg.pixiv.controller;

import com.hcyacg.pixiv.dto.Result;
import com.hcyacg.pixiv.service.PublicService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: Nekoer
 * @Desc: 公共接口
 * @Date: 2020/5/13 21:06
 */
@Api(value = "公共业务接口",tags = "处理公共相关操作")
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
    @ApiOperation(value = "公共排行榜",notes = "公共排行榜")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ranking_type", value = "类型", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "mode", value = "模式", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "per_page", value = "当页数量", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "date", value = "日期", required = true, paramType = "query", dataType = "string"),
    })
    @RequestMapping(value = "ranking", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String ranking(@RequestParam(value = "ranking_type",required = false) String rankingType, @RequestParam(value = "mode",required = false) String mode, @RequestParam(value = "page",required = false) String page, @RequestParam(value = "per_page",required = false) String perPage, @RequestParam(value = "date",required = false) String date)   {
        return publicService.ranking(rankingType, mode, page, perPage, date,false);
    }

    @ApiOperation(value = "搜索图片",notes = "搜索图片")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "q", value = "关键词", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "offset", value = "当页数量", required = true, paramType = "query", dataType = "string"),
    })
    @RequestMapping(value = "search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String search(@RequestParam(value = "q") String q,@RequestParam(value = "offset")String offset){
        return publicService.search(q,offset);
    }

    @ApiOperation(value = "标签",notes = "标签")
    @RequestMapping(value = "tags", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String tags(){
        return publicService.tags();
    }


    @ApiOperation(value = "涩图",notes = "涩图")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "开发者token", required = true, paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "setu", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result amazingPic(@RequestHeader(value = "token") String token){
        return publicService.amazingPic(token);
    }


    @ApiOperation(value = "搜索作者",notes = "搜索作者")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "word", value = "关键词", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "offset", value = "当页数量", required = true, paramType = "query", dataType = "int",defaultValue = "30",example = "30"),
    })
    @RequestMapping(value = "search/users", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String searchUser(@RequestParam(value = "word") String word,@RequestParam(value = "offset") Integer offset){
        return publicService.searchUser(word,offset);
    }

    @ApiOperation(value = "搜索作者的详情",notes = "搜索作者的详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "作者id", required = true, paramType = "query", dataType = "string"),
    })
    @RequestMapping(value = "search/users/details", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String userDetails(@RequestParam(value = "id") String id){
        return publicService.userDetails(id);
    }


    @ApiOperation(value = "搜索作者的插画",notes = "搜索作者的插画")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "作者id", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "offset", value = "当页数量", required = true, paramType = "query", dataType = "int",defaultValue = "30",example = "30"),
    })
    @RequestMapping(value = "search/users/illusts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String userIllusts(@RequestParam(value = "id") String id,@RequestParam(value = "offset") Integer offset){
        return publicService.userIllusts(id,offset);
    }
}
