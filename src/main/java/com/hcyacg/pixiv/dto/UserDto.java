package com.hcyacg.pixiv.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * @Author: Nekoer
 * @Desc: 作者类
 * @Date: 2020/6/24 11:33
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserDto implements Serializable {

    private String id;
    private String name;
    private String account;
    private String profileImageUrls;
    private String isFollowed;

    public UserDto(String id, String name, String account, String profileImageUrls, String isFollowed) {
        this.id = id;
        this.name = name;
        this.account = account;
        this.profileImageUrls = profileImageUrls;
        this.isFollowed = isFollowed;
    }

    public UserDto() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
        return "UserDto{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", account='" + account + '\'' +
                ", profileImageUrls='" + profileImageUrls + '\'' +
                ", isFollowed='" + isFollowed + '\'' +
                '}';
    }
}
