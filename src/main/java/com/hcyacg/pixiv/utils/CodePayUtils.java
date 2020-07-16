package com.hcyacg.pixiv.utils;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created: 黄智文
 * Desc: 支付工具类
 * Date: 2020/7/13 12:17
 */
@Component
public class CodePayUtils {

    @Value("${code.pay.id}")
    private Integer id;
    @Value("${code.pay.key}")
    private String key;
    @Value("${code.pay.url}")
    private String url;
    @Value("${code.pay.type}")
    private Integer type;
    @Value("${code.pay.notifyUrl}")
    private String notifyUrl;
    @Value("${code.pay.returnUrl}")
    private String returnUrl;

    public String createUrl(Integer type,Object payId, BigDecimal price, Object param) {
        StringBuilder sb = new StringBuilder();
        sb.append("id=").append(id);
        if (StringUtils.isNotBlank(notifyUrl)) {
            sb.append("&notify_url=").append(notifyUrl);
        }
        sb.append("&param=").append(param);
        sb.append("&pay_id=").append(payId);
        sb.append("&price=").append(price);
        if (StringUtils.isNotBlank(returnUrl)) {
            sb.append("&return_url=").append(returnUrl);
        }
        sb.append("&type=").append(type);

        String sign = DigestUtils.md5Hex(sb.toString() + key);
        sb.append("&sign=").append(sign);
        return url + "?" + sb;
    }

    public String parse(HttpServletRequest request) throws NoSuchAlgorithmException {
        Map<String, String> params = new HashMap<String, String>(); //申明hashMap变量储存接收到的参数名用于排序
        Map requestParams = request.getParameterMap(); //获取请求的全部参数

        String valueStr = ""; //申明字符变量 保存接收到的变量
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {

            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            valueStr = values[0];
            //乱码解决，这段代码在出现乱码时使用。如果sign不相等也可以使用这段代码转化
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
            System.out.println(name + "=>" + valueStr);
            params.put(name, valueStr);//增加到params保存
        }
        List<String> keys = new ArrayList<String>(params.keySet()); //转为数组
        Collections.sort(keys); //重新排序
        String prestr = "";
        String sign = params.get("sign"); //获取接收到的sign 参数

        for (int i = 0; i < keys.size(); i++) { //遍历拼接url 拼接成a=1&b=2 进行MD5签名
            String key_name = keys.get(i);
            String value = params.get(key_name);
            if (value == null || value.equals("") || key_name.equals("sign")) { //跳过这些 不签名
                continue;
            }
            if (prestr.equals("")) {
                prestr = key_name + "=" + value;
            } else {
                prestr = prestr + "&" + key_name + "=" + value;
            }
        }
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update((prestr + key).getBytes());
        String mySign = new BigInteger(1, md.digest()).toString(16).toLowerCase();
        if (mySign.length() != 32) mySign = "0" + mySign;
        if (mySign.equals(sign)) {

//            sign=>f579cf6c3ef11e0dd356c9827e04665f
//            codepay_server_time=>1594722988
//            endTime=>1594723333
//            id=>164770954299
//            mode=>0
//            money=>1.00
//            notify_count=>0
//            ok=>1
//            param=>月费大会员
//            pay_id=>1
//            pay_no=>101000026901502007141424262159
//            pay_time=>1594722986
//            price=>1.00
//            qr_user=>0
//            status=>0
//            tag=>0
//            target=>get
//            trade_no=>115947229732473873137484834
//            trueID=>47387
//            type=>2
//            userID=>47387
            //编码要匹配 编码不一致中文会导致加密结果不一致
            //参数合法处理业务
            //request.getParameter("pay_no") 流水号
            //request.getParameter("pay_id") 用户唯一标识
            //request.getParameter("money") 付款金额
            //request.getParameter("price") 提交的金额
            return "ok";
        } else {
            //参数不合法
            return "fail";
        }
    }

}
