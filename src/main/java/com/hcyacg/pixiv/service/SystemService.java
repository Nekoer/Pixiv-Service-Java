package com.hcyacg.pixiv.service;

import com.hcyacg.pixiv.dto.Result;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Created: 黄智文
 * Desc: 开放接口
 * Date: 2020/7/4 17:58
 */
public interface SystemService {

    /**
     * 手动启动爬虫系统
     * @return 返回是否成功
     */
    //@PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN')")
    Result runCrawlData();

    /**
     * 判断是否是管理员
     * @return
     */
    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN','ROLE_ADMIN')")
    Result hasRole();

    /**
     * 获取接口近一周的调用频率
     * @return
     */
    Result log();
}
