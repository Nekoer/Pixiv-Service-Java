package com.hcyacg.pixiv.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * Created: 黄智文
 * Desc: 系统参数
 * Date: 2020/7/10 11:14
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@TableName(value = "system")
public class Systems implements Serializable {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    private String name;
    private String parameter;


    public Systems(Integer id, String name, String parameter) {
        this.id = id;
        this.name = name;
        this.parameter = parameter;
    }

    public Systems() {
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

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    @Override
    public String toString() {
        return "System{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", parameter='" + parameter + '\'' +
                '}';
    }
}
