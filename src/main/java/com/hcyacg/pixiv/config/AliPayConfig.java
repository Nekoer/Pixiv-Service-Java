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
    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号 2021001182689951
    @Value("${alipay.appId}")
    public  String app_id;

    // 商户私钥，您的PKCS8格式RSA2私钥 MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCIROrXyeVcihSrpolONIlAnZF14KVLzpVan7MejLWL7QoZuFDxO+KhR9xzv80N8l/lgscMxEEbrOBWLLEXyKDJtu03D1PjZPysDIAP8CHZ4tQYqXh4PZHY41BOBaZ4Igr2t8e5AhzvDF3BlcvVt3/J5IazAan3HloxTOo0Iyd/Erw2u/zL3F2u3c+QYeNgFkhEkOJnyBk77cMbqH+li3mCn2JQZlGRX9JDGrKnYUq8OSGOiB/+4O0xMQbbtL6AafKU+XDxIbxZdibGBQ1AAYKLja6V7p9Ro1Gyfq6ymdVcUcgGOusyKBMYnMCTDoh5tPme9InprQs1W5eaz28D6gBlAgMBAAECggEAedICj/6xyNS4PeuXfA6DVPyH+XVm0CpSYr26B2mLMxgpm2ynD8GVjNENR30D95/iEu+iussH2ZHmy6MQfk6hTzL1Re+x8uto5vYV1im6WWrqaVG3nOaN5/Lu65EpjOQCISWSqtY0v4+ykUk0/E4bM4XU1Vp7c6vyvXghN5Ll0DbFizsy5qXS7QsJYRnVuWshNHbRkNFoKUk1bavCkVrExgvIBlixxB6jy05WKBghp6mcorz5K2zm3xuZsAzegBvFAWqAutkm6rp6REbd0qzF187p6a4bntR1CEdkrjuehIJebDYfQSaNr8aVG9GnD3pDNUwVq04lLNWazoZhSBPDUQKBgQC9ArMLvpI1QY1OY84HRPJZS7DV8uXsWlUDwVut6KY836EQZOO9YWczLlaXdLGeJu7ePoHRynnqeiump9OZ0P+pdI7zYI0uFZLFXPJ/CwH8MzEQWIf/KOvhPVlfGAqYQYEs8jkWvM/KKavYvPBKa0lOhiqWeivsGQ0Cuh/9ZcVNvwKBgQC4kORnP1fsdwqHloPa5z6X6oaR64dNkh5I7URpTVPhc1QcQNWdwwL8eZZTNE07AxuCDpj2sNGvqNXm8SMp+g3AmdZxTopOUFORPFLq8D7zOv7XWvTAOh/RgnZMuvHmC94IlXtN4Y+FOOOizivK8P/VvrpY/DznIhADDu1IIbsC2wKBgChy0HA9+zoMajoRX8Dsf1Sevxw6G/xFpJXmJkGEXTb/wI8qmBYqJxSr4PoOjghDWrES/WMxCoAkXv0OQ1lSgbP47rKkwfHPyhYUewy3BVi8d86As7HmRvxvzZVtJq9IrWU5/P+hrnIcEtuSK3YxmYGFcs27HXUsMz2XKUtoIR4vAoGAZ5u+/E7M1svEt3TBNEOYGcPoI0YqwFsEcT/wpYRpUChj0TB8OoHczUqcHr0/lZ8a9BmpuOJt88Q9v9UiPlBHJBW4EgdbELd7Eg7hJU4UqIo3wUEoIDkA36XzLwPLA68px3vSqlezL8BN2XkPkG9VzvGwvn9q2P2AK5dXkKSOzP0CgYAki/Jgi2xDwZHRRwsvDnAhMgOSVu1AK+D/IxGvTdHN1l/0Bqj3UhpowGUTkkHNsnlWfI5vvb5CnvKelOViUdSzT5G9kmAmSXXXIyH5P/E+wSbmHFjlWCOlbXYqlhtV7Vdstlc1P5uUqK19jKDosd5KFby0+o9Ws4rfrFaodjRfrA==
    @Value("${alipay.privateKey}")
    public  String merchant_private_key;

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    // MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApwBXRBDfk85CNj/aQe5Y6x5J25NnkXxh93569SV9JtPF7tQ6DsJ9poia0KfRYQbZQR8GHhgIu8u58OIZ+RuqsW0F2ntFouINlQOlL6jy6QVypD1wT/oOLSenk2lyH9StpOlxwW5u0NQPbnJxXCWcmWCU5jPxTM93C/aHFKYMMGyT9lWRZkEVtUVsuzl283UeaxhPlbI8yZ02/V0ZSn+iLU7ASa1j7xNFPF1Xvy88XFuWZ4OmfA6E9IYFVjIQ71HbVgkaBrlIz+YUuXMzlAGkoDsIggQ0gbkRG92YBwL02aVn29xhfcrzr/KMaKm7OiEq4yzYJHOImb31rbckvw6E9QIDAQAB
    @Value("${alipay.publickey}")
    public  String alipay_public_key;
    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public  String notify_url = "https://api.acg-gov.com/pays/notify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public  String return_url = "https://www.acg-gov.com";

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
