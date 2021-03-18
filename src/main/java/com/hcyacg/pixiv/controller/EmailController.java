package com.hcyacg.pixiv.controller;

import com.hcyacg.pixiv.dto.Result;
import com.hcyacg.pixiv.service.EmailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * @Author Nekoer
 * @Date 2020/9/19 0:01
 * @Desc 邮箱
 */
@Api(value = "邮箱业务接口",tags = "处理邮箱相关操作")
@RestController
@RequestMapping("emails")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @ApiOperation(value = "发送注册验证码",notes = "发送注册验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "email", value = "注册邮箱", required = true, paramType = "body", dataType = "string"),
    })
    @RequestMapping(value = "code", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result sendCode(@RequestParam(value = "email") String email) {
        return emailService.code(email);
    }

    @ApiOperation(value = "发送验证码",notes = "发送验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "token", required = true, paramType = "header", dataType = "string"),
    })
    @RequestMapping(value = "updateCode", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result updateCode(@RequestHeader(value = "authorization") String authorization) {
        return emailService.updateCode(authorization);
    }


    @ApiOperation(value = "发送更新邮箱的验证码",notes = "发送更新邮箱的验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "token", required = true, paramType = "header", dataType = "string"),
            @ApiImplicitParam(name = "email", value = "邮箱", required = true, paramType = "body", dataType = "string"),
    })
    @RequestMapping(value = "changeEmailCode", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result changeEmailCode(@RequestHeader(value = "authorization") String authorization,@RequestParam(value = "email") String email) {
        return emailService.changeEmailCode(authorization,email);
    }

    @ApiOperation(value = "发送忘记密码的验证码",notes = "发送忘记密码的验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "email", value = "邮箱", required = true, paramType = "body", dataType = "string"),
    })
    @RequestMapping(value = "changeForgetCode", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result changePassWordCodeForForget(@RequestParam(value = "email") String email) {
        return emailService.changePassWordCodeForForget(email);
    }
}
