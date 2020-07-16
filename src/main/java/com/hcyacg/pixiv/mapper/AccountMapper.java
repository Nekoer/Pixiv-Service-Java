package com.hcyacg.pixiv.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hcyacg.pixiv.entity.Account;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created: 黄智文
 * Desc: 账户
 * Date: 2020/6/15 14:55
 */
@Mapper
public interface AccountMapper extends BaseMapper<Account> {
}
