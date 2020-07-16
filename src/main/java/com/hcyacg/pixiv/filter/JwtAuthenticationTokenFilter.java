package com.hcyacg.pixiv.filter;

import com.alibaba.fastjson.JSON;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.hcyacg.pixiv.bean.JwtOperation;
import com.hcyacg.pixiv.dto.Result;
import com.hcyacg.pixiv.entity.Account;
import com.hcyacg.pixiv.utils.HttpUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 这个过滤器用于拦截每次需要携带Token的请求
 * 对Token进行统一的检查
 */

public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private JwtOperation jwtOperation;
    private UserDetailsService userDetailsService;
    private PasswordEncoder encoder;
    private HttpUtils httpUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        ServletContext sc = request.getSession().getServletContext();
        WebApplicationContext cxt = WebApplicationContextUtils.getWebApplicationContext(sc);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");

        jwtOperation = cxt.getBean(JwtOperation.class);
        userDetailsService = cxt.getBean("jdbcUseService", UserDetailsService.class);
        encoder = cxt.getBean(BCryptPasswordEncoder.class);
        httpUtils = cxt.getBean(HttpUtils.class);
        //获取请求头中的token
        String token = request.getHeader("authorization");
        //这里的校验合法性操作应当在业务层中完成
        try {
            //头信息中没有token
            if (StringUtils.isBlank(token)) {
                throw new BadCredentialsException("没有携带Token信息");
            }
            //获取Claims
            Claims claims = jwtOperation.parseJwt(token);
            Object userClaim = claims.get("account");
            if (null == claims || null == userClaim) {
                throw new BadCredentialsException("无效的Token");
            }
            //获取用户信息
            Account account = JSON.parseObject(String.valueOf(claims.get("account")), Account.class);
            String username = account.getUserName();
            //这里获取的是JWT中存的密文密码
            String pwd = account.getPassWord();
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                //数据库获取当前用户信息
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                //数据库密码和JWT密码不一致，说明密码可能被篡改了
                if (!pwd.equals(userDetails.getPassword())) {
                    throw new BadCredentialsException("密码可能被篡改了");
                }

                //构建认证用户及权限对象
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        username, null, userDetails.getAuthorities());
                //将权限写入本次会话
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            //执行过滤链中后续的过滤器
            chain.doFilter(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof ExpiredJwtException) {
                response.getWriter().print(JSON.toJSONString(httpUtils.setBuild(response,new Result(401,"登录信息已过期",null,e.getMessage()))));
            }
            if (e instanceof BadCredentialsException) {
                response.getWriter().print(JSON.toJSONString(httpUtils.setBuild(response,new Result(401,"服务器错误",null,e.getMessage()))));
            } else {
                response.getWriter().print(JSON.toJSONString(httpUtils.setBuild(response,new Result(500,"服务器错误",null,e.getMessage()))));
            }
        }

    }
}