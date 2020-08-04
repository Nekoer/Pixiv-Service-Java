package com.hcyacg.pixiv.service;

import com.hcyacg.pixiv.dto.Result;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Created: 黄智文
 * Desc: 账户接口
 * Date: 2020/5/12 15:09
 */
public interface AccountService {

    /**
     * pixiv账户获得token
     * @return
     */
    Result getToken();

    /**
     * 本站用户注册
     * @param userName 账户
     * @param passWord 密码
     * @param confirm 确认密码
     * @param email 邮箱
     * @param code 验证码
     * @return 返回是否注册成功
     */
    Result register(String userName,String passWord,String confirm,String email,String code);

    /**
     * 本站用户登录
     * @param userName 账户
     * @param passWord 密码
     * @param code 验证码
     * @return 返回token
     */
    Result login(String userName,String passWord,String code);

    /**
     * 发送邮箱验证码
     * @param email 邮箱
     * @return 返回验证码
     */
    Result code(String email);

    /**
     * 获取图形验证码
     */
    void ValidateCode();

    /**
     * 修改密码
     * @param authorization 用户鉴权
     * @param originalPassWord 原密码
     * @param passWord 修改密码
     * @param confirm 确认修改密码
     * @param vCode 验证码
     * @return 返回修改状态
     */
    @PreAuthorize("hasRole('ROLE_CHANGE') AND hasRole('ROLE_USER')")
    Result updatePassWord(String authorization,String originalPassWord,String passWord,String confirm,String vCode);

    /**
     * 发送邮箱验证码
     * @param authorization 用户鉴权
     * @return 返回验证码
     */
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN","ROLE_SUPER_ADMIN"})
    Result updateCode(String authorization);

    /**
     * 发送邮箱验证码
     * @param authorization 用户鉴权
     * @param email 新邮箱
     * @return 返回验证码
     */
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN","ROLE_SUPER_ADMIN"})
    Result changeEmailCode(String authorization,String email);
    /**
     * 更改邮箱
     * @param authorization 用户jwt
     * @param email 新邮箱
     * @param code 验证码
     * @return 返回修改是否成功
     */
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN","ROLE_SUPER_ADMIN"})
    Result changeEmail(String authorization,String email,String code);

    /**
     * 获得用户信息
     * @return 返回数据
     */
    @PreAuthorize("hasAnyRole('ROLE_READ') AND hasAnyRole('ROLE_USER')")
    Result getAccountInfo(String authorization);

    /**
     * 判断jwt是否过期
     * @param authorization jwt
     * @return 返回布尔值
     */
    Result expires(String authorization);

    /**
     * 修改头像
     * @param authorization jwt
     * @param file 图片
     * @return 返回数据
     */
    @PreAuthorize("hasRole('ROLE_UPLOAD') AND hasRole('ROLE_USER')")
    Result updateAvatar(String authorization, String file);

    /**
     *
     * @param authorization jwt
     * @param nickName 昵称
     * @param iphone 手机号
     * @param sex 性别 1为男，0为女
     * @param birthday 生日
     * @return 返回是否更新成功并返回新的用户信息
     */
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN","ROLE_SUPER_ADMIN","ROLE_CHANGE"})
    Result updateAccountInfo(String authorization,String nickName,String iphone,String sex,String birthday);

    /**
     * 判断是否有看18x的权限
     * @param authorization jwt
     * @return 返回布尔值
     */
    Result hasPorn(String authorization);


    /**
     * 判断是否有申请ApiKey的权限
     * @return 默认返回true
     */
    @PreAuthorize("hasRole('ROLE_APIKEY') AND hasRole('ROLE_USER')")
    Result hasApiKey();

    /**
     * 获取APIKEY
     * @param authorization jwt
     * @return 返回数据
     */
    @PreAuthorize("hasRole('ROLE_APIKEY') AND hasRole('ROLE_USER')")
    Result getApiKey(String authorization);

    /**
     * 颁发该用户的APIKEY
     * @param authorization jwt
     * @return
     */
    @PreAuthorize("hasRole('ROLE_APIKEY') AND hasRole('ROLE_USER')")
    Result applyForApiKey(String authorization);

    /**
     * 重置APIKEY
     * @param authorization jwt
     * @return
     */
    @PreAuthorize("hasRole('ROLE_APIKEY') AND hasRole('ROLE_USER')")
    Result resetApiKey(String authorization);

    /**
     * 是否允许注册
     * @return
     */
    Boolean canRegister();

    /**
     * 判断是否是会员
     * @param authorization 用户jwt
     * @return 返回会员信息
     */
    Result isVip(String authorization);

    /**
     * 判断会员是否过期
     * @param userId 用户id
     * @return 返回是否过期
     */
    Boolean checkVipTime(Integer userId);
}
