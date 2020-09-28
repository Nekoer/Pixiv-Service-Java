package com.hcyacg.pixiv.utils;

import org.apache.tomcat.util.buf.UEncoder;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

@Component
public class ValidateCodeUtils {
    private ValidateCodeUtils() {

    }

    /**
     * 用来生成长度为length的随机字符串
     *
     * @param length
     * @return
     */
    private String getRandomString(int length) {
        String str = "123456789abcdefghijklmnopqrstuvwxyz";
        StringBuilder result = new StringBuilder();
        Random r = new Random();
        for (int i = 1; i <= length; i++) {
            result.append(str.charAt(r.nextInt(str.length())));
        }
        return result.toString();
    }

    /**
     * 绘制validateCode字符串对应的验证码
     */
    public Map<String, Object> drawImage() throws IOException {
        Random r = new Random();
        //1.在内存中创建画布对象
        BufferedImage image = new BufferedImage(120, 30, BufferedImage.TYPE_INT_RGB);
        //2.获取画笔对象
        Graphics g = image.getGraphics();
        //3.画背景色
        g.setColor(new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255)));
        g.fillRect(0, 0, 120, 30);
        //4.绘制字符串
        g.setFont(new Font("", Font.BOLD, 24));
        g.setColor(new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255)));
        String code = getRandomString(6);
        g.drawString(code, 20, 20);
        //5.绘制噪音线
        for (int i = 1; i <= 5; i++) {
            g.setColor(new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255)));
            g.drawLine(r.nextInt(120), r.nextInt(30), r.nextInt(120), r.nextInt(30));
        }
        //6.绘制噪音点
        for (int i = 1; i <= 50; i++) {
            g.setColor(new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255)));
            int x = r.nextInt(120);
            int y = r.nextInt(30);
            g.drawLine(x, y, x, y);
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", stream);

        BASE64Encoder encoder = new BASE64Encoder();
        //将验证码值和验证码在内存中的图片封装在Map中返回
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("code", code);
        map.put("base64", encoder.encode(stream.toByteArray()).replaceAll("\r\n", "").replaceAll("\n","").replaceAll("\\n",""));
        map.put("image", image);
        return map;
    }

}
