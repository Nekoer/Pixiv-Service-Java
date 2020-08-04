package com.hcyacg.pixiv.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * @Author: Nekoer
 * @Desc: pixiv用户
 * @Date: 2020/6/23 18:17
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@TableName(value = "user")
public class User implements Serializable {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    @TableField(value = "userid")
    private String userId;
    private String name;
    private String account;
    @TableField(value = "profile_image_urls")
    private String profileImageUrls;
    @TableField(value = "is_followed")
    private String isFollowed;

    public User(Integer id, String userId, String name, String account, String profileImageUrls, String isFollowed) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.account = account;
        this.profileImageUrls = profileImageUrls;
        this.isFollowed = isFollowed;
    }

    public User() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getProfileImageUrls() {
        return profileImageUrls;
    }

    public void setProfileImageUrls(String profileImageUrls) {
        this.profileImageUrls = profileImageUrls;
    }

    public String getIsFollowed() {
        return isFollowed;
    }

    public void setIsFollowed(String isFollowed) {
        this.isFollowed = isFollowed;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", account='" + account + '\'' +
                ", profileImageUrls='" + profileImageUrls + '\'' +
                ", isFollowed='" + isFollowed + '\'' +
                '}';
    }
}
