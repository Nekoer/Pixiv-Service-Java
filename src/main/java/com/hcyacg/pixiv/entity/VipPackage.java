package com.hcyacg.pixiv.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author: Nekoer
 * @Desc: 会员套餐
 * @Date: 2020/7/14 14:26
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@TableName(value = "vip_package")
public class VipPackage implements Serializable {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    @TableField(value = "title")
    private String title;
    @TableField(value = "month")
    private Integer month;
    private Integer discount;
    @TableField(value = "vip_id")
    private String vipId;
    private BigDecimal price;

    public VipPackage(Integer id, String title, Integer month, Integer discount, String vipId, BigDecimal price) {
        this.id = id;
        this.title = title;
        this.month = month;
        this.discount = discount;
        this.vipId = vipId;
        this.price = price;
    }

    public VipPackage() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public String getVipId() {
        return vipId;
    }

    public void setVipId(String vipId) {
        this.vipId = vipId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "VipPackage{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", month='" + month + '\'' +
                ", discount='" + discount + '\'' +
                ", vipId='" + vipId + '\'' +
                ", price=" + price +
                '}';
    }
}
