package com.hcyacg.pixiv.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;

/**
 * Created: 黄智文
 * Desc:
 * Date: 2020/7/8 21:16
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AmazingPic implements Serializable {
    private Long total;
    private Long size;
    private Long current;
    private Long pages;
    private List<IllustDto> illustDtos;

    public AmazingPic(Long total, Long size, Long current, Long pages, List<IllustDto> illustDtos) {
        this.total = total;
        this.size = size;
        this.current = current;
        this.pages = pages;
        this.illustDtos = illustDtos;
    }

    public AmazingPic() {
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getCurrent() {
        return current;
    }

    public void setCurrent(Long current) {
        this.current = current;
    }

    public Long getPages() {
        return pages;
    }

    public void setPages(Long pages) {
        this.pages = pages;
    }

    public List<IllustDto> getIllustDtos() {
        return illustDtos;
    }

    public void setIllustDtos(List<IllustDto> illustDtos) {
        this.illustDtos = illustDtos;
    }

    @Override
    public String toString() {
        return "amazingPic{" +
                "total=" + total +
                ", size=" + size +
                ", current=" + current +
                ", pages=" + pages +
                ", illustDtos=" + illustDtos +
                '}';
    }
}
