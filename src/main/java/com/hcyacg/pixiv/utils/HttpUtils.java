package com.hcyacg.pixiv.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.hcyacg.pixiv.constant.AppConstant;
import com.hcyacg.pixiv.dto.Result;
import com.hcyacg.pixiv.entity.Log;
import com.hcyacg.pixiv.mapper.LogMapper;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created: 黄智文
 * Desc:
 * Date: 2020/5/12 18:12
 */
@Component
public class HttpUtils {
    @Value(value = "${pixiv.proxy.host}")
    private  String proxyHost;

    @Value(value = "${pixiv.proxy.port}")
    private  Integer proxyPort;

    @Value(value = "${pixiv.proxy.http.type}")
    private  String proxyType;



    public HttpUtils() {
    }

    public  HttpGet get(String url){
        HttpGet httpGet = new HttpGet(url);
        if (!StringUtils.isBlank(proxyHost) && null != proxyPort && !StringUtils.isBlank(proxyType)) {
            HttpHost proxy = new HttpHost(proxyHost, proxyPort, proxyType);
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(2000).setProxy(proxy).build();
            httpGet.setConfig(requestConfig);
            httpGet.addHeader("x-forwarded-for","127.0.0.1:7890");
        }

        return httpGet;
    }

    public  HttpPost post(String url, List<NameValuePair> params) throws UnsupportedEncodingException {
        HttpPost httpPost = new HttpPost(url);

        if (!StringUtils.isBlank(proxyHost) &&  null != proxyPort && !StringUtils.isBlank(proxyType)) {
            HttpHost proxy = new HttpHost(proxyHost, proxyPort, proxyType);
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(2000).setProxy(proxy).build();
            httpPost.setConfig(requestConfig);
            httpPost.addHeader("x-forwarded-for","127.0.0.1:7890");

        }
        UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params, "UTF-8");
        httpPost.setEntity(formEntity);
        return httpPost;
    }

    public Result setBuild(HttpServletResponse res, Result result) {

        if (AppConstant.HTTP_CODE_SUCCESS.contains(result.getCode())) {
            return buildSuccess(res, result.getCode(), result.getData(),String.valueOf(result.getMsg()));
        } else {
            return buildFailure(res, result.getCode(), String.valueOf(result.getError()),String.valueOf(result.getMsg()));
        }
    }

    public Result buildSuccess(HttpServletResponse res, Integer code, Object data,String msg) {

        Result result = new Result();
        try{
            res.setStatus(code);
        }catch (Exception e){
            e.printStackTrace();
        }

        result.setMsg(msg);
        result.setCode(code);
        result.setData(data);
        return result;
    }

    public Result buildFailure(HttpServletResponse res, Integer code, String errMsg ,String msg) {
        Result result = new Result();
        try{
            res.setStatus(code);
        }catch (Exception e){
            e.printStackTrace();
        }
        result.setCode(code);
        result.setMsg(msg);
        result.setError(errMsg);
        return result;
    }
}
