package com.hcyacg.pixiv.utils;

import java.util.Random;

public class Codeutils {
    /**
     * 生成短信随机码
     * @param length
     * @return
     */
    public static String getRandomStr(int length) {
        String str = "123456789abcdefghijklmnopqrstuvwxyz";
        StringBuilder result = new StringBuilder();
        Random r = new Random();
        for (int i = 1; i <= length; i++) {
            result.append(str.charAt(r.nextInt(str.length())));
        }
        return result.toString();
    }
}
