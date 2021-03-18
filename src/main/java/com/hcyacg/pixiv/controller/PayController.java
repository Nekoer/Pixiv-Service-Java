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
import java.math.BigDecimal;

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


    @ApiOperation(value = "创建支付链接",notes = "创建支付链接")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "token", required = true, paramType = "header", dataType = "string"),
            @ApiImplicitParam(name = "type", value = "支付方式", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "vipId", value = "会员种类", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "vipPackAge", value = "会员套餐", required = true, paramType = "query", dataType = "int"),
    })
    @RequestMapping(value = "pays", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result createUrl(@RequestHeader(value = "authorization",required = false) String authorization,@RequestParam(value = "type") Integer type, @RequestParam(value = "vipId") Integer vipId,@RequestParam(value = "vipPackAge")Integer vipPackAge){
        return payService.createUrl(authorization,type,vipId,vipPackAge);
    }

    @ApiOperation(value = "支付异步回调",notes = "支付异步回调")
    @RequestMapping(value = "pays", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String notifyUrl(){
        return payService.notifyUrl(request);
    }


    @ApiOperation(value = "支付宝支付调用接口",notes = "支付宝支付调用接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "token", required = true, paramType = "header", dataType = "string"),
            @ApiImplicitParam(name = "vip", value = "会员种类", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "json", value = "是否为json格式", required = true, paramType = "query", dataType = "boolean"),
    })
    @RequestMapping(value = "pay", method = RequestMethod.GET)
    public void payMent(@RequestHeader(value = "authorization",required = false) String authorization,@RequestParam Integer vip,@RequestParam Boolean json){
        payService.aliPay(authorization,vip,json);
    }


    @ApiOperation(value = "支付宝异步回调",notes = "支付宝异步回调")
    @RequestMapping(value = "notify", method = RequestMethod.POST)
    public void notify(HttpServletRequest request, HttpServletResponse response) throws AlipayApiException, IOException {
        String str = payService.notify(request, response);
        response.setContentType("text/html;charset=utf-8");
        response.getWriter().println(str);
    }
}
