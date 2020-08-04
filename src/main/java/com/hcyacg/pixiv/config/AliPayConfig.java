package com.hcyacg.pixiv.config;


import org.springframework.beans.factory.annotation.Autowired;
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
@Configuration
public class AliPayConfig {

    /**
     * 沙箱版
     */
    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public static String app_id = "2021001182689951";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCIROrXyeVcihSrpolONIlAnZF14KVLzpVan7MejLWL7QoZuFDxO+KhR9xzv80N8l/lgscMxEEbrOBWLLEXyKDJtu03D1PjZPysDIAP8CHZ4tQYqXh4PZHY41BOBaZ4Igr2t8e5AhzvDF3BlcvVt3/J5IazAan3HloxTOo0Iyd/Erw2u/zL3F2u3c+QYeNgFkhEkOJnyBk77cMbqH+li3mCn2JQZlGRX9JDGrKnYUq8OSGOiB/+4O0xMQbbtL6AafKU+XDxIbxZdibGBQ1AAYKLja6V7p9Ro1Gyfq6ymdVcUcgGOusyKBMYnMCTDoh5tPme9InprQs1W5eaz28D6gBlAgMBAAECggEAedICj/6xyNS4PeuXfA6DVPyH+XVm0CpSYr26B2mLMxgpm2ynD8GVjNENR30D95/iEu+iussH2ZHmy6MQfk6hTzL1Re+x8uto5vYV1im6WWrqaVG3nOaN5/Lu65EpjOQCISWSqtY0v4+ykUk0/E4bM4XU1Vp7c6vyvXghN5Ll0DbFizsy5qXS7QsJYRnVuWshNHbRkNFoKUk1bavCkVrExgvIBlixxB6jy05WKBghp6mcorz5K2zm3xuZsAzegBvFAWqAutkm6rp6REbd0qzF187p6a4bntR1CEdkrjuehIJebDYfQSaNr8aVG9GnD3pDNUwVq04lLNWazoZhSBPDUQKBgQC9ArMLvpI1QY1OY84HRPJZS7DV8uXsWlUDwVut6KY836EQZOO9YWczLlaXdLGeJu7ePoHRynnqeiump9OZ0P+pdI7zYI0uFZLFXPJ/CwH8MzEQWIf/KOvhPVlfGAqYQYEs8jkWvM/KKavYvPBKa0lOhiqWeivsGQ0Cuh/9ZcVNvwKBgQC4kORnP1fsdwqHloPa5z6X6oaR64dNkh5I7URpTVPhc1QcQNWdwwL8eZZTNE07AxuCDpj2sNGvqNXm8SMp+g3AmdZxTopOUFORPFLq8D7zOv7XWvTAOh/RgnZMuvHmC94IlXtN4Y+FOOOizivK8P/VvrpY/DznIhADDu1IIbsC2wKBgChy0HA9+zoMajoRX8Dsf1Sevxw6G/xFpJXmJkGEXTb/wI8qmBYqJxSr4PoOjghDWrES/WMxCoAkXv0OQ1lSgbP47rKkwfHPyhYUewy3BVi8d86As7HmRvxvzZVtJq9IrWU5/P+hrnIcEtuSK3YxmYGFcs27HXUsMz2XKUtoIR4vAoGAZ5u+/E7M1svEt3TBNEOYGcPoI0YqwFsEcT/wpYRpUChj0TB8OoHczUqcHr0/lZ8a9BmpuOJt88Q9v9UiPlBHJBW4EgdbELd7Eg7hJU4UqIo3wUEoIDkA36XzLwPLA68px3vSqlezL8BN2XkPkG9VzvGwvn9q2P2AK5dXkKSOzP0CgYAki/Jgi2xDwZHRRwsvDnAhMgOSVu1AK+D/IxGvTdHN1l/0Bqj3UhpowGUTkkHNsnlWfI5vvb5CnvKelOViUdSzT5G9kmAmSXXXIyH5P/E+wSbmHFjlWCOlbXYqlhtV7Vdstlc1P5uUqK19jKDosd5KFby0+o9Ws4rfrFaodjRfrA==";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApwBXRBDfk85CNj/aQe5Y6x5J25NnkXxh93569SV9JtPF7tQ6DsJ9poia0KfRYQbZQR8GHhgIu8u58OIZ+RuqsW0F2ntFouINlQOlL6jy6QVypD1wT/oOLSenk2lyH9StpOlxwW5u0NQPbnJxXCWcmWCU5jPxTM93C/aHFKYMMGyT9lWRZkEVtUVsuzl283UeaxhPlbI8yZ02/V0ZSn+iLU7ASa1j7xNFPF1Xvy88XFuWZ4OmfA6E9IYFVjIQ71HbVgkaBrlIz+YUuXMzlAGkoDsIggQ0gbkRG92YBwL02aVn29xhfcrzr/KMaKm7OiEq4yzYJHOImb31rbckvw6E9QIDAQAB";

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = "http://bot.free.idcfengye.com/pays/notify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = "http://127.0.0.1:7777";

