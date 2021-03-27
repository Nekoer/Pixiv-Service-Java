package com.hcyacg.pixiv.controller;

import com.alipay.api.AlipayApiException;
import com.hcyacg.pixiv.dto.Result;
import com.hcyacg.pixiv.service.PayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: Nekoer
 * @Desc:
 * @Date: 2020/7/13 12:59
 */
@Api(value = "支付业务接口",tags = "处理支付相关操作")
@RestController
@RequestMapping("pays")
public class PayController {
    @Autowired
    private PayService payService;

    @Autowired
    private HttpServletRequest request;


    @ApiOperation(value = "支付宝支付调用接口",notes = "支付宝支付调用接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "token", required = true, paramType = "header", dataType = "string"),
            @ApiImplicitParam(name = "vip", value = "会员种类", required = true, paramType = "query", dataType = "int",defaultValue = "1",example = "1"),
            @ApiImplicitParam(name = "json", value = "是否为json格式", required = true, paramType = "query", dataType = "boolean"),
    })
    @RequestMapping(value = "pay", method = RequestMethod.GET)
    public void payMent(@RequestHeader(value = "authorization",required = false) String authorization,@RequestParam Integer vip,@RequestParam Boolean json,@RequestParam(value = "base64",required = false) Boolean base64){
        payService.aliPay(authorization,vip,json,base64);
    }


    @ApiOperation(value = "支付宝异步回调",notes = "支付宝异步回调")
    @RequestMapping(value = "notify", method = RequestMethod.POST)
    public void notify(HttpServletRequest request, HttpServletResponse response) throws AlipayApiException, IOException {
        String str = payService.notify(request, response);
        response.setContentType("text/html;charset=utf-8");
        response.getWriter().println(str);
    }
}
