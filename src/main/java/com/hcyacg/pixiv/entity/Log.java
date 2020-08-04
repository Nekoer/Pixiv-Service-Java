package com.hcyacg.pixiv.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * @Author: Nekoer
 * @Desc: 近七天的调用数据
 * @Date: 2020/5/20 23:52
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@TableName(value = "log")
public class Log implements Serializable {
    @TableId(value = "id",type = IdType.AUTO)
    Integer id;
    @TableField(value = "day")
    String day;
    @TableField(value = "frequency")
    Integer frequency;

    public Log(Integer id, String day, Integer frequency) {
        this.id = id;
        this.day = day;
        this.frequency = frequency;
    }

    public Log() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }


    @Override
    public String toString() {
        return "Log{" +
                "id=" + id +
                ", day='" + day + '\'' +
                ", frequency=" + frequency +
                '}';
    }
}
