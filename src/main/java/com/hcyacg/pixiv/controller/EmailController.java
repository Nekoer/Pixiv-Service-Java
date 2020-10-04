package com.hcyacg.pixiv.controller;

import com.hcyacg.pixiv.dto.Result;
import com.hcyacg.pixiv.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * @Author Nekoer
 * @Date 2020/9/19 0:01
 * @Desc 邮箱
 */
@RestController
@RequestMapping("emails")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @RequestMapping(value = "code", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result sendCode(@RequestParam(value = "email") String email) {
        return emailService.code(email);
    }

    @RequestMapping(value = "updateCode", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result updateCode(@RequestHeader(value = "authorization") String authorization) {
        return emailService.updateCode(authorization);
    }

    @RequestMapping(value = "changeEmailCode", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result changeEmailCode(@RequestHeader(value = "authorization") String authorization,@RequestParam(value = "email") String email) {
        return emailService.changeEmailCode(authorization,email);
    }

    @RequestMapping(value = "changeForgetCode", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result changePassWordCodeForForget(@RequestParam(value = "email") String email) {
        System.out.println(email);
        return emailService.changePassWordCodeForForget(email);
    }
}
