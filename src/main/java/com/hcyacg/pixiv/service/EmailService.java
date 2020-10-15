package com.hcyacg.pixiv.service;

import com.hcyacg.pixiv.dto.Result;

import javax.annotation.security.RolesAllowed;

/**
 * @Author Nekoer
 * @Date 2020/9/18 23:55
 * @Desc 邮箱类
 */
public interface EmailService {

    /**
     * 发送邮箱验证码
     * @param email 邮箱
     * @return 返回验证码
     */
    Result code(String email);

    /**
     * 发送邮箱验证码 - 修改密码
     * @param authorization 用户鉴权
     * @return 返回验证码
     */
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN","ROLE_SUPER_ADMIN"})
    Result updateCode(String authorization);

    /**
     * 发送邮箱验证码 - 修改邮箱
     * @param authorization 用户鉴权
     * @param email 新邮箱
     * @return 返回验证码
     */
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN","ROLE_SUPER_ADMIN"})
    Result changeEmailCode(String authorization,String email);

    /**
     * 忘记密码状态 发送邮箱验证码
     * @param email 账号邮箱
     * @return 返回是否发送成功
     */
    Result changePassWordCodeForForget(String email);
}
