package com.hcyacg.pixiv.controller;

import com.hcyacg.pixiv.dto.Result;
import com.hcyacg.pixiv.service.SystemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: Nekoer
 * @Desc:
 * @Date: 2020/7/4 18:04
 */
@Api(value = "系统业务接口",tags = "处理系统相关操作")
@RestController
@RequestMapping("systems")
public class SystemController {

    @Autowired
    private SystemService systemService;

    @ApiOperation(value = "爬虫系统",notes = "爬虫系统")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "time", value = "日期", required = true, paramType = "query", dataType = "string"),
    })
    @RequestMapping(value = "run", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result run(@RequestParam(value = "time",required = false) String time){
        return systemService.runCrawlData(time);
    }

    @ApiOperation(value = "判断是否是管理员",notes = "判断是否是管理员")
    @RequestMapping(value = "hasRole", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result hasRole(){
        return systemService.hasRole();
    }

    @ApiOperation(value = "访问信息",notes = "访问信息")
    @RequestMapping(value = "logs", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result logs(){
        return systemService.log();
    }
}
