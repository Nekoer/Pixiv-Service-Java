package com.hcyacg.pixiv.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * @Author: Nekoer
 * @Desc: 会员
 * @Date: 2020/7/14 14:25
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@TableName(value = "vip")
public class Vip implements Serializable {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    @TableField(value = "name")
    private String name;


    public Vip(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Vip() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Vip{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
