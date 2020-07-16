package com.hcyacg.pixiv.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * Created: 黄智文
 * Desc: 标签类
 * Date: 2020/6/24 11:34
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TagDto implements Serializable {

    private String name;
    private String translatedName;

    public TagDto(String name, String translatedName) {
        this.name = name;
        this.translatedName = translatedName;
    }

    public TagDto() {
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
        return "TagDto{" +
                "name='" + name + '\'' +
                ", translatedName='" + translatedName + '\'' +
                '}';
    }
}
