package com.hcyacg.pixiv.task;

import com.hcyacg.pixiv.entity.Account;
import com.hcyacg.pixiv.mapper.AccountMapper;
import com.hcyacg.pixiv.mapper.AccountVipMapper;
import com.hcyacg.pixiv.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: Nekoer
 * @Desc: 会员
 * @Date: 2020/7/23 19:40
 */
@Component
public class VipTask {

    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private AccountVipMapper accountVipMapper;
    @Autowired
    private AccountService accountService;

    /**
     * 检查用户的会员是否到期
     * 每小时执行一次
     */
    @Scheduled(cron = "0 0 0/1 * * ?")
    private void checkAccountVip(){
        List<Account> accounts = accountMapper.selectList(null);
        accounts.forEach(account -> accountService.checkVipTime(account.getId()));
    }

}
