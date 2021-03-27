package com.hcyacg.pixiv.service;

import com.hcyacg.pixiv.dto.Result;
import com.sun.org.apache.xpath.internal.operations.Bool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;

/**
 * @Author: Nekoer
 * @Desc: 支付接口
 * @Date: 2020/7/13 12:52
 */
public interface PayService {



    /**
     * 支付宝支付调用接口
     *
     * @param vip vip
     */
    void aliPay(String authorization, int vip, Boolean json,Boolean base64);

    String notify(HttpServletRequest request, HttpServletResponse response);

    boolean rsaCheckV1(HttpServletRequest request);
}
