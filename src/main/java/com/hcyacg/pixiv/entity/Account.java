package com.hcyacg.pixiv.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

/**
 * @Author: Nekoer
 * @Desc: 用户账户
 * @Date: 2020/6/15 12:26
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@TableName(value = "account")
public class Account implements Serializable {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    @TableField(value = "username")
    private String userName;
    @TableField(value = "nickname")
    private String nickName;
    @TableField(value = "password")
    private String passWord;
    private Integer gender;
    private String phone;
    private String email;
    private Date birthday;
    @TableField(value = "haspron")
    private Integer hasPron;
    private String avatar;
    private Integer status;

    public Account(Integer id, String userName, String nickName, String passWord, Integer gender, String phone, String email, Date birthday, Integer hasPron, String avatar, Integer status) {
        this.id = id;
        this.userName = userName;
        this.nickName = nickName;
        this.passWord = passWord;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
        this.birthday = birthday;
        this.hasPron = hasPron;
        this.avatar = avatar;
        this.status = status;
    }

    public Account() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Integer getHasPron() {
        return hasPron;
    }

    public void setHasPron(Integer hasPron) {
        this.hasPron = hasPron;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", nickName='" + nickName + '\'' +
                ", passWord='" + passWord + '\'' +
                ", gender=" + gender +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", birthday=" + birthday +
                ", hasPron=" + hasPron +
                ", avatar='" + avatar + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
