package com.hcyacg.pixiv.utils;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.StorageClass;
import com.qcloud.cos.region.Region;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

/**
 * @Author: Nekoer
 * @Desc: 腾讯对象存储
 * @Date: 2020/6/12 14:39
 */
@Component
public class COSUtils {

    private static final String ACCESSKEY = "";
    private static final String SECRETKEY = "";
    private static final String BUCKETNAME = "";
    private static final String APPID = "";
    private static final String REGIONID = "";


    public COSClient getClient(){
        COSCredentials cred = new BasicCOSCredentials(ACCESSKEY, SECRETKEY);
        // 2 设置bucket的区域, COS地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
        // clientConfig中包含了设置region, https(默认http), 超时, 代理等set方法, 使用可参见源码或者接口文档FAQ中说明
        ClientConfig clientConfig = new ClientConfig(new Region(REGIONID));
        // 3 生成cos客户端
        return new COSClient(cred, clientConfig);
        // bucket的命名规则为{name}-{appid} ，此处填写的存储桶名称必须为此格式
        //String bucketName = BUCKETNAME;
    }


    /**
     * 上传文件
     * @return
     * //绝对路径和相对路径都OK
     */
    public String uploadFile(String key,File file) throws IOException {
//        File localFile = new File("E:\\software\\JavaProject\\demo\\demo20\\src\\main\\resources\\1.jpg");
        PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKETNAME, key, file);

        // 设置存储类型, 默认是标准(Standard), 低频(standard_ia),一般为标准的
        putObjectRequest.setStorageClass(StorageClass.Standard);

        COSClient cosClient = getClient();
        cosClient.putObject(putObjectRequest);
            // putobjectResult会返回文件的etag
        // 关闭客户端
        cosClient.shutdown();
        Date expiration = new Date(new Date().getTime() + 5 * 60 * 10000);
        return cosClient.generatePresignedUrl(BUCKETNAME,key,expiration).toString();
    }

//    public static void main(String[] args) throws IOException {
//        System.out.println(uploadFile("1",new File("C:\\Users\\Administrator\\Desktop\\图片1.png")););
//    }
}
