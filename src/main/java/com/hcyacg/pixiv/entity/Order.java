package com.hcyacg.pixiv.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

/**
 * @Author Nekoer
 * @Date 2020/10/17 15:40
 * @Desc 订单
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@TableName(value = "orders")
public class Order {

    @TableId(value = "id",type = IdType.AUTO)
    Integer id;
    @TableField(value = "out_trade")
    String outTrade;
    @TableField(value = "trade")
    String trade;
    @TableField(value = "amount")
    BigDecimal amount;
    @TableField(value = "status")
    Integer status;
    @TableField(value = "vip")
    Integer vip;
    @TableField(value = "account")
    Integer account;

    public Order() {
    }

    public Order(Integer id, String outTrade, String trade, BigDecimal amount, Integer status, Integer vip, Integer account) {
        this.id = id;
        this.outTrade = outTrade;
        this.trade = trade;
        this.amount = amount;
        this.status = status;
        this.vip = vip;
        this.account = account;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOutTrade() {
        return outTrade;
    }

    public void setOutTrade(String outTrade) {
        this.outTrade = outTrade;
    }

    public String getTrade() {
        return trade;
    }

    public void setTrade(String trade) {
        this.trade = trade;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getVip() {
        return vip;
    }

    public void setVip(Integer vip) {
        this.vip = vip;
    }

    public Integer getAccount() {
        return account;
    }

    public void setAccount(Integer account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", outTrade='" + outTrade + '\'' +
                ", trade='" + trade + '\'' +
                ", amount=" + amount +
                ", status=" + status +
                ", vip=" + vip +
                ", account=" + account +
                '}';
    }
}
