package com.hcyacg.pixiv.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * Created: 黄智文
 * Desc:
 * Date: 2020/6/24 11:37
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class OriginalDto implements Serializable {
    private String url;

    public OriginalDto(String url) {
        this.url = url;
    }

    public OriginalDto() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "OriginalDto{" +
                "url='" + url + '\'' +
                '}';
    }
}
