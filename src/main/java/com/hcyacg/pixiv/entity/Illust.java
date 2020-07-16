package com.hcyacg.pixiv.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * Created: 黄智文
 * Desc: 插画详情类
 * Date: 2020/6/23 18:08
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@TableName(value = "illust")
public class Illust implements Serializable {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    @TableField(value = "illust")
    private String illustId;
    private String title;
    private String type;
    private String caption;
    @TableField(value = "restrictn")
    private String restrictN;
    @TableField(value = "create_date")
    private String createDate;
    @TableField(value = "page_count")
    private String pageCount;
    private String width;
    private String height;
    @TableField(value = "sanitylevel")
    private String sanityLevel;
    @TableField(value = "x_restrict")
    private String xRestrict;
    private String series;
    @TableField(value = "total_view")
    private String totalView;
    @TableField(value = "total_bookmarks")
    private String totalBookmarks;
    @TableField(value = "is_bookmarked")
    private String isBookmarked;
    private String visible;
    @TableField(value = "is_muted")
    private String isMuted;
    @TableField(value = "total_comments")
    private String totalComments;
    private String large;
    @TableField(value = "userid")
    private String userId;

    public Illust() {
    }

    public Illust(Integer id, String illustId, String title, String type, String caption, String restrictN, String createDate, String pageCount, String width, String height, String sanityLevel, String xRestrict, String series, String totalView, String totalBookmarks, String isBookmarked, String visible, String isMuted, String totalComments, String large, String userId) {
        this.id = id;
        this.illustId = illustId;
        this.title = title;
        this.type = type;
        this.caption = caption;
        this.restrictN = restrictN;
        this.createDate = createDate;
        this.pageCount = pageCount;
        this.width = width;
        this.height = height;
        this.sanityLevel = sanityLevel;
        this.xRestrict = xRestrict;
        this.series = series;
        this.totalView = totalView;
        this.totalBookmarks = totalBookmarks;
        this.isBookmarked = isBookmarked;
        this.visible = visible;
        this.isMuted = isMuted;
        this.totalComments = totalComments;
        this.large = large;
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIllustId() {
        return illustId;
    }

    public void setIllustId(String illustId) {
        this.illustId = illustId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getRestrictN() {
        return restrictN;
    }

    public void setRestrictN(String restrictN) {
        this.restrictN = restrictN;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getPageCount() {
        return pageCount;
    }

    public void setPageCount(String pageCount) {
        this.pageCount = pageCount;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getSanityLevel() {
        return sanityLevel;
    }

    public void setSanityLevel(String sanityLevel) {
        this.sanityLevel = sanityLevel;
    }

    public String getxRestrict() {
        return xRestrict;
    }

    public void setxRestrict(String xRestrict) {
        this.xRestrict = xRestrict;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getTotalView() {
        return totalView;
    }

    public void setTotalView(String totalView) {
        this.totalView = totalView;
    }

    public String getTotalBookmarks() {
        return totalBookmarks;
    }

    public void setTotalBookmarks(String totalBookmarks) {
        this.totalBookmarks = totalBookmarks;
    }

    public String getIsBookmarked() {
        return isBookmarked;
    }

    public void setIsBookmarked(String isBookmarked) {
        this.isBookmarked = isBookmarked;
    }

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public String getIsMuted() {
        return isMuted;
    }

    public void setIsMuted(String isMuted) {
        this.isMuted = isMuted;
    }

    public String getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(String totalComments) {
        this.totalComments = totalComments;
    }

    public String getLarge() {
        return large;
    }

    public void setLarge(String large) {
        this.large = large;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Illust{" +
                "id=" + id +
                ", illustId='" + illustId + '\'' +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", caption='" + caption + '\'' +
                ", restrictN='" + restrictN + '\'' +
                ", createDate='" + createDate + '\'' +
                ", pageCount='" + pageCount + '\'' +
                ", width='" + width + '\'' +
                ", height='" + height + '\'' +
                ", sanityLevel='" + sanityLevel + '\'' +
                ", xRestrict='" + xRestrict + '\'' +
                ", series='" + series + '\'' +
                ", totalView='" + totalView + '\'' +
                ", totalBookmarks='" + totalBookmarks + '\'' +
                ", isBookmarked='" + isBookmarked + '\'' +
                ", visible='" + visible + '\'' +
                ", isMuted='" + isMuted + '\'' +
                ", totalComments='" + totalComments + '\'' +
                ", large='" + large + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
