package com.hcyacg.pixiv.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hcyacg.pixiv.entity.VipPackage;

import java.io.Serializable;
import java.util.List;

/**
 * Created: 黄智文
 * Desc:
 * Date: 2020/7/14 14:38
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class VipDto implements Serializable {
    private Integer id;
    private String name;
    private List<VipPackage> vipPackages;

    public VipDto(Integer id, String name, List<VipPackage> vipPackages) {
        this.id = id;
        this.name = name;
        this.vipPackages = vipPackages;
    }

    public VipDto() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<VipPackage> getVipPackages() {
        return vipPackages;
    }

    public void setVipPackages(List<VipPackage> vipPackages) {
        this.vipPackages = vipPackages;
    }

    @Override
    public String toString() {
        return "VipDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", vipPackages=" + vipPackages +
                '}';
    }
}
