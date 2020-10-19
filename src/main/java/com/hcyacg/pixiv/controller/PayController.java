package com.hcyacg.pixiv.controller;

import com.alipay.api.AlipayApiException;
import com.hcyacg.pixiv.dto.Result;
import com.hcyacg.pixiv.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * @Author: Nekoer
 * @Desc:
 * @Date: 2020/7/13 12:59
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


    @RequestMapping(value = "pay", method = RequestMethod.GET)
    public void payMent(@RequestHeader(value = "authorization",required = false) String authorization,@RequestParam Integer vip,@RequestParam Boolean json){
        payService.aliPay(authorization,vip,json);
    }


    @RequestMapping(value = "notify", method = RequestMethod.POST)
    public void notify(HttpServletRequest request, HttpServletResponse response) throws AlipayApiException, IOException {
        String str = payService.notify(request, response);
        response.setContentType("text/html;charset=utf-8");
        response.getWriter().println(str);
    }
}
