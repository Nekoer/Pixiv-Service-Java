package com.hcyacg.pixiv.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;

/**
 * Created: 黄智文
 * Desc:
 * Date: 2020/5/14 21:36
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@TableName(value = "token")
public class Token implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer Id;
    @TableField(value = "token")
    private String token;
    @TableField(value = "line")
    private Integer line;
    @TableField(value = "account_id")
    private Integer accountId;

    public Token(Integer id, String token, Integer line, Integer accountId) {
        Id = id;
        this.token = token;
        this.line = line;
        this.accountId = accountId;
    }

    public Token() {
    }

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getLine() {
        return line;
    }

    public void setLine(Integer line) {
        this.line = line;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    @Override
    public String toString() {
        return "Token{" +
                "Id=" + Id +
                ", token='" + token + '\'' +
                ", line=" + line +
                ", accountId=" + accountId +
                '}';
    }
}
