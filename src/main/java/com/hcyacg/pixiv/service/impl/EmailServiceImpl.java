package com.hcyacg.pixiv.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.hcyacg.pixiv.bean.JwtOperation;
import com.hcyacg.pixiv.constant.AppConstant;
import com.hcyacg.pixiv.dto.Result;
import com.hcyacg.pixiv.entity.Account;
import com.hcyacg.pixiv.mapper.AccountMapper;
import com.hcyacg.pixiv.service.AccountService;
import com.hcyacg.pixiv.service.EmailService;
import com.hcyacg.pixiv.utils.Codeutils;
import com.hcyacg.pixiv.utils.HttpUtils;
import com.hcyacg.pixiv.utils.RedisUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Nekoer
 * @Date 2020/9/18 23:57
 * @Desc
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class EmailServiceImpl implements EmailService {

    @Autowired
    private HttpUtils httpUtils;
    @Autowired
    private HttpServletResponse res;
    @Autowired
    private HttpServletRequest req;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private JwtOperation jwtOperation;
    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Override
    public Result code(String email) {
        try {
            if (!accountService.canRegister()){
                return httpUtils.setBuild(res, new Result(403, "本站已暂停注册", null, null));
            }

            if (redisUtils.hasKey(AppConstant.CODE_EMAIL + email)) {
                return httpUtils.setBuild(res, new Result(400, "该邮箱已发送验证码，如需再次发送，请等待三分钟", null, null));
            }

            if (StringUtils.isBlank(email)) {
                return httpUtils.setBuild(res, new Result(400, "邮箱不能为空", null, null));
            }

            List<Account> account = accountMapper.selectList(new QueryWrapper<Account>().eq("email", email));
            if (account.size() >= 1) {
                return httpUtils.setBuild(res, new Result(400, "该邮箱已被注册", null, null));
            }
            //获取6位验证码
            String code = Codeutils.getRandomStr(6);
            //存入redis，实施3分钟有效验证
            redisUtils.set(AppConstant.CODE_EMAIL + email, code, 3 * 60L);
            Map<String, Object> map = new HashMap<>();
            map.put("email", email);
            map.put("code", code);
            rabbitTemplate.convertAndSend("code", map);

            return httpUtils.setBuild(res, new Result(200, "发送成功", null, null));
        } catch (Exception e) {
            e.printStackTrace();
            return httpUtils.setBuild(res, new Result(500, "服务器内部错误", null, e.getMessage()));
        }
    }

    @Override
    public Result updateCode(String authorization) {
        try {

            if (StringUtils.isBlank(authorization)) {
                return httpUtils.setBuild(res, new Result(403, "你未登录", null, null));
            }


            Account account = JSON.parseObject(String.valueOf(jwtOperation.parseJwt(authorization).get("account")), Account.class);


            if (null == account) {
                return httpUtils.setBuild(res, new Result(400, "该用户未找到", null, null));
            }

            if (redisUtils.hasKey(AppConstant.CODE_EMAIL_UPDATE + account.getEmail())) {
                return httpUtils.setBuild(res, new Result(400, "该邮箱已发送验证码，如需再次发送，请等待三分钟", null, null));
            }

            //获取6位验证码
            String code = Codeutils.getRandomStr(6);
            //存入redis，实施3分钟有效验证
            redisUtils.set(AppConstant.CODE_EMAIL_UPDATE + account.getEmail(), code, 3 * 60L);
            Map<String, Object> map = new HashMap<>();
            map.put("email", account.getEmail());
            map.put("code", code);
            rabbitTemplate.convertAndSend("code", map);

            return httpUtils.setBuild(res, new Result(200, "发送成功", null, null));
        } catch (Exception e) {
            e.printStackTrace();
            return httpUtils.setBuild(res, new Result(500, "服务器内部错误", null, e.getMessage()));
        }
    }

    @Override
    public Result changeEmailCode(String authorization, String email) {
        try{
            if (StringUtils.isBlank(authorization)) {
                return httpUtils.setBuild(res, new Result(403, "你未登录", null, null));
            }

            Account account = JSON.parseObject(String.valueOf(jwtOperation.parseJwt(authorization).get("account")), Account.class);
            if (null == account) {
                return httpUtils.setBuild(res, new Result(400, "该用户未找到", null, null));
            }

            if (StringUtils.isBlank(email)){
                return httpUtils.setBuild(res, new Result(400, "新邮箱不能为空", null, null));
            }

            if (!AppConstant.EMAIL_PATTERN.matcher(email).matches()){
                return httpUtils.setBuild(res, new Result(400, "该邮箱格式错误", null, null));
            }

            if (redisUtils.hasKey(AppConstant.CODE_EMAIL_CHANGE_EMAIL_CODE + email)) {
                return httpUtils.setBuild(res, new Result(400, "该邮箱已发送验证码，如需再次发送，请等待三分钟", null, null));
            }




            //获取6位验证码
            String code = Codeutils.getRandomStr(6);
            //存入redis，实施3分钟有效验证
            redisUtils.set(AppConstant.CODE_EMAIL_CHANGE_EMAIL_CODE + email, code, 3 * 60L);
            Map<String, Object> map = new HashMap<>();
            map.put("email", email);
            map.put("code", code);
            rabbitTemplate.convertAndSend("code", map);

            return httpUtils.setBuild(res, new Result(200, "发送成功", null, null));

        }catch (Exception e){
            e.printStackTrace();
            return httpUtils.setBuild(res, new Result(500, "服务器内部错误", null, e.getMessage()));
        }
    }

    @Override
    public Result changePassWordCodeForForget(String email) {
        try{
            if (StringUtils.isBlank(email)) {
                return httpUtils.setBuild(res, new Result(400, "邮箱不存在", null, null));
            }

            if (accountMapper.selectCount(new QueryWrapper<Account>().eq("email",email))<1){
                return httpUtils.setBuild(res,new Result(400,"没有该账户",false,null));
            }

            if (!AppConstant.EMAIL_PATTERN.matcher(email).matches()){
                return httpUtils.setBuild(res, new Result(400, "该邮箱格式错误", null, null));
            }

            if (redisUtils.hasKey(AppConstant.CODE_EMAIL_FORGET_CHANGE_PASSWORD_CODE + email)) {
                return httpUtils.setBuild(res, new Result(400, "该邮箱已发送验证码，如需再次发送，请等待三分钟", null, null));
            }

            //获取6位验证码
            String code = Codeutils.getRandomStr(6);
            //存入redis，实施3分钟有效验证
            redisUtils.set(AppConstant.CODE_EMAIL_FORGET_CHANGE_PASSWORD_CODE + email, code, 3 * 60L);
            Map<String, Object> map = new HashMap<>();
            map.put("email", email);
            map.put("code", code);
            rabbitTemplate.convertAndSend("code", map);

            return httpUtils.setBuild(res, new Result(200, "发送成功", null, null));
        }catch (Exception e){
            e.printStackTrace();
            return httpUtils.setBuild(res, new Result(500, "服务器内部错误", null, e.getMessage()));
        }
    }

}
