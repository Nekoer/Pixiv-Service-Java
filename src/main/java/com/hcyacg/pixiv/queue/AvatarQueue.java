package com.hcyacg.pixiv.queue;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hcyacg.pixiv.entity.Account;
import com.hcyacg.pixiv.mapper.AccountMapper;
import com.hcyacg.pixiv.utils.Base64Utils;
import com.hcyacg.pixiv.utils.MinioUilts;
import io.minio.PutObjectOptions;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Map;

/**
 * @Author: Nekoer
 * @Desc:
 * @Date: 2020/7/1 15:08
 */

@Component
@Transactional(rollbackFor = Exception.class)
public class AvatarQueue {

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private MinioUilts minioUilts;

    @Autowired
    private Base64Utils base64Utils;

    @Value("${minio.domin}")
    private String domin;

    @RabbitListener(queues = "avatar")
    public void receive(Map<String,Object> map) {
        try {
            Account account = (Account) map.get("account");
            InputStream in = base64Utils.BaseToInputStream(String.valueOf(map.get("file")).replace("data:image/png;base64,",""));
            PutObjectOptions putObjectOptions = new PutObjectOptions(in.available(), -1);
            putObjectOptions.setContentType("image/png");
            String url = minioUilts.uploadAccountAvatarBucketImage(account.getId() + account.getUserName() + ".png", in, putObjectOptions);

            account.setAvatar(domin + url);

            if (accountMapper.updateById(account) < 1) {
                throw new RuntimeException("图片上传失败");
            }

        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }

}
