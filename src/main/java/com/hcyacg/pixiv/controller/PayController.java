package com.hcyacg.pixiv.controller;

import com.hcyacg.pixiv.dto.Result;
import com.hcyacg.pixiv.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * Created: 黄智文
 * Desc:
 * Date: 2020/7/13 12:59
 */
@RestController
@RequestMapping("pays")
public class PayController {
    @Autowired
    private PayService payService;

    @Autowired
    private HttpServletRequest request;

    @RequestMapping(value = "pays", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result createUrl(@RequestHeader(value = "authorization",required = false) String authorization,@RequestParam(value = "type") Integer type, @RequestParam(value = "vipId") Integer vipId,@RequestParam(value = "vipPackAge")Integer vipPackAge){
        return payService.createUrl(authorization,type,vipId,vipPackAge);
    }

    @RequestMapping(value = "pays", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String notifyUrl(){
        return payService.notifyUrl(request);
    }
}
