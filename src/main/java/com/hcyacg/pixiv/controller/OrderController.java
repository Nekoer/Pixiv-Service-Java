package com.hcyacg.pixiv.controller;

import com.hcyacg.pixiv.dto.Result;
import com.hcyacg.pixiv.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

/**
 * @Author Nekoer
 * @Date 2020/10/19 16:39
 * @Desc
 */
@Api(value = "订单业务接口",tags = "处理订单相关操作")
@RestController
@RequestMapping("orders")
public class OrderController {


    @Autowired
    private OrderService orderService;

    @ApiOperation(value = "获取订单详情",notes = "获取订单详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "token", required = true, paramType = "header", dataType = "string"),
            @ApiImplicitParam(name = "trade", value = "订单号", required = true, paramType = "query", dataType = "string"),
    })
    @RequestMapping(value = "info", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result detail( @RequestHeader(value = "authorization",required = false) String authorization, @RequestParam(value = "trade",required = false) String  trade) throws ExecutionException, InterruptedException {
        return orderService.getOrders(trade,authorization);
    }
}
