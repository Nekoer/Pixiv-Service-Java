package com.hcyacg.pixiv.config;

import com.alibaba.fastjson.JSONObject;
import com.hcyacg.pixiv.dto.Result;
import com.hcyacg.pixiv.filter.JwtAuthenticationTokenFilter;
import com.hcyacg.pixiv.filter.TokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @Author: Nekoer
 * @Desc: 安全框架配置
 * @Date: 2020/7/8 11:13
 */

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    //注入密码加密器
    @Autowired
    private PasswordEncoder encoder;

    //注入JdbcTemplate
    @Autowired
    private JdbcTemplate jdbcTemplate;

    //注入用户权限数据源对象
    @Autowired
    //因为在SpringBoot环境中UserDetailsService在实现Bean不止一个，所以通过id加以区分
    @Qualifier("jdbcUseService")
    private UserDetailsService userDetailsService;


    /**
     * 全局的密码加密类，当前SpringSecurity推荐使用BCryptPasswordEncoder的加密方式
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 相当于XML中配置用户和权限的数据来源
     * 这里的JdbcDaoImpl实现的是UserDetailService接口
     * 表示从数据库获取用户和权限信息
     */
    @Bean("jdbcUseService")
    public JdbcDaoImpl userDetailService() {
        //创建JdbcDaoImpl对象
        JdbcDaoImpl jdbcDao = new JdbcDaoImpl();
        //因为要操作数据库，所以要指定数据源
        //因为导入了jdbc-starter，所以直接使用JdbcTemplate对象即可
        //Druid和JdbcTemplate在Spring容器中自动装配完成
        jdbcDao.setJdbcTemplate(jdbcTemplate);
        //查询用户名和密码的SQL语句
        jdbcDao.setUsersByUsernameQuery("select username,password, status AS enabled from account where username=?");
        //查询用户名和其对应权限名的SQL语句
        jdbcDao.setAuthoritiesByUsernameQuery("SELECT a.username,r.role AS authority FROM account AS a,role AS r,account_role AS ru WHERE  a.id = ru.account_id AND r.id = ru.role_id  AND a.username = ?");
        return jdbcDao;
    }





    /**
     * 将用户权限数据源和密码加密器放入认证管理器中
     * SpringSecurity中进行认证和授权时会用到
     *
     * @param authenticationManagerBuilder
     */
    @Override
    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) {
        try {
            authenticationManagerBuilder
                    // 设置UserDetailsService
                    .userDetailsService(userDetailsService)
                    // 使用BCrypt进行密码的hash
                    .passwordEncoder(encoder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 由于登录操作是创建JWT的操作
     * 这里主要设置登录操作对应的路径执行时不用走SpringSecurity的过滤链条
     * 直接使用自己编写的Controller方法进行登录创建即可
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        //登录请求完全不走SpringSecurity，由自定义Controller完成
        //其它不想走过滤链的请求也可以在这里配置
        web.ignoring().mvcMatchers("/illusts/**",
                "/public/**",
                "/accounts/validate",
                "/accounts/android/validate",
                "/accounts/login",
                "/accounts/register",
                "/accounts/code",
                "/accounts/expires",
                "/accounts/hasPorn",
                "/accounts/forget/change",
                "/systems/logs",
                "/systems/run",
                "/pays/**",
                "/vips/**",
                "/geetests/**",
                "/emails/**"
                );
    }

    /**
     * SpringSecurity中具体的访问策略配置
     */
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        //关闭csrf
        httpSecurity.csrf().disable();
        //要关闭Session的功能，并且设置当前服务端为无状态应用
        httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        //支持跨域
        httpSecurity.cors();

//        httpSecurity.authorizeRequests()
//                .mvcMatchers("/illust/**").permitAll()
//                .mvcMatchers("/public/**").permitAll()
//                .mvcMatchers("/system/**").permitAll()
//                .mvcMatchers("/account/validate").permitAll();


        httpSecurity
                .exceptionHandling()
                //这里覆盖的就是当访问权限不够时写出JSON的操作，以此覆盖默认的转发方式
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    TokenFilter.getCurResponse().setStatus(403);
                    Result result = new Result();
                    result.setCode(403);
                    result.setMsg("权限不足");
                    TokenFilter.getCurResponse().getWriter().println(JSONObject.toJSONString(result));
                });

        // 添加JWT过滤器
        // 用于在每次具体请求时判断当前请求携带的JWT是否为空、格式是否合法、是否过期……
        //以及该JWT中的用户信息和数据库中的是否保持一致
        //TODO:
        // 这里表示在UsernamePasswordAuthenticationFilter过滤器之前添加我们自己的JWT过滤器
        // SpringSecurity中过滤链结构其实非常复杂，同学们可以自行了解
        httpSecurity
                .addFilterBefore(new JwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);

    }
}
