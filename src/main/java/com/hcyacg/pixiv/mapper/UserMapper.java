package com.hcyacg.pixiv.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hcyacg.pixiv.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created: 黄智文
 * Desc:
 * Date: 2020/6/23 18:32
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
