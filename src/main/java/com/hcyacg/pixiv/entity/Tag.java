package com.hcyacg.pixiv.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * Created: 黄智文
 * Desc: 图片标签
 * Date: 2020/6/23 18:21
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@TableName(value = "tag")
public class Tag implements Serializable {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    private String name;
    @TableField(value = "translated_name")
    private String translatedName;

    public Tag(Integer id, String name, String translatedName) {
        this.id = id;
        this.name = name;
        this.translatedName = translatedName;
    }

    public Tag() {
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

    public String getTranslatedName() {
        return translatedName;
    }

    public void setTranslatedName(String translatedName) {
        this.translatedName = translatedName;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", translatedName='" + translatedName + '\'' +
                '}';
    }
}
