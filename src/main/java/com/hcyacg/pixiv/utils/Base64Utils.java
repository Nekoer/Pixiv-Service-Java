package com.hcyacg.pixiv.utils;

import com.hcyacg.pixiv.dto.Result;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Nekoer
 * @Desc:
 * @Date: 2020/6/30 18:38
 */
@Component
public class Base64Utils {

    /**
     * 将base64编码字符串转换为图片
     *
     * @return
     */
    public InputStream getImage(String base64) {
        InputStream in = null;
        try {
            byte[] decode = Base64.getDecoder().decode(base64.replace("data:image/png;base64,",""));
            in = new ByteArrayInputStream(decode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return in;
    }

    /**
     * base64转inputStream
     * @param base64string
     * @return
     */
    public InputStream BaseToInputStream(String base64string){
        ByteArrayInputStream stream = null;
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] bytes1 = decoder.decodeBuffer(base64string);
            stream = new ByteArrayInputStream(bytes1);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return stream;
    }



    public Result encodeImageToBase64(String url) throws Exception {

        //将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        //打开链接
        HttpURLConnection conn = null;
        String suffix = null;
        try {
            suffix = String.valueOf(url).substring((String.valueOf(url).lastIndexOf(".") + 1), url.toString().length());

            conn = (HttpURLConnection) new URL(url).openConnection();
            //设置请求方式为"GET"
            conn.setRequestMethod("GET");
            //超时响应时间为5秒
            conn.setConnectTimeout(5 * 1000);
            //通过输入流获取图片数据
            InputStream inStream = conn.getInputStream();
            //得到图片的二进制数据，以二进制封装得到数据，具有通用性
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            //创建一个Buffer字符串
            byte[] buffer = new byte[1024];
            //每次读取的字符串长度，如果为-1，代表全部读取完毕
            int len = 0;
            //使用一个输入流从buffer里把数据读取出来
            while ((len = inStream.read(buffer)) != -1) {
                //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
                outStream.write(buffer, 0, len);
            }

            //关闭输入流
            inStream.close();
            byte[] data = outStream.toByteArray();
            //对字节数组Base64编码
            BASE64Encoder encoder = new BASE64Encoder();
            String base64 = encoder.encode(data);
            Map<String,Object> map = new HashMap<>();
            map.put("base64",base64);
            map.put("suffix",suffix);
            return new Result(201,"获取成功",map,null);
        } catch (IOException e) {
            e.printStackTrace();
            return new Result(500,"图片上传失败,请联系客服!",null,e.getMessage());
        }
    }
}
