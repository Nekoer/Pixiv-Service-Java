package com.hcyacg.pixiv.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.hcyacg.pixiv.bean.JwtOperation;
import com.hcyacg.pixiv.dto.Result;
import com.hcyacg.pixiv.entity.Account;
import com.hcyacg.pixiv.entity.Order;
import com.hcyacg.pixiv.mapper.AccountMapper;
import com.hcyacg.pixiv.mapper.OrderMapper;
import com.hcyacg.pixiv.service.OrderService;
import com.hcyacg.pixiv.utils.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Author Nekoer
 * @Date 2020/10/19 16:27
 * @Desc 订单
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OrderServiceImpl implements OrderService {
    @Autowired
    private HttpServletResponse res;
    @Autowired
    private HttpServletRequest req;
    @Autowired
    private HttpUtils httpUtils;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private JwtOperation jwtOperation;

    @Override
    public Result getOrders(String tradeNo,String authorization) {
        try{
            if(StringUtils.isBlank(authorization)){
                return httpUtils.setBuild(res, new Result(403, "您未登录", null, null));
            }
            Account account = JSON.parseObject(String.valueOf(jwtOperation.parseJwt(authorization).get("account")), Account.class);
            if (null == account) {
                return httpUtils.setBuild(res, new Result(403, "你未登录", null, null));
            }
            Account account1 = accountMapper.selectById(account.getId());
            if (null == account1){
                return httpUtils.setBuild(res, new Result(403, "你未登录", null, null));
            }

            if(StringUtils.isBlank(tradeNo)){
                return httpUtils.setBuild(res, new Result(400, "订单不能为空", null, null));
            }

            List<Order> out_trade = orderMapper.selectList(new QueryWrapper<Order>().eq("out_trade", tradeNo).eq("account",account.getId()));
            if(out_trade.size() < 1){
                return httpUtils.setBuild(res, new Result(400, "没有该订单", null, null));
            }

            return httpUtils.setBuild(res, new Result(201, "获取成功", out_trade.get(0), null));

        }catch (Exception e){
            e.printStackTrace();
            return httpUtils.setBuild(res, new Result(500, "获取失败", null, e.getMessage()));
        }
    }
}
