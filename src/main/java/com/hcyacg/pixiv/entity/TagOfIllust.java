package com.hcyacg.pixiv.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * @Author: Nekoer
 * @Desc: 插画的标签
 * @Date: 2020/6/24 11:22
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@TableName(value = "tagofillust")
public class TagOfIllust implements Serializable {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    @TableField(value = "tagid")
    private Integer tagId;
    private String illust;


    public TagOfIllust(Integer id, Integer tagId, String illust) {
        this.id = id;
        this.tagId = tagId;
        this.illust = illust;
    }

    public TagOfIllust() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }

    public String getIllust() {
        return illust;
    }

    public void setIllust(String illust) {
        this.illust = illust;
    }

    @Override
    public String toString() {
        return "TagOfIllust{" +
                "id=" + id +
                ", tagId=" + tagId +
                ", illust=" + illust +
                '}';
    }
}
