package com.hcyacg.pixiv.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * @Author: Nekoer
 * @Desc: 用户权限
 * @Date: 2020/7/8 10:54
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@TableName(value = "account_role")
public class AccountRole  implements Serializable {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    @TableField(value = "account_id")
    private Integer accountId;
    @TableField(value = "role_id")
    private Integer roleId;

    public AccountRole(Integer id, Integer accountId, Integer roleId) {
        this.id = id;
        this.accountId = accountId;
        this.roleId = roleId;
    }

    public AccountRole() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    @Override
    public String toString() {
        return "AccountRole{" +
                "id=" + id +
                ", accountId=" + accountId +
                ", roleId=" + roleId +
                '}';
    }
}
