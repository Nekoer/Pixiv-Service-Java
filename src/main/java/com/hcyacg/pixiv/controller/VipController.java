package com.hcyacg.pixiv.controller;

import com.hcyacg.pixiv.dto.Result;
import com.hcyacg.pixiv.service.VipService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Nekoer
 * @Desc:
 * @Date: 2020/7/14 14:47
 */
@Api(value = "会员业务接口",tags = "处理会员相关操作")
@RestController
@RequestMapping("vips")
public class VipController {

    @Autowired
    private VipService vipService;

    @ApiOperation(value = "获得会员默认数据",notes = "获得会员默认数据")
    @RequestMapping(value = "vips", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getVip(){
        return vipService.getVip();
    }

}
