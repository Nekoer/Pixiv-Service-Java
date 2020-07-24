package com.hcyacg.pixiv.task;

import com.hcyacg.pixiv.mapper.AccountMapper;
import com.hcyacg.pixiv.mapper.AccountVipMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created: 黄智文
 * Desc: 会员
 * Date: 2020/7/23 19:40
 */
@Component
public class VipTask {

    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private AccountVipMapper accountVipMapper;

    /**
     * 检查用户的会员是否到期
     */
    @Scheduled(cron = "0 1 0 * * ?")
    private void checkAccountVip(){



    }

}
