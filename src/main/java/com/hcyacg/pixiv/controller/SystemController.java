package com.hcyacg.pixiv.controller;

import com.hcyacg.pixiv.dto.Result;
import com.hcyacg.pixiv.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * Created: 黄智文
 * Desc:
 * Date: 2020/7/4 18:04
 */
@RestController
@RequestMapping("systems")
public class SystemController {

    @Autowired
    private SystemService systemService;

    @RequestMapping(value = "run", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result run(){
        return systemService.runCrawlData();
    }

    @RequestMapping(value = "hasRole", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result hasRole(){
        return systemService.hasRole();
    }

    @RequestMapping(value = "logs", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result logs(){
        return systemService.log();
    }
}
