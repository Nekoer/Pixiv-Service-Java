package com.hcyacg.pixiv.filter;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.hcyacg.pixiv.constant.AppConstant;
import com.hcyacg.pixiv.dto.Result;
import com.hcyacg.pixiv.entity.Token;
import com.hcyacg.pixiv.mapper.TokenMapper;
import com.hcyacg.pixiv.mapper.LogMapper;
import com.hcyacg.pixiv.utils.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.Security;
import java.util.*;

/**
 * @Author: Nekoer
 * @Desc:
 * @Date: 2020/5/14 20:26
 */

@WebFilter("/*")
@Transactional(rollbackFor = Exception.class)
public class TokenFilter implements Filter {

    private static ThreadLocal<HttpServletRequest> curRequest = new ThreadLocal<>();
    private static ThreadLocal<HttpServletResponse> curResponse = new ThreadLocal<>();

    /**
     * 获取当前的请求对象
     *
     * @return 请求对象
     */
    public static HttpServletRequest getCurRequest() {
        return curRequest.get();
    }

    /**
     * 获取当前的响应对象
     *
     * @return 响应对象
     */
    public static HttpServletResponse getCurResponse() {
        return curResponse.get();
    }


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;
        ServletContext sc = req.getSession().getServletContext();
        WebApplicationContext cxt = WebApplicationContextUtils.getWebApplicationContext(sc);
        RedisUtils redisUtils = cxt.getBean(RedisUtils.class);
        IpUtils ipUtils = cxt.getBean(IpUtils.class);
        RabbitUtils rabbitUtils = cxt.getBean(RabbitUtils.class);
        LogMapper logMapper = cxt.getBean(LogMapper.class);
        TokenMapper accessTokenMapper = cxt.getBean(TokenMapper.class);


        res.setHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));
        res.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE,PUT,PATCH");
        res.setHeader("Access-Control-Max-Age", "0");
        res.setHeader("Access-Control-Allow-Headers", "*,authorization");
        res.setHeader("Access-Control-Allow-Credentials", "true");
        res.setHeader("XDomainRequestAllowed", "1");

        //设置编码格式
        res.setCharacterEncoding("UTF-8");
        res.setContentType("application/json; charset=utf-8");

//        System.out.println(AppConstant.SDF.format(new Date()) + "调用");
        curRequest.set(req);
        curResponse.set(res);

        String ip = "";
        try {
            ip = ipUtils.getIpAddr(req);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (req.getMethod().equals("OPTIONS")) {
            filterChain.doFilter(req, res);
            return;
        } else if (null != req.getHeader("referer")) {
            if (req.getHeader("referer").contains("https://www.acg-gov.com") || req.getHeader("referer").contains("https://m.acg-gov.com") || req.getHeader("referer").contains("https://api.hcyacg.com")) {
                filterChain.doFilter(req, res);
                return;
            }
        }


        try {

            //添加访问次数
            rabbitUtils.convertAndSend("log", 1);

            //判断权限
            String token = req.getHeader("token");
            //token不为空
            if (StringUtils.isNotBlank(token)) {
                //判断token是否存在数据库中
                List<Token> accessToken = accessTokenMapper.selectList(new QueryWrapper<Token>().eq("token", token));
                if (accessToken.size() > 0) {

                    //增加用户调用次数
                    if (accessTokenMapper.selectCount(new QueryWrapper<Token>().eq("token", String.valueOf(token))) > 0) {
                        Token tokenData = accessTokenMapper.selectOne(new QueryWrapper<Token>().eq("token", String.valueOf(token)));
                        if (tokenData.getTotal() == null || tokenData.getTotal() <= 0){
                            tokenData.setTotal(1);
                        }else {
                            tokenData.setTotal(tokenData.getTotal() + 1);
                        }
                        if (accessTokenMapper.updateById(tokenData) < 1) {
                            throw new RuntimeException("添加失败");
                        }
                        System.out.println("用户id" + tokenData.getAccountId() + "添加次数");
                    }
                    //判断token是否存在redis缓存中
                    if (!StringUtils.isBlank(String.valueOf(redisUtils.get(token))) && String.valueOf(redisUtils.get(token)).matches(AppConstant.ISNUMBER)) {
                        int number = Integer.parseInt(String.valueOf(redisUtils.get(token)));

                        //调用次数已用完
                        if (number <= 0) {
                            res.getWriter().write(JSONObject.toJSONString(new Result(400, "请求失败", "", "调用次数已归零，请1分钟后再试")));
                            return;
                        }

                        //自减1次
                        redisUtils.decr(token, 1);
                    } else {
                        //添加token缓存,进行1分钟时间策略
                        redisUtils.set(accessToken.get(0).getToken(), accessToken.get(0).getLine() - 1, 60L);
                    }
                    filterChain.doFilter(req, res);
                } else {
                    res.getWriter().write(JSONObject.toJSONString(new Result(400, "请求失败", "", "该token不存在数据库中")));
                }
            } else {
                //token未填写,根据ip来限制
                try {
                    ip = ipUtils.getIpAddr(req);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                System.out.println(ip);
                if (ip.matches("^((25[0-5]|2[0-4]\\d|[1]{1}\\d{1}\\d{1}|[1-9]{1}\\d{1}|\\d{1})($|(?!\\.$)\\.)){4}$")) {
                    //如果redis缓存中存在token，则进行自减
                    if (redisUtils.hasKey(ip)) {
                        if (String.valueOf(redisUtils.get(ip)).matches(AppConstant.ISNUMBER) && Integer.parseInt(String.valueOf(redisUtils.get(ip))) > 0) {
                            redisUtils.decr(ip, 1);
                            filterChain.doFilter(req, res);
                        } else {
                            res.getWriter().write(JSONObject.toJSONString(new Result(400, "请求失败", "", "调用次数已超标,请一分钟后再试")));
                        }
                    } else {
                        redisUtils.set(ip, 10, 60L);
                        filterChain.doFilter(req, res);
                    }


                } else {
                    //如果获取不到ip，则建立公共调用凭证限制次数
                    if (!String.valueOf(redisUtils.get("unKnownHost")).matches(AppConstant.ISNUMBER) || StringUtils.isBlank(String.valueOf(redisUtils.get("unKnownHost")))) {
                        redisUtils.set("unKnownHost", 5, 60L);
                        filterChain.doFilter(req, res);
                    } else {
                        if (String.valueOf(redisUtils.get("unKnownHost")).matches(AppConstant.ISNUMBER) && Integer.parseInt(String.valueOf(redisUtils.get("unKnownHost"))) <= 0) {
                            res.getWriter().write(JSONObject.toJSONString(new Result(400, "请求失败", "", "调用次数已超标,请一分钟后再试")));
                        } else {
                            redisUtils.decr("unKnownHost", 1);
                            filterChain.doFilter(req, res);
                        }
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            res.getWriter().write(JSONObject.toJSONString(new Result(500, "请求失败", "", "错误" + e.getMessage())));
        } finally {
            curRequest.remove();
            curResponse.remove();
        }
    }


    @Override
    public void destroy() {

    }


}
