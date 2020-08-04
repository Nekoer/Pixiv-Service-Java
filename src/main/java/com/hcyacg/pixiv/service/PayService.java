package com.hcyacg.pixiv.service;

import com.hcyacg.pixiv.dto.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;

/**
 * Created: 黄智文
 * Desc: 支付接口
 * Date: 2020/7/13 12:52
 */
public interface PayService {

    /**
     * 创建支付路径
     * @param authorization 用户jwt
     * @param type 支付方式
     * @param vipId 会员种类
     * @param vipPackAge 会员套餐
     * @return 返回支付路径
     */
    Result createUrl(String authorization,Integer type, Integer vipId, Integer vipPackAge);

    /**
     * 支付异步回调
     * @return
     */
    String notifyUrl(HttpServletRequest request);

    /**
     * 支付宝支付调用接口
     *
     * @param orderNum 订单号
     */
    void aliPay(String orderNum);

    String notify(HttpServletRequest request, HttpServletResponse response);

    boolean rsaCheckV1(HttpServletRequest request);
}
