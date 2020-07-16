package com.hcyacg.pixiv.controller;

import com.hcyacg.pixiv.dto.Result;
import com.hcyacg.pixiv.service.VipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created: 黄智文
 * Desc:
 * Date: 2020/7/14 14:47
 */
@RestController
@RequestMapping("vips")
public class VipController {

    @Autowired
    private VipService vipService;

    @RequestMapping(value = "vips", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getVip(){
        return vipService.getVip();
    }

}
