package com.hcyacg.pixiv.service;

import com.hcyacg.pixiv.dto.Result;

import javax.annotation.security.RolesAllowed;

/**
 * @Author Nekoer
 * @Date 2020/10/19 16:26
 * @Desc 订单
 */
public interface OrderService {

    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN","ROLE_SUPER_ADMIN"})
    Result getOrders(String tradeNo,String authorization);

}
