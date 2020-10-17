package com.hcyacg.pixiv.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.hcyacg.pixiv.entity.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author Nekoer
 * @Date 2020/10/17 15:42
 * @Desc 订单
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
