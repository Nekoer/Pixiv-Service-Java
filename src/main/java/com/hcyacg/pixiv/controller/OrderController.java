package com.hcyacg.pixiv.controller;

import com.hcyacg.pixiv.dto.Result;
import com.hcyacg.pixiv.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

/**
 * @Author Nekoer
 * @Date 2020/10/19 16:39
 * @Desc
 */
@RestController
@RequestMapping("orders")
public class OrderController {


    @Autowired
    private OrderService orderService;

    @RequestMapping(value = "info", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result detail( @RequestHeader(value = "authorization",required = false) String authorization, @RequestParam(value = "trade",required = false) String  trade) throws ExecutionException, InterruptedException {
        return orderService.getOrders(trade,authorization);
    }
}
