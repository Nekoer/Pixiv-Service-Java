package com.hcyacg.pixiv.service;


import com.hcyacg.pixiv.dto.Result;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.CompletableFuture;

/**
 * Created: 黄智文
 * Desc: 插图业务层接口
 * Date: 2020/5/12 17:39
 */
public interface IllustService {

    /**
     * 插画细节
     * @param illustId 插画id
     * @param authorization
     * @param reduction 是否还原
     * @return 返回插画详细数据
     */
    Result detail(String illustId,String authorization,boolean reduction);
    /**
     * 插画评论
     * @param illustId 插画id
     * @return 返回插画详细评论
     */
    String comments(String illustId);


    /**
     * 直接通过图片链接访问获取图片流
     * @param url 图片路径
     */
    ByteArrayOutputStream urlLook(String url, Boolean cache);


    /**
     * 插画排行榜
     * @param mode 类别
     * @param date 时间
     * @param offset 张数
     * @return 返回
     */
    String ranking(String mode,String date,String offset);

    /**
     * 获取图片数量
     * @return
     */
    Result getPicNumber();
}
