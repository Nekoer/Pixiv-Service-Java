package com.hcyacg.pixiv.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created: 黄智文
 * Desc: 用户会员账单
 * Date: 2020/7/14 22:38
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@TableName(value = "account_vip")
public class AccountVip implements Serializable {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    @TableField(value = "account_id")
    private Integer accountId;
    @TableField(value = "vip_package_id")
    private Integer vipPackAgeId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField(value = "createtime")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField(value = "endtime")
    private Date endTime;
    @TableField(value = "pay_no")
    private String payNo;

    public AccountVip(Integer id, Integer accountId, Integer vipPackAgeId, Date createTime, Date endTime, String payNo) {
        this.id = id;
        this.accountId = accountId;
        this.vipPackAgeId = vipPackAgeId;
        this.createTime = createTime;
        this.endTime = endTime;
        this.payNo = payNo;
    }

    public AccountVip() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public Integer getVipPackAgeId() {
        return vipPackAgeId;
    }

    public void setVipPackAgeId(Integer vipPackAgeId) {
        this.vipPackAgeId = vipPackAgeId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getPayNo() {
        return payNo;
    }

    public void setPayNo(String payNo) {
        this.payNo = payNo;
    }

    @Override
    public String toString() {
        return "AccountVip{" +
                "id=" + id +
                ", accountId=" + accountId +
                ", vipPackAgeId=" + vipPackAgeId +
                ", createTime=" + createTime +
                ", endTime=" + endTime +
                ", payNo=" + payNo +
                '}';
    }
}
