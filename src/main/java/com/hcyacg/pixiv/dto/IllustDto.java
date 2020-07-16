package com.hcyacg.pixiv.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;

/**
 * Created: 黄智文
 * Desc: 插画详情类
 * Date: 2020/6/24 11:32
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class IllustDto implements Serializable {

    private String illust;
    private String title;
    private String type;
    private String caption;
    private String restrict;
    private String createDate;
    private String pageCount;
    private String width;
    private String height;
    private String sanityLevel;
    private String xRestrict;
    private String series;
    private String totalView;
    private String totalBookmarks;
    private String isBookmarked;
    private String visible;
    private String isMuted;
    private String totalComments;
    private String large;
    private UserDto user;
    private List<TagDto> tags;
    private List<OriginalDto> originals;

    public IllustDto(String illust, String title, String type, String caption, String restrict, String createDate, String pageCount, String width, String height, String sanityLevel, String xRestrict, String series, String totalView, String totalBookmarks, String isBookmarked, String visible, String isMuted, String totalComments, String large, UserDto user, List<TagDto> tags, List<OriginalDto> originals) {
        this.illust = illust;
        this.title = title;
        this.type = type;
        this.caption = caption;
        this.restrict = restrict;
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
        this.user = user;
        this.tags = tags;
        this.originals = originals;
    }

    public IllustDto() {
    }

    public String getIllust() {
        return illust;
    }

    public void setIllust(String illust) {
        this.illust = illust;
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

    public String getRestrict() {
        return restrict;
    }

    public void setRestrict(String restrict) {
        this.restrict = restrict;
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

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public List<TagDto> getTags() {
        return tags;
    }

    public void setTags(List<TagDto> tags) {
        this.tags = tags;
    }

    public List<OriginalDto> getOriginals() {
        return originals;
    }

    public void setOriginals(List<OriginalDto> originals) {
        this.originals = originals;
    }

    @Override
    public String toString() {
        return "illustDto{" +
                "illust='" + illust + '\'' +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", caption='" + caption + '\'' +
                ", restrict='" + restrict + '\'' +
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
                ", user=" + user +
                ", tags=" + tags +
                ", originals=" + originals +
                '}';
    }
}
