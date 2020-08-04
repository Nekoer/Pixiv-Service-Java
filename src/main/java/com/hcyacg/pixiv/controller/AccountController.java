package com.hcyacg.pixiv.controller;

import com.hcyacg.pixiv.dto.Result;
import com.hcyacg.pixiv.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.ExecutionException;

/**
 * Created: 黄智文
 * Desc: 账户控制器
 * Date: 2020/6/15 13:44
 */
@RestController
@RequestMapping("accounts")
public class AccountController {
    @Autowired
    private AccountService accountService;


    @RequestMapping(value = "register", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result register(@RequestParam(value = "userName") String userName, @RequestParam(value = "passWord") String passWord, @RequestParam(value = "confirm") String confirm, @RequestParam(value = "email") String email, @RequestParam(value = "code") String code) {
        return accountService.register(userName, passWord, confirm, email, code);
    }

    @RequestMapping(value = "login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result login(@RequestParam(value = "userName") String userName, @RequestParam(value = "passWord") String passWord, @RequestParam(value = "code") String code) {
        return accountService.login(userName, passWord, code);
    }

    @RequestMapping(value = "code", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result sendCode(@RequestParam(value = "email") String email) {
        return accountService.code(email);
    }

    @RequestMapping(value = "updateCode", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result updateCode(@RequestHeader(value = "authorization") String authorization) {
        return accountService.updateCode(authorization);
    }

    @RequestMapping(value = "changeEmailCode", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result changeEmailCode(@RequestHeader(value = "authorization") String authorization,@RequestParam(value = "email") String email) {
        return accountService.changeEmailCode(authorization,email);
    }

    @RequestMapping(value = "updatePassWord", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result updatePassWord(@RequestHeader(value = "authorization") String authorization, @RequestParam(value = "originalPassWord") String originalPassWord, @RequestParam(value = "passWord") String passWord, @RequestParam(value = "confirm") String confirm, @RequestParam(value = "vCode") String vCode) {
        return accountService.updatePassWord(authorization, originalPassWord, passWord, confirm, vCode);
    }

    @RequestMapping(value = "changeEmail", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result changeEmail(@RequestHeader(value = "authorization") String authorization, @RequestParam(value = "email") String email,@RequestParam(value = "code") String code) {
        return accountService.changeEmail(authorization, email, code);
    }

    @RequestMapping(value = "validate", method = RequestMethod.GET)
    public void getValidate() {
        accountService.ValidateCode();
    }


    @RequestMapping(value = "info", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getAccountInfo(@RequestHeader(value = "authorization") String authorization) {
        return accountService.getAccountInfo(authorization);
    }

    @RequestMapping(value = "expires", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result Expires(@RequestHeader(value = "authorization") String authorization) {
        return accountService.expires(authorization);
    }

    @RequestMapping(value = "avatar", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result updateAvatar(@RequestHeader(value = "authorization") String authorization, @RequestParam("file") String file) {
        return accountService.updateAvatar(authorization, file);
    }

    @RequestMapping(value = "info", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result updateAccountInfo(@RequestHeader(value = "authorization") String authorization, @RequestParam(value = "nickName",required = false) String nickName, @RequestParam(value = "sex",required = false) String sex, @RequestParam(value = "birthday",required = false) String birthday, @RequestParam(value = "iphone",required = false) String iphone) {
        return accountService.updateAccountInfo(authorization, nickName, iphone, sex, birthday);
    }

    @RequestMapping(value = "hasPorn", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result hasPorn(@RequestHeader(value = "authorization") String authorization) {
        return accountService.hasPorn(authorization);
    }

    @RequestMapping(value = "hasApiKey", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result hasApiKey() {
        return accountService.hasApiKey();
    }

    @RequestMapping(value = "ApiKey", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getApiKey(@RequestHeader(value = "authorization") String authorization) {
        return accountService.getApiKey(authorization);
    }

    @RequestMapping(value = "apply", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result applyForApiKey(@RequestHeader(value = "authorization") String authorization) {
        return accountService.applyForApiKey(authorization);
    }

    @RequestMapping(value = "reset", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result resetApiKey(@RequestHeader(value = "authorization") String authorization) {
        return accountService.resetApiKey(authorization);
    }

    @RequestMapping(value = "vips", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result vips(@RequestHeader(value = "authorization") String authorization) {
        return accountService.isVip(authorization);
    }
}