    // 签名方式
    public static String sign_type = "RSA2";

    // 字符编码格式
    public static String charset = "utf-8";

    // 支付宝网关
    public static String gatewayUrl = "https://openapi.alipay.com/gateway.do";


    // 支付宝网关
    public static String log_path = "C:\\Users\\Administrator\\Desktop\\";


    /**
     * 正式版
     */
    //public static String app_id = "2017112000066014";
    //public static String merchant_private_key ="MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCWnz/tORj5SHuyNqH29CNevQ2ER6PC0QmiMRuZBoSFvH5Mf7+Be2IiQF3Fil6hCHjB9knMdRYac08nQZHZYfWvqs4Hnt6JzHuEoRnpBUTyH0dPGTBbbMTK4PZGAMSozpft8GAkueqIzowr1GUZ+FVKXdij6ij3D45gE00ZEmvAMdT5gxmRHq2BZT0VkAe40tF9duYDOg/HE3lglgynVlQxO7LqdRVV8pQOiM/7aOagaEoaWKtwnnIupik/GJC1S/dj/Z03s2YeVhGwM0hRs4yXTpCMXLjiEJjokkmD7fdP4oJks7VH74gse+xWfdrPDp/apRGQ7gUiy77vhNtWhcXNAgMBAAECggEBAIDocFS9rjMDJuJiQYHSdkcJs/zHN09vijgmoUD5RAgVR15/Ys4VY+p+fgKnps9ZKciO2aBDgRRIJbEqPAH5BcU/gK8i98ClofzjyAIXQWea/3Oh0jIcx9v+ZqeI1sbGcV7xf8dO2nwVkf+c+p4cXBqWcOfwavjvdmGhgzV2/4LvkUtUwfdCOTQs0qmc7gRb9MFErAVTAm+K267tyxfPej3YwUUfEYHmNe6/ju5rhZpDQ0MVhGo+7KE32zTLBiQQswUS7MKDCJLZT/DByMO1nT6N7g6Lo3+t748fVtSWO8iSPD4l99uneaBlTIaiU1HLy27AqldkalrvDfstUBO4+UECgYEA2TR7F5FN9aS0nxfVL0y4KWIrHaLvRtwlQ4LrxT2q9FG/Mtdix2RfxC6ABbgXighjku17vMUyGYLvIhE4nJlZ8KN2QF+jwhTsHOfwvYU69662M+SomyroNlM4xFkaqH2qMndRMEFZA3G5j2+UyeJBIUMzcj/CQrbByv/9UkPIa7ECgYEAsYZRvHNHaE+t+tQ6zJYH3+OR7/TpIFQIcbsGhCnrQ3pP8byY26gny+w5FvNIbx7sHxFmwSEeONJArChPXrNTfUe4SW0OEf9WMuZ+iS9wWR37RPXVoprB6ekxrNbk/zfh30XHO5ErIZwkoUO25sdjwCrJ5J6GkvGijUgguxZXLt0CgYAOaQjxm1ZMwcyp8W7XYIOnWzf5E2JElT5zqC/0Og8pfnLs8JVBHUutPFiKCJinM79HdDINaBfW0XUEEZhr61CoQnAJ/U5kBfdUQ58ZM8mhvRmhf74f7UzSrdrKTA8UtqGgD4J3bR52uAjTl1eLvVMM3eR/4/MDchSRX0JzPsQBQQKBgBrKLFeGBgalz5mEoYLyOSn+S62shenNBKE1Ghyjvs8mDB5s2Zx8WdUaRttBx3KNdhCSLbGDb+4fPIc+Zgvif/zt78+U14S7xaRUyE+niwrHzX1wo9awCAdmm4p7u38QTdPF0fUZSSr/Rz/W2ikledYsHt+TVLekI2Wyts3E5085AoGBALKNYJMYfPSXXOILXRWbOGwOn5B5f65OS0hJ1d98y+BQRu0PCaW9CbHx6PmFs0t69B30AVxFDvNzAMWpEEJTlsxuHNGTQnd2J1895hA8jN5Fbxa/8F7K0yD5fzccxd6rdfOUST8zdlMDq3i9scRS7j+tfhsOVPMCbtcfAPi0ki3N";
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    //public static String alipay_public_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCWnz/tORj5SHuyNqH29CNevQ2ER6PC0QmiMRuZBoSFvH5Mf7+Be2IiQF3Fil6hCHjB9knMdRYac08nQZHZYfWvqs4Hnt6JzHuEoRnpBUTyH0dPGTBbbMTK4PZGAMSozpft8GAkueqIzowr1GUZ+FVKXdij6ij3D45gE00ZEmvAMdT5gxmRHq2BZT0VkAe40tF9duYDOg/HE3lglgynVlQxO7LqdRVV8pQOiM/7aOagaEoaWKtwnnIupik/GJC1S/dj/Z03s2YeVhGwM0hRs4yXTpCMXLjiEJjokkmD7fdP4oJks7VH74gse+xWfdrPDp/apRGQ7gUiy77vhNtWhcXNAgMBAAECggEBAIDocFS9rjMDJuJiQYHSdkcJs/zHN09vijgmoUD5RAgVR15/Ys4VY+p+fgKnps9ZKciO2aBDgRRIJbEqPAH5BcU/gK8i98ClofzjyAIXQWea/3Oh0jIcx9v+ZqeI1sbGcV7xf8dO2nwVkf+c+p4cXBqWcOfwavjvdmGhgzV2/4LvkUtUwfdCOTQs0qmc7gRb9MFErAVTAm+K267tyxfPej3YwUUfEYHmNe6/ju5rhZpDQ0MVhGo+7KE32zTLBiQQswUS7MKDCJLZT/DByMO1nT6N7g6Lo3+t748fVtSWO8iSPD4l99uneaBlTIaiU1HLy27AqldkalrvDfstUBO4+UECgYEA2TR7F5FN9aS0nxfVL0y4KWIrHaLvRtwlQ4LrxT2q9FG/Mtdix2RfxC6ABbgXighjku17vMUyGYLvIhE4nJlZ8KN2QF+jwhTsHOfwvYU69662M+SomyroNlM4xFkaqH2qMndRMEFZA3G5j2+UyeJBIUMzcj/CQrbByv/9UkPIa7ECgYEAsYZRvHNHaE+t+tQ6zJYH3+OR7/TpIFQIcbsGhCnrQ3pP8byY26gny+w5FvNIbx7sHxFmwSEeONJArChPXrNTfUe4SW0OEf9WMuZ+iS9wWR37RPXVoprB6ekxrNbk/zfh30XHO5ErIZwkoUO25sdjwCrJ5J6GkvGijUgguxZXLt0CgYAOaQjxm1ZMwcyp8W7XYIOnWzf5E2JElT5zqC/0Og8pfnLs8JVBHUutPFiKCJinM79HdDINaBfW0XUEEZhr61CoQnAJ/U5kBfdUQ58ZM8mhvRmhf74f7UzSrdrKTA8UtqGgD4J3bR52uAjTl1eLvVMM3eR/4/MDchSRX0JzPsQBQQKBgBrKLFeGBgalz5mEoYLyOSn+S62shenNBKE1Ghyjvs8mDB5s2Zx8WdUaRttBx3KNdhCSLbGDb+4fPIc+Zgvif/zt78+U14S7xaRUyE+niwrHzX1wo9awCAdmm4p7u38QTdPF0fUZSSr/Rz/W2ikledYsHt+TVLekI2Wyts3E5085AoGBALKNYJMYfPSXXOILXRWbOGwOn5B5f65OS0hJ1d98y+BQRu0PCaW9CbHx6PmFs0t69B30AVxFDvNzAMWpEEJTlsxuHNGTQnd2J1895hA8jN5Fbxa/8F7K0yD5fzccxd6rdfOUST8zdlMDq3i9scRS7j+tfhsOVPMCbtcfAPi0ki3N";
    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //public static String notify_url = "http://hd.vipgz4.idcfengye.com/v1.0/pays/notify";
    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //public static String return_url = "http://127.0.0.1:8848/web/one/home/success.html";
    // 签名方式
    //public static String sign_type = "RSA";
    // 字符编码格式
    //public static String charset = "utf-8";
    // 支付宝网关
    //public static String gatewayUrl = "https://openapi.alipay.com/gateway.do";
    // 支付宝LOG
    //public static String log_path = "C:\\Users\\Administrator\\Desktop\\";

    /**
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     *
     * @param sWord 要写入日志里的文本内容
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis() + ".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
