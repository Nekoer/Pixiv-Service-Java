package com.hcyacg.pixiv.utils;


import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * 随机操作工具类
 *
 * @author Administrator
 */

@Component
public class RandomUtils {
    private RandomUtils() {

    }

    /**
     * 用来生成一个32位的随机字符串
     *
     * @return 随机字符串
     */
    public  String getRandomStr() {
        return UUID.randomUUID().toString().replace("-", "");
    }


    /**
     * 生成订单编号的前缀
     *
     * @return
     */
    private  String generateOrderPrefix() {

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        //年
        int year = c.get(Calendar.YEAR);
        //一年中的第几天
        int day = c.get(Calendar.DAY_OF_YEAR);
        //一天中的第几个小时
        int hour = c.get(Calendar.HOUR_OF_DAY);

        DecimalFormat format = new DecimalFormat("000");
        String dayFmt = format.format(day);
        format = new DecimalFormat("00");
        String hourFmt = format.format(hour);
        return year - 2000 + dayFmt + hourFmt;
    }


    /**
     * 用来生成订单编号
     *
     * @return 订单编号
     */
    public  String generateOrderId() {
        DecimalFormat format = new DecimalFormat("00000000");
        return generateOrderPrefix();
    }

}
