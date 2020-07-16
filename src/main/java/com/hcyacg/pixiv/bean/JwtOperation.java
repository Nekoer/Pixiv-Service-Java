package com.hcyacg.pixiv.bean;

import com.alibaba.fastjson.JSON;


import com.alibaba.fastjson.JSONObject;
import com.hcyacg.pixiv.entity.Account;
import com.sun.org.apache.xpath.internal.operations.Bool;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wf
 * @createTime 2020/03/11 14:08
 * 这里将JWT操作封装成了一个SpringBean，在使用的地方直接@Autowired注入即可
 */
@Component
public class JwtOperation {

    /**
     * 配置文件中的盐值
     */
    @Value("${jwt.config.secret}")
    private String secret;

    /**
     * 配置文件中的超时时间，-1代表永久，否则单位是秒
     */
    @Value("${jwt.config.expire}")
    private Long expire;


    /**
     * 创建JWT 【这个方法用于SpringSecurity与JWT整合的案例中】
     *
     * @param account 使用数据库中合法的用户对象的相关信息来创建JWT
     * @return 创建的JWT的值
     */
    public String createJwt(Account account) {
        JwtBuilder jwtBuilder = Jwts.builder();
        //将得到的用户对象放入声明中，这里不能写实体类对象，那就写成JSON字符串
        Map<String, Object> maps = new HashMap<>();
        maps.put("account", JSONObject.toJSONString(account));
        jwtBuilder.setClaims(maps);
        jwtBuilder.setId(String.valueOf(account.getId()));
        jwtBuilder.setSubject(account.getUserName());
        jwtBuilder.setIssuedAt(new Date());
        if (null != expire && !expire.equals(-1L)) {
            jwtBuilder.setExpiration(new Date(System.currentTimeMillis() + expire));
        }
        jwtBuilder.signWith(SignatureAlgorithm.HS256, secret);
        return jwtBuilder.compact();
    }
    /**
     * 创建JWT 【这个方法用于SpringSecurity与JWT整合的案例中】
     *
     * @param user 使用数据库中合法的管理员对象的相关信息来创建JWT
     * @return 创建的JWT的值
     */
//    public String createJwt(Account user, Long expire, Integer role) {
//        JwtBuilder jwtBuilder = Jwts.builder();
//        jwtBuilder.setId(String.valueOf(user.getId()));
//        jwtBuilder.setSubject(user.getUserName());
//        jwtBuilder.setIssuedAt(new Date());
//        if (null != expire && !expire.equals(-1L)) {
//            jwtBuilder.setExpiration(new Date(System.currentTimeMillis() + expire));
//        }
//        jwtBuilder.signWith(SignatureAlgorithm.HS256, secret);
//        //将得到的用户对象放入声明中，这里不能写实体类对象，那就写成JSON字符串
//        Map<String, Object> maps = new HashMap<>();
//        maps.put("user", user);
//        maps.put("role", role);
//        jwtBuilder.setClaims(maps);
//        return jwtBuilder.compact();
//    }


    /**
     * 创建他人调用的APIKEY
     * @param account
     * @return
     */
    public String createOtherToken(Account account,String uuid) {
        JwtBuilder jwtBuilder = Jwts.builder();
        //将得到的用户对象放入声明中，这里不能写实体类对象，那就写成JSON字符串
        Map<String, Object> maps = new HashMap<>();
        maps.put("account", JSONObject.toJSONString(account));
        maps.put("uuid", uuid);
        jwtBuilder.setClaims(maps);
        jwtBuilder.setId(String.valueOf(account.getId()));
        jwtBuilder.setSubject(account.getUserName());
        jwtBuilder.setIssuedAt(new Date());
        jwtBuilder.signWith(SignatureAlgorithm.HS256, secret);
        return jwtBuilder.compact();
    }

    /**
     * 解析JWT
     *
     * @param jwt 要解析的JWT
     * @return 解析后的结果
     */
    public Claims parseJwt(String jwt) {
        JwtParser jwtParser = Jwts.parser();
        return jwtParser.setSigningKey(secret).parseClaimsJws(jwt).getBody();
    }

    /**
     * 解析JWT 判断是否过期
     *
     * @param jwt 要解析的JWT
     * @return 解析后的结果
     */
    public Boolean parseOverdue(String jwt) {
        try{
            JwtParser jwtParser = Jwts.parser();
            return jwtParser.setSigningKey(secret).parseClaimsJws(jwt).getBody().getExpiration().getTime() > new Date().getTime();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
