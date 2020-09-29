package com.hcyacg.pixiv.controller;

import com.hcyacg.pixiv.service.EmailService;
import com.hcyacg.pixiv.service.GeetestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Nekoer
 * @Date 2020/9/29 12:39
 * @Desc
 */
@RestController
@RequestMapping("geetests")
public class GeetestController {

    @Autowired
    private GeetestService geetestService;


    @RequestMapping(value = "register", method = RequestMethod.GET)
    public void FirstRegister(@RequestParam(value = "userId") String userId,@RequestParam(value = "clientType") String clientType) {
        geetestService.doGet(userId, clientType);
    }

    @RequestMapping(value = "validate", method = RequestMethod.GET)
    public void SecondValidate(@RequestParam(value = "userId") String userId,@RequestParam(value = "clientType") String clientType) {
        geetestService.doPost(userId, clientType);
    }

}
