package com.hcyacg.pixiv.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hcyacg.pixiv.dto.Result;
import com.hcyacg.pixiv.dto.VipDto;
import com.hcyacg.pixiv.entity.Vip;
import com.hcyacg.pixiv.entity.VipPackage;
import com.hcyacg.pixiv.mapper.VipMapper;
import com.hcyacg.pixiv.mapper.VipPackageMapper;
import com.hcyacg.pixiv.service.VipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

/**
 * Created: 黄智文
 * Desc:
 * Date: 2020/7/14 14:32
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class VipServiceImpl implements VipService {
    @Autowired
    private VipMapper vipMapper;
    @Autowired
    private VipPackageMapper vipPackageMapper;

    @Override
    public Result getVip() {
        try{

            List<VipDto> vipDtos = new LinkedList<>();
            List<Vip> vips = vipMapper.selectList(null);
            for (int i = 0; i < vips.size(); i++) {
                VipDto vipDto = new VipDto();
                vipDto.setId(vips.get(i).getId());
                vipDto.setName(vips.get(i).getName());

                List<VipPackage> vipPackages = vipPackageMapper.selectList(new QueryWrapper<VipPackage>().eq("vip_id",vips.get(i).getId()));
                vipDto.setVipPackages(vipPackages);
                vipDtos.add(vipDto);
            }

            return new Result(201,"获取成功",vipDtos,null);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(500,"获取失败",null,e.getMessage());
        }
    }
}
