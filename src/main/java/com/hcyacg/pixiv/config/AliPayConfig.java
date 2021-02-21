package com.hcyacg.pixiv.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;

/**
 * @Author: Nekoer
 * @Desc:
 * @Date: 2020/3/19 16:09
 */
@Component
@Configuration
public class AliPayConfig {

    /**
     * 沙箱版
     */
    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    @Value("${alipay.appId}")
    public  String app_id;

    // 商户私钥，您的PKCS8格式RSA2私钥 
    @Value("${alipay.privateKey}")
    public  String merchant_private_key;

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    // 
    @Value("${alipay.publickey}")
    public  String alipay_public_key;
    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public  String notify_url = "https://api.acgmx.com/pays/notify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public  String return_url = "https://www.acgmx.com";

    // 签名方式 RSA2
    @Value("${alipay.signType}")
    public  String sign_type;

    // 字符编码格式 utf-8
    @Value("${alipay.charset}")
    public  String charset;

    // 支付宝网关 https://openapi.alipay.com/gateway.do
    @Value("${alipay.gatewayHost}")
    public  String gatewayUrl;


}
