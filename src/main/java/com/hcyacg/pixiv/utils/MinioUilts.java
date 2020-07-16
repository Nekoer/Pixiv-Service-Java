package com.hcyacg.pixiv.utils;

import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created: 黄智文
 * Desc: Minio工具包
 * Date: 2020/6/23 14:38
 */
@Component
public class MinioUilts {

    @Value("${minio.host}")
    private String host;

    @Value("${minio.domin}")
    private String domin;

    @Value("${minio.accessKey}")
    private String AccessKey;

    @Value("${minio.secretKey}")
    private String SecretKey;

    @Value("${minio.thumbBucket}")
    private String thumbBucket;

    @Value("${minio.originalBucket}")
    private String originalBucket;

    @Value("${minio.avatarBucket}")
    private String avatarBucket;

    @Value("${minio.accountBucket}")
    private String accountBucket;


    public String uploadThumbImage(String fileName, InputStream in,PutObjectOptions putObjectOptions){
        try {

            MinioClient minioClient = new MinioClient(host, AccessKey, SecretKey);
            //MinioClient minioClient = new MinioClient("http://103.133.179.35:9000/", "admin", "243462032");
            boolean isExist = minioClient.bucketExists(thumbBucket);
            if(isExist) {
                System.out.println("Bucket already exists.");
            } else {
                minioClient.makeBucket(thumbBucket);
            }


            minioClient.putObject(thumbBucket,fileName,in,putObjectOptions);

            return minioClient.getObjectUrl(thumbBucket,fileName).replace(host,"");
        } catch(MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            System.out.println("Error occurred: " + e);
            return null;
        }
    }

    public String uploadOriginalImage(String fileName, InputStream in,PutObjectOptions putObjectOptions){
        try {

            MinioClient minioClient = new MinioClient(host, AccessKey, SecretKey);
            //MinioClient minioClient = new MinioClient("http://103.133.179.35:9000/", "admin", "243462032");
            boolean isExist = minioClient.bucketExists(originalBucket);
            if(isExist) {
                System.out.println("Bucket already exists.");
            } else {
                minioClient.makeBucket(originalBucket);
            }

            minioClient.putObject(originalBucket,fileName,in,putObjectOptions);

            return minioClient.getObjectUrl(originalBucket,fileName).replace(host,"");
        } catch(MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            System.out.println("Error occurred: " + e);
            return null;
        }
    }

    public String uploadAvatarBucketImage(String fileName, InputStream in,PutObjectOptions putObjectOptions){
        try {

            MinioClient minioClient = new MinioClient(host, AccessKey, SecretKey);
            //MinioClient minioClient = new MinioClient("http://103.133.179.35:9000/", "admin", "243462032");
            boolean isExist = minioClient.bucketExists(avatarBucket);
            if(isExist) {
                System.out.println("Bucket already exists.");
            } else {
                minioClient.makeBucket(avatarBucket);
            }

            minioClient.putObject(avatarBucket,fileName,in,putObjectOptions);

            return minioClient.getObjectUrl(avatarBucket,fileName).replace(host,"");
        } catch(MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            System.out.println("Error occurred: " + e);
            return null;
        }
    }

    public String uploadAccountAvatarBucketImage(String fileName, InputStream in,PutObjectOptions putObjectOptions){
        try {

            MinioClient minioClient = new MinioClient(host, AccessKey, SecretKey);
            //MinioClient minioClient = new MinioClient("http://103.133.179.35:9000/", "admin", "243462032");
            boolean isExist = minioClient.bucketExists(accountBucket);
            if(isExist) {
                System.out.println("Bucket already exists.");
            } else {
                minioClient.makeBucket(accountBucket);
            }

            minioClient.putObject(accountBucket,fileName,in,putObjectOptions);

            return minioClient.getObjectUrl(accountBucket,fileName).replace(host,"");
        } catch(MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            System.out.println("Error occurred: " + e);
            return null;
        }
    }

    public static void main(String[] args) throws IOException {
        MinioUilts minioUilts = new MinioUilts();
        FileInputStream fileInputStream = new FileInputStream(new File("C:\\Users\\Administrator\\Desktop\\图片1.png"));
        PutObjectOptions putObjectOptions = new PutObjectOptions(fileInputStream.available(), -1);
        putObjectOptions.setContentType("image/png");
        minioUilts.uploadThumbImage("1.png",fileInputStream,putObjectOptions);
    }
}
