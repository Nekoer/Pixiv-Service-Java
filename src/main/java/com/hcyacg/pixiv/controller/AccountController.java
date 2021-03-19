package com.hcyacg.pixiv.controller;

import com.hcyacg.pixiv.dto.Result;
import com.hcyacg.pixiv.service.AccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


/**
 * @Author: Nekoer
 * @Desc: 账户控制器
 * @Date: 2020/6/15 13:44
 */
@RestController
@RequestMapping("accounts")
@Api(value = "用户业务接口",tags = "处理用户相关操作")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @ApiOperation(value = "注册账号",notes = "注册账号")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userName", value = "账号", required = true, paramType = "body", dataType = "string"),
            @ApiImplicitParam(name = "passWord", value = "密码", required = true, paramType = "body", dataType = "string"),
            @ApiImplicitParam(name = "confirm", value = "确认密码", required = true, paramType = "body", dataType = "string"),
            @ApiImplicitParam(name = "email", value = "注册邮箱", required = true, paramType = "body", dataType = "string"),
            @ApiImplicitParam(name = "code", value = "验证码", required = true, paramType = "body", dataType = "string"),
    })
    @RequestMapping(value = "register", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result register(@RequestParam(value = "userName") String userName, @RequestParam(value = "passWord") String passWord, @RequestParam(value = "confirm") String confirm, @RequestParam(value = "email") String email, @RequestParam(value = "code") String code) {
        return accountService.register(userName, passWord, confirm, email, code);
    }

    @ApiOperation(value = "登录账号",notes = "登录账号")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userName", value = "账号", required = true, paramType = "body", dataType = "string"),
            @ApiImplicitParam(name = "passWord", value = "密码", required = true, paramType = "body", dataType = "string"),
            @ApiImplicitParam(name = "code", value = "验证码", required = true, paramType = "body", dataType = "string"),
    })
    @RequestMapping(value = "login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result login(@RequestParam(value = "userName") String userName, @RequestParam(value = "passWord") String passWord, @RequestParam(value = "code") String code) {
        return accountService.login(userName, passWord, code);
    }

    @ApiOperation(value = "更新密码",notes = "更新密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "token", required = true, paramType = "header", dataType = "string"),
            @ApiImplicitParam(name = "originalPassWord", value = "原密码", required = true, paramType = "body", dataType = "string"),
            @ApiImplicitParam(name = "passWord", value = "新密码", required = true, paramType = "body", dataType = "string"),
            @ApiImplicitParam(name = "confirm", value = "确认密码", required = true, paramType = "body", dataType = "string"),
            @ApiImplicitParam(name = "vCode", value = "验证码", required = true, paramType = "body", dataType = "string"),
    })
    @RequestMapping(value = "updatePassWord", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result updatePassWord(@RequestHeader(value = "authorization") String authorization, @RequestParam(value = "originalPassWord") String originalPassWord, @RequestParam(value = "passWord") String passWord, @RequestParam(value = "confirm") String confirm, @RequestParam(value = "vCode") String vCode) {
        return accountService.updatePassWord(authorization, originalPassWord, passWord, confirm, vCode);
    }

    @ApiOperation(value = "更改邮箱",notes = "更改邮箱")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "token", required = true, paramType = "header", dataType = "string"),
            @ApiImplicitParam(name = "email", value = "邮箱", required = true, paramType = "body", dataType = "string"),
            @ApiImplicitParam(name = "code", value = "验证码", required = true, paramType = "body", dataType = "string"),
    })
    @RequestMapping(value = "changeEmail", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result changeEmail(@RequestHeader(value = "authorization") String authorization, @RequestParam(value = "email") String email,@RequestParam(value = "code") String code) {
        return accountService.changeEmail(authorization, email, code);
    }

    @ApiOperation(value = "获得图形验证码",notes = "获得图形验证码")
    @RequestMapping(value = "validate", method = RequestMethod.GET)
    public void getValidate() {
        accountService.validateCode();
    }

    @ApiOperation(value = "获得App图形验证码",notes = "获得App图形验证码")
    @RequestMapping(value = "android/validate", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getValidateForAndroid() {
        return accountService.validateCodeForAndroid();
    }

    @ApiOperation(value = "获得用户信息",notes = "获得用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "token", required = true, paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "info", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getAccountInfo(@RequestHeader(value = "authorization") String authorization) {
        return accountService.getAccountInfo(authorization);
    }

    @ApiOperation(value = "用户鉴权是否过期",notes = "用户鉴权是否过期")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "token", required = true, paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "expires", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result Expires(@RequestHeader(value = "authorization") String authorization) {
        return accountService.expires(authorization);
    }

    @ApiOperation(value = "更新用户头像",notes = "更新用户头像")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "token", required = true, paramType = "header", dataType = "string"),
            @ApiImplicitParam(name = "file", value = "图片Base64编码", required = true, paramType = "body", dataType = "string"),
    })
    @RequestMapping(value = "avatar", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result updateAvatar(@RequestHeader(value = "authorization") String authorization, @RequestParam("file") String file) {
        return accountService.updateAvatar(authorization, file);
    }

    @ApiOperation(value = "更新用户信息",notes = "更新用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "token", required = true, paramType = "header", dataType = "string"),
            @ApiImplicitParam(name = "nickName", value = "昵称", required = true, paramType = "body", dataType = "string"),
            @ApiImplicitParam(name = "sex", value = "性别", required = true, paramType = "body", dataType = "string"),
            @ApiImplicitParam(name = "birthday", value = "生日", required = true, paramType = "body", dataType = "string"),
            @ApiImplicitParam(name = "iphone", value = "手机号", required = true, paramType = "body", dataType = "string"),
    })
    @RequestMapping(value = "info", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result updateAccountInfo(@RequestHeader(value = "authorization") String authorization, @RequestParam(value = "nickName",required = false) String nickName, @RequestParam(value = "sex",required = false) String sex, @RequestParam(value = "birthday",required = false) String birthday, @RequestParam(value = "iphone",required = false) String iphone) {
        return accountService.updateAccountInfo(authorization, nickName, iphone, sex, birthday);
    }

    @ApiOperation(value = "判断用户是否有涩图权限",notes = "判断用户是否有涩图权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "token", required = true, paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "hasPorn", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result hasPorn(@RequestHeader(value = "authorization") String authorization) {
        return accountService.hasPorn(authorization);
    }

    @ApiOperation(value = "判断用户是否有开发权限",notes = "判断用户是否有开发权限")
    @RequestMapping(value = "hasApiKey", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result hasApiKey() {
        return accountService.hasApiKey();
    }


    @ApiOperation(value = "获得开发token",notes = "获得开发token")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "token", required = true, paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "ApiKey", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getApiKey(@RequestHeader(value = "authorization") String authorization) {
        return accountService.getApiKey(authorization);
    }

    @ApiOperation(value = "申请开发token",notes = "申请开发token")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "token", required = true, paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "apply", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result applyForApiKey(@RequestHeader(value = "authorization") String authorization) {
        return accountService.applyForApiKey(authorization);
    }

    @ApiOperation(value = "重设开发token",notes = "重设开发token")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "token", required = true, paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "reset", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result resetApiKey(@RequestHeader(value = "authorization") String authorization) {
        return accountService.resetApiKey(authorization);
    }

    @ApiOperation(value = "判断是否是vip会员",notes = "判断是否是vip会员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "token", required = true, paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "vips", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result vips(@RequestHeader(value = "authorization") String authorization) {
        return accountService.isVip(authorization);
    }

    @ApiOperation(value = "忘记密码",notes = "忘记密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "email", value = "邮箱", required = true, paramType = "header", dataType = "string"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, paramType = "body", dataType = "string"),
            @ApiImplicitParam(name = "confirm", value = "确认密码", required = true, paramType = "body", dataType = "string"),
            @ApiImplicitParam(name = "vCode", value = "验证码", required = true, paramType = "body", dataType = "string"),
    })
    @RequestMapping(value = "forget/change", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result changePassWordForForget(@RequestParam(value = "email") String email,@RequestParam(value = "password") String password,@RequestParam(value = "confirm") String confirm,@RequestParam(value = "vCode") String vCode) {
        return accountService.changePassWordForForget(email, password, confirm, vCode);
    }
}
