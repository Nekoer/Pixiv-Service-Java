package com.hcyacg.pixiv.service;

import com.hcyacg.pixiv.dto.GeetestLibResult;

/**
 * @Author Nekoer
 * @Date 2020/9/29 12:22
 * @Desc 极验验证码
 */
public interface GeetestService {

    /**
     * 验证初始化接口，GET请求
     * @return
     */
    void doGet(String userId,String clientType);

    /**
     * 二次验证接口，POST请求
     */
    void doPost(String userId,String clientType);

}
