package com.hcyacg.pixiv.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * Created: 黄智文
 * Desc: 高清图
 * Date: 2020/6/23 18:26
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Original implements Serializable {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    private String url;
    private String illustid;

    public Original(Integer id, String url, String illustid) {
        this.id = id;
        this.url = url;
        this.illustid = illustid;
    }

    public Original() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIllustid() {
        return illustid;
    }

    public void setIllustid(String illustid) {
        this.illustid = illustid;
    }

    @Override
    public String toString() {
        return "Original{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", illustid='" + illustid + '\'' +
                '}';
    }
}
