package com.hcyacg.pixiv.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.hcyacg.pixiv.bean.JwtOperation;
import com.hcyacg.pixiv.constant.AppConstant;
import com.hcyacg.pixiv.dto.Result;
import com.hcyacg.pixiv.entity.*;
import com.hcyacg.pixiv.dto.PixivToken;
import com.hcyacg.pixiv.mapper.*;

import com.hcyacg.pixiv.service.AccountService;
import com.hcyacg.pixiv.utils.*;
import io.jsonwebtoken.Claims;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: Nekoer
 * @Desc: 账户实现类
 * @Date: 2020/5/12 15:12
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AccountServiceImpl implements AccountService {

    @Value(value = "${pixiv.client_id}")
    private String clientId;
    @Value(value = "${pixiv.client_secret}")
    private String clientSecret;
    @Value(value = "${pixiv.hash_secret}")
    private String hashSecret;
    @Value(value = "${pixiv.account.username}")
    private String userName;
    @Value(value = "${pixiv.account.password}")
    private String passWord;
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
    private Base64Utils base64Utils;
    @Autowired
    private EmailUtils emailUtils;
    @Autowired
    private MinioUilts minioUilts;
    @Autowired
    private ValidateCodeUtils validateCodeUtils;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private SystemMapper systemMapper;
    @Autowired
    private AccountRoleMapper accountRoleMapper;
    @Autowired
    private TokenMapper tokenMapper;
    @Autowired
    private AccountVipMapper accountVipMapper;
    @Autowired
    private JwtOperation jwtOperation;
    @Value("${minio.domin}")
    private String domin;

    /**
     * 获取pixiv的账号数据
     */
    @Override
    public Result getToken() {
        Object token1 = redisUtils.get("token");
        java.lang.System.out.println(token1);
        if (!StringUtils.isBlank(String.valueOf(token1)) && token1 != null) {
            return new Result(201, "缓存已存在", token1, null);
        }
        HttpPost httpPost = null;
        ByteArrayOutputStream infoStream = new ByteArrayOutputStream();
        try {


            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+00:00");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest((sdf.format(date) + hashSecret).getBytes());
            StringBuilder hash = new StringBuilder();
            for (int r3 = 0; r3 < digest.length; r3++) {
                hash.append(String.format("%02x", digest[r3]));
            }

            CloseableHttpClient httpClient = HttpClients.createDefault();
            List<NameValuePair> parameters = new ArrayList<NameValuePair>(0);
            parameters.add(new BasicNameValuePair("get_secure_url", "1"));
            parameters.add(new BasicNameValuePair("client_id", clientId));
            parameters.add(new BasicNameValuePair("client_secret", clientSecret));
            parameters.add(new BasicNameValuePair("grant_type", "password"));
            parameters.add(new BasicNameValuePair("username", userName));
            parameters.add(new BasicNameValuePair("password", passWord));
            httpPost = httpUtils.post(AppConstant.OAUTH_URL + "/auth/token", parameters);

            httpPost.setProtocolVersion(HttpVersion.HTTP_1_0);
            httpPost.addHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_CLOSE);
            httpPost.addHeader("User-Agent", "PixivAndroidApp/5.0.64 (Android 6.0)");
            httpPost.addHeader("X-Client-Time", sdf.format(date));
            httpPost.addHeader("X-Client-Hash", String.valueOf(hash));
            httpPost.addHeader("Connection", "Close");

            CloseableHttpResponse response = null;
            //发送请求
            response = httpClient.execute(httpPost);
            InputStream in = response.getEntity().getContent();
            byte[] buffer = new byte[1];
            int len = 0;
            String data = "";
            while ((len = in.read(buffer)) > 0) {
                infoStream.write(buffer, 0, len);
            }
            JSONObject parse = JSONObject.parseObject(infoStream.toString("UTF-8"));
            JSONObject obj = JSONObject.parseObject(String.valueOf(parse.get("response")));


            PixivToken token = new PixivToken();
            token.setAccessToken(String.valueOf(obj.get("access_token")));
            token.setDeviceToken(String.valueOf(obj.get("device_token")));
            token.setExpiresIn(String.valueOf(obj.get("expires_in")));
            token.setRefreshToken(String.valueOf(obj.get("refresh_token")));
            token.setTokenType(String.valueOf(obj.get("token_type")));

            redisUtils.set("token", token, Long.parseLong(String.valueOf(obj.get("expires_in"))));
//            redisUtils.expire("token",Long.parseLong(String.valueOf(obj.get("expires_in"))));

//            if (tokenMapper.selectById(1) == null){
//                if (tokenMapper.insert(token) < 1) {
//                    throw new RuntimeException("插入数据库失败");
//                }
//            }else {
//                token.setId(1);
//                tokenMapper.updateById(token);
//            }

            infoStream.close();
            return new Result(201, "请求成功", JSONObject.parse(infoStream.toString("UTF-8")), "");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(500, "请求失败", "", e.getMessage());
        } finally {
            assert httpPost != null;
            httpPost.abort();

        }

    }

    @Override
    public Result register(String userName, String passWord, String confirm, String email, String code) {
        try {

            if (!canRegister()){
                return httpUtils.setBuild(res, new Result(403, "本站已暂停注册", null, null));
            }


            if (StringUtils.isBlank(userName)) {
                return httpUtils.setBuild(res, new Result(400, "用户名不能为空", null, null));
            }

            List<Account> list = accountMapper.selectList(new QueryWrapper<Account>().eq("username", userName));
            if (list.size() >= 1) {
                return httpUtils.setBuild(res, new Result(400, "该用户名已被注册", null, null));
            }

            if (StringUtils.isBlank(passWord)) {
                return httpUtils.setBuild(res, new Result(400, "密码不能为空", null, null));
            }

            if (StringUtils.isBlank(confirm)) {
                return httpUtils.setBuild(res, new Result(400, "确认密码不能为空", null, null));
            }

            if (StringUtils.isBlank(email)) {
                return httpUtils.setBuild(res, new Result(400, "邮箱不能为空", null, null));
            }

            list = accountMapper.selectList(new QueryWrapper<Account>().eq("email", email));
            if (list.size() >= 1) {
                return httpUtils.setBuild(res, new Result(400, "该邮箱已被注册", null, null));
            }

            if (StringUtils.isBlank(code)) {
                return httpUtils.setBuild(res, new Result(400, "验证码不能为空", null, null));
            }

            if (!passWord.equals(confirm)) {
                return httpUtils.setBuild(res, new Result(400, "两次密码不相同", null, null));
            }

            if (!redisUtils.hasKey(AppConstant.CODE_EMAIL + email)) {
                return httpUtils.setBuild(res, new Result(400, "该验证码已失效", null, null));
            }

            String vCode = String.valueOf(redisUtils.get(AppConstant.CODE_EMAIL + email));
            if (!vCode.equals(code)) {
                return httpUtils.setBuild(res, new Result(400, "验证码错误", null, null));
            }

            Account account = new Account();
            account.setUserName(userName);
            account.setPassWord(DigestUtils.md5Hex(passWord));
            account.setEmail(email);
            account.setGender(AppConstant.ACCOUNT_SECRECY);
            account.setHasPron(AppConstant.ACCOUNT_NOT_HAS_PRON);
            account.setStatus(AppConstant.ACCOUNT_BAN_NO);
            if (accountMapper.insert(account) <= 0) {
                throw new RuntimeException("注册失败");
            }

            for (int i = 0; i < AppConstant.ACCOUNT_ACCESS_LEVEL.size(); i++) {
                AccountRole accountRole = new AccountRole();
                accountRole.setAccountId(account.getId());
                accountRole.setRoleId(AppConstant.ACCOUNT_ACCESS_LEVEL.get(i));
                if (accountRoleMapper.insert(accountRole) < 1) {
                    throw new RuntimeException("注册失败");
                }
            }

            return httpUtils.setBuild(res, new Result(200, "注册成功", null, null));
        } catch (Exception e) {
            e.printStackTrace();
            return httpUtils.setBuild(res, new Result(500, "服务器内部错误", null, e.getMessage()));
        }
    }

    @Override
    public Result login(String userName, String passWord, String code) {
        try {
            if (StringUtils.isBlank(userName)) {
                return httpUtils.setBuild(res, new Result(400, "用户名不能为空", null, null));
            }

            List<Account> list = accountMapper.selectList(new QueryWrapper<Account>().eq("username", userName));
            List<Account> listEmail = accountMapper.selectList(new QueryWrapper<Account>().eq("email",userName));

            if (list.size() == 0 && listEmail.size() == 0) {
                return httpUtils.setBuild(res, new Result(400, "该账号不存在", null, null));
            }

            if (list.size() == 0 && listEmail.size() != 0){
                list = listEmail;
            }

            if (StringUtils.isBlank(passWord)) {
                return httpUtils.setBuild(res, new Result(400, "密码不能为空", null, null));
            }

            if (!list.get(0).getPassWord().equals(DigestUtils.md5Hex(passWord))) {
                return httpUtils.setBuild(res, new Result(400, "密码错误", null, null));
            }


            String vCode = String.valueOf(req.getSession().getAttribute(AppConstant.CODE_EMAIL_VALIDATE));
            java.lang.System.out.println(vCode);
            if (!code.equals(vCode)) {
                return httpUtils.setBuild(res, new Result(400, "验证码错误", null, null));
            }
            return httpUtils.setBuild(res, new Result(201, "登录成功", jwtOperation.createJwt(list.get(0)), null));
        } catch (Exception e) {
            e.printStackTrace();
            return httpUtils.setBuild(res, new Result(500, "服务器内部错误", null, e.getMessage()));
        }
    }

    @Override
    public Result code(String email) {
        try {
            if (!canRegister()){
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
    public void ValidateCode() {
        try {
            Map<String, Object> map = validateCodeUtils.drawImage();
            java.lang.System.out.println(map.get("code"));

            req.getSession().setAttribute(AppConstant.CODE_EMAIL_VALIDATE, map.get("code"));
            res.setContentType("image/jpeg");

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            BufferedImage image = (BufferedImage) map.get("image");
            ImageIO.write(image, "jpg", stream);

            res.getOutputStream().write(stream.toByteArray());
            res.getOutputStream().flush();

            //redisUtils.set(AppConstant.CODE_EMAIL_VALIDATE + userName,map.get("code"),60L);
            //return httpUtils.setBuild(res, new Result(201, "获取成功", map.get("image"), null));
        } catch (Exception e) {
            e.printStackTrace();
            res.setCharacterEncoding("UTF-8");
            res.setContentType("application/json; charset=utf-8");
            try {
                res.getWriter().write(JSONObject.toJSONString(new Result(500, "服务器内部错误", "", "错误" + e.getMessage())));
            } catch (Exception a) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Result updatePassWord(String authorization, String originalPassWord, String passWord, String confirm, String vCode) {
        try {

            if (StringUtils.isBlank(vCode)) {
                return httpUtils.setBuild(res, new Result(400, "验证码不能为空", null, null));
            }

            if (StringUtils.isBlank(authorization)) {
                return httpUtils.setBuild(res, new Result(403, "您未登录", null, null));
            }

            Account account = JSON.parseObject(String.valueOf(jwtOperation.parseJwt(authorization).get("account")), Account.class);
            if (null == account) {
                return httpUtils.setBuild(res, new Result(400, "该用户未找到", null, null));
            }

            if (!vCode.equals(String.valueOf(redisUtils.get(AppConstant.CODE_EMAIL_UPDATE + account.getEmail())))) {
                return httpUtils.setBuild(res, new Result(400, "验证码错误", null, null));
            }


            if (StringUtils.isBlank(originalPassWord)) {
                return httpUtils.setBuild(res, new Result(400, "原密码不能为空", null, null));
            }

            if (!DigestUtils.md5Hex(originalPassWord).equals(account.getPassWord())) {
                return httpUtils.setBuild(res, new Result(400, "原密码不匹配", null, null));
            }

            if (StringUtils.isBlank(passWord)) {
                return httpUtils.setBuild(res, new Result(400, "密码不能为空", null, null));
            }
            if (StringUtils.isBlank(confirm)) {
                return httpUtils.setBuild(res, new Result(400, "确认密码不能为空", null, null));
            }

            if (!passWord.equals(confirm)) {
                return httpUtils.setBuild(res, new Result(400, "两次密码不相同", null, null));
            }


            account.setPassWord(DigestUtils.md5Hex(passWord));

            if (accountMapper.updateById(account) < 1) {
                throw new RuntimeException("修改失败");
            }

            return httpUtils.setBuild(res, new Result(201, "修改成功", null, null));
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

           if (redisUtils.hasKey(AppConstant.CODE_EMAIL_CHANGE_EMAIL_CODE + email)) {
               return httpUtils.setBuild(res, new Result(400, "该邮箱已发送验证码，如需再次发送，请等待三分钟", null, null));
           }


           if (!AppConstant.EMAIL_PATTERN.matcher(email).matches()){
               return httpUtils.setBuild(res, new Result(400, "该邮箱格式错误", null, null));
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
    public Result changeEmail(String authorization, String email, String code) {
        try{
            if (StringUtils.isBlank(authorization)) {
                return httpUtils.setBuild(res, new Result(403, "你未登录", null, null));
            }

            if (StringUtils.isBlank(email)){
                return httpUtils.setBuild(res, new Result(400, "新邮箱不能为空", null, null));
            }

            if (StringUtils.isBlank(code)){
                return httpUtils.setBuild(res, new Result(400, "验证码不能为空", null, null));
            }

            Account account = JSON.parseObject(String.valueOf(jwtOperation.parseJwt(authorization).get("account")), Account.class);
            if (null == account) {
                return httpUtils.setBuild(res, new Result(400, "该用户未找到", null, null));
            }

            if (!AppConstant.EMAIL_PATTERN.matcher(email).matches()){
                return httpUtils.setBuild(res, new Result(400, "该邮箱格式错误", null, null));
            }

            if (!redisUtils.hasKey(AppConstant.CODE_EMAIL_CHANGE_EMAIL_CODE + email)){
                return httpUtils.setBuild(res, new Result(400, "验证码错误", null, null));
            }

            if (!code.equals(redisUtils.get(AppConstant.CODE_EMAIL_CHANGE_EMAIL_CODE + email))){
                return httpUtils.setBuild(res, new Result(400, "验证码错误", null, null));
            }
            account.setEmail(email);
            if (accountMapper.updateById(account) < 1){
                throw new RuntimeException("更新失败");
            }

            return httpUtils.setBuild(res, new Result(201, "更新成功", jwtOperation.createJwt(account), null));
        }catch (Exception e){
            e.printStackTrace();
            return httpUtils.setBuild(res, new Result(500, "服务器内部错误", null, e.getMessage()));
        }
    }

    @Override
    public Result getAccountInfo(String authorization) {
        try {

            if (StringUtils.isBlank(authorization)) {
                return httpUtils.setBuild(res, new Result(403, "你未登录", null, null));
            }

            Claims claims = jwtOperation.parseJwt(authorization);
            Account account = JSON.parseObject(String.valueOf(claims.get("account")), Account.class);


            if (null == account) {
                return httpUtils.setBuild(res, new Result(400, "该用户未找到", null, null));
            }

            Account accountInfo = accountMapper.selectById(account.getId());
            if (null == accountInfo) {
                return httpUtils.setBuild(res, new Result(400, "该用户未找到", null, null));
            }


            //设置密码、手机号为空,保护隐私
            accountInfo.setPassWord(null);
            //accountInfo.setPhone(null);
            Map<String, Object> map = new HashMap<>();
            map.put("account", accountInfo);

//            if (!StringUtils.isBlank(accountInfo.getAvatar())){
//                map.put("avatar", base64Utils.encodeImageToBase64(accountInfo.getAvatar()));
//            }else {
//                map.put("avatar", null);
//            }

            return httpUtils.setBuild(res, new Result(201, "获取成功", map, null));
        } catch (Exception e) {
            e.printStackTrace();
            return httpUtils.setBuild(res, new Result(500, "服务器内部错误", null, e.getMessage()));
        }
    }

    @Override
    public Result expires(String authorization) {
        return httpUtils.setBuild(res,new Result(201, "获取成功", jwtOperation.parseOverdue(authorization), null));
    }

    @Override
    public Result updateAvatar(String authorization, String file) {

        try {

            if (StringUtils.isBlank(authorization)) {
                return httpUtils.setBuild(res, new Result(403, "你未登录", null, null));
            }

            if (null == file) {
                return httpUtils.setBuild(res, new Result(400, "请上传图片", null, null));
            }

            Claims claims = jwtOperation.parseJwt(authorization);
            Account account = JSON.parseObject(String.valueOf(claims.get("account")), Account.class);


            if (null == account) {
                return httpUtils.setBuild(res, new Result(400, "该用户未找到", null, null));
            }

            Account accountInfo = accountMapper.selectById(account.getId());
            if (null == accountInfo) {
                return httpUtils.setBuild(res, new Result(400, "该用户未找到", null, null));
            }

            Map<String, Object> map = new HashMap<>();
            map.put("account", accountInfo);
            map.put("file", file);
            rabbitTemplate.convertAndSend("avatar", map);

            return httpUtils.setBuild(res, new Result(201, "获取成功", "上传成功,更新需等待片刻", null));
        } catch (Exception e) {
            e.printStackTrace();
            return httpUtils.setBuild(res, new Result(500, "服务器内部错误", null, e.getMessage()));
        }
    }

    @Override
    public Result updateAccountInfo(String authorization, String nickName, String iphone, String sex, String birthday) {
        try {

            if (StringUtils.isBlank(authorization)) {
                return httpUtils.setBuild(res, new Result(403, "你未登录", null, null));
            }

            Account account = JSON.parseObject(String.valueOf(jwtOperation.parseJwt(authorization).get("account")), Account.class);
            if (null == account) {
                return httpUtils.setBuild(res, new Result(403, "你未登录", null, null));
            }

            if (StringUtils.isNotBlank(iphone) && !AppConstant.CHINA_IPHONE.matcher(iphone).matches()) {
                return httpUtils.setBuild(res, new Result(400, "手机号格式不正确", null, null));
            }

            if (StringUtils.isNotBlank(birthday)) {
                try {
                    AppConstant.SDF.parse(birthday);
                } catch (Exception ex) {
                    return httpUtils.setBuild(res, new Result(400, "日期格式不正确", null, null));
                }
            }

            if (AppConstant.ISNUMBER.matches(sex) && Arrays.asList(AppConstant.ACCOUNT_FEMALE, AppConstant.ACCOUNT_MALE, AppConstant.ACCOUNT_SECRECY).indexOf(Integer.parseInt(sex)) < 0) {
                return httpUtils.setBuild(res, new Result(400, "不存在该性别", null, null));
            }

            account.setPhone(iphone);
            account.setBirthday(AppConstant.SDF.parse(birthday));
            account.setNickName(nickName);
            account.setGender(Integer.parseInt(sex));
            if (accountMapper.updateById(account) < 1) {
                throw new RuntimeException("更新失败");
            }

            return new Result(201, "更新成功", account, null);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(500, "更新失败", null, e.getMessage());
        }
    }

    @Override
    public Result hasPorn(String authorization) {
        try {

            if (StringUtils.isBlank(authorization)) {
                return httpUtils.setBuild(res, new Result(403, "你未登录", null, null));
            }

            Account account = JSON.parseObject(String.valueOf(jwtOperation.parseJwt(authorization).get("account")), Account.class);
            if (null == account) {
                return httpUtils.setBuild(res, new Result(403, "你未登录", null, null));
            }
            account = accountMapper.selectById(account.getId());

            return httpUtils.setBuild(res, new Result(201, "获取成功", AppConstant.ACCOUNT_HAS_PRON.equals(account.getHasPron()), null));
        } catch (Exception e) {
            e.printStackTrace();
            return httpUtils.setBuild(res, new Result(500, "获取失败", null, e.getMessage()));
        }
    }

    @Override
    public Result hasApiKey() {
        return httpUtils.setBuild(res, new Result(201, "获取成功", true, null));
    }

    @Override
    public Result getApiKey(String authorization) {
        try {
            if (StringUtils.isBlank(authorization)) {
                return httpUtils.setBuild(res, new Result(403, "你未登录", null, null));
            }

            Claims claims = jwtOperation.parseJwt(authorization);
            Account account = JSON.parseObject(String.valueOf(claims.get("account")), Account.class);


            if (null == account) {
                return httpUtils.setBuild(res, new Result(400, "该用户未找到", null, null));
            }

            Account accountInfo = accountMapper.selectById(account.getId());
            if (null == accountInfo) {
                return httpUtils.setBuild(res, new Result(400, "该用户未找到", null, null));
            }

            return httpUtils.setBuild(res, new Result(201, "获取成功", tokenMapper.selectOne(new QueryWrapper<Token>().eq("account_id", accountInfo.getId())), null));
        } catch (Exception e) {
            e.printStackTrace();
            return httpUtils.setBuild(res, new Result(500, "获取失败", null, e.getMessage()));
        }
    }

    @Override
    public Result applyForApiKey(String authorization) {
        try {
            if (StringUtils.isBlank(authorization)) {
                return httpUtils.setBuild(res, new Result(403, "你未登录", null, null));
            }

            Claims claims = jwtOperation.parseJwt(authorization);
            Account account = JSON.parseObject(String.valueOf(claims.get("account")), Account.class);


            if (null == account) {
                return httpUtils.setBuild(res, new Result(400, "该用户未找到", null, null));
            }

            Account accountInfo = accountMapper.selectById(account.getId());
            if (null == accountInfo) {
                return httpUtils.setBuild(res, new Result(400, "该用户未找到", null, null));
            }

            if (tokenMapper.selectCount(new QueryWrapper<Token>().eq("account_id", accountInfo.getId())) > 0) {
                return httpUtils.setBuild(res, new Result(400, "申请失败,您已有APIKEY", null, null));
            }

            String token = jwtOperation.createOtherToken(accountInfo, UUID.randomUUID().toString().replaceAll("-", ""));
            Token otherToken = new Token();
            otherToken.setAccountId(accountInfo.getId());
            otherToken.setToken(token);


            if (systemMapper.selectCount(new QueryWrapper<Systems>().eq("name", AppConstant.SYSTEM_TOKEN_DEFAULT_NUMBER)) > 0){
                Systems system = systemMapper.selectOne(new QueryWrapper<Systems>().eq("name", AppConstant.SYSTEM_TOKEN_DEFAULT_NUMBER));
                otherToken.setLine(Integer.parseInt(system.getParameter()));
            }else {
                otherToken.setLine(0);
            }

            if (tokenMapper.insert(otherToken) < 1) {
                throw new RuntimeException("申请失败");
            }

            return httpUtils.setBuild(res, new Result(201, "申请成功", otherToken, null));
        } catch (Exception e) {
            return httpUtils.setBuild(res, new Result(500, "申请失败", null, e.getMessage()));
        }
    }

    @Override
    public Result resetApiKey(String authorization) {
        try {
            if (StringUtils.isBlank(authorization)) {
                return httpUtils.setBuild(res, new Result(403, "你未登录", null, null));
            }

            Claims claims = jwtOperation.parseJwt(authorization);
            Account account = JSON.parseObject(String.valueOf(claims.get("account")), Account.class);


            if (null == account) {
                return httpUtils.setBuild(res, new Result(400, "该用户未找到", null, null));
            }

            Account accountInfo = accountMapper.selectById(account.getId());
            if (null == accountInfo) {
                return httpUtils.setBuild(res, new Result(400, "该用户未找到", null, null));
            }

            if (tokenMapper.selectCount(new QueryWrapper<Token>().eq("account_id", accountInfo.getId())) < 1) {
                return httpUtils.setBuild(res, new Result(400, "重置失败,您还未申请APIKEY", null, null));
            }

            String tokenStr = jwtOperation.createOtherToken(accountInfo, UUID.randomUUID().toString().replaceAll("-", ""));

            Token token = tokenMapper.selectOne(new QueryWrapper<Token>().eq("account_id", accountInfo.getId()));

            if (systemMapper.selectCount(new QueryWrapper<Systems>().eq("name", AppConstant.SYSTEM_TOKEN_DEFAULT_NUMBER)) > 0){
                Systems system = systemMapper.selectOne(new QueryWrapper<Systems>().eq("name", AppConstant.SYSTEM_TOKEN_DEFAULT_NUMBER));
                token.setLine(Integer.parseInt(system.getParameter()));
            }
            token.setToken(tokenStr);


            if (tokenMapper.updateById(token) < 1) {
                throw new RuntimeException("重置失败");
            }

            return httpUtils.setBuild(res, new Result(201, "重置成功", token, null));
        } catch (Exception e) {
            return httpUtils.setBuild(res, new Result(500, "重置失败", null, e.getMessage()));
        }
    }

    @Override
    public Boolean canRegister() {
        try{
            if (systemMapper.selectCount(new QueryWrapper<Systems>().eq("name",AppConstant.SYSTEM_ALLOWED_TO_REGISTER)) > 0){
                Systems system = systemMapper.selectOne(new QueryWrapper<Systems>().eq("name", AppConstant.SYSTEM_ALLOWED_TO_REGISTER));
                if (system.getParameter().equals("false")){
                    return  false;
                }
            }
            return true;
        }catch (Exception e){
            return true;
        }
    }

    @Override
    public Result isVip(String authorization) {
        try{
            if (StringUtils.isBlank(authorization)) {
                return httpUtils.setBuild(res, new Result(403, "你未登录", null, null));
            }

            Claims claims = jwtOperation.parseJwt(authorization);
            Account account = JSON.parseObject(String.valueOf(claims.get("account")), Account.class);


            if (null == account) {
                return httpUtils.setBuild(res, new Result(400, "该用户未找到", null, null));
            }

            Account accountInfo = accountMapper.selectById(account.getId());
            if (null == accountInfo) {
                return  httpUtils.setBuild(res, new Result(400, "该用户未找到", null, null));
            }
            Role role = roleMapper.selectOne(new QueryWrapper<Role>().eq("role", "ROLE_VIP"));

            if (accountRoleMapper.selectCount(new QueryWrapper<AccountRole>().eq("account_id", accountInfo.getId()).eq("role_id", role.getId())) < 1){
                return  httpUtils.setBuild(res, new Result(400, "您还未成为会员", null, null));
            }

            Boolean isOverdue = checkVipTime(accountInfo.getId());
            if (null == isOverdue){
                return  httpUtils.setBuild(res, new Result(500, "获取失败", null, null));
            }else if (!isOverdue){
                return  httpUtils.setBuild(res, new Result(400, "获取成功", "您还未成为会员", null));
            }else {
                AccountVip accountVip = accountVipMapper.selectOne(new QueryWrapper<AccountVip>().eq("account_id",accountInfo.getId()));
                return  httpUtils.setBuild(res, new Result(201, "获取成功", accountVip, null));
            }

        }catch (Exception e){
            return httpUtils.setBuild(res, new Result(500, "获取失败", null, e.getMessage()));
        }
    }

    @Override
    public Boolean checkVipTime(Integer userId) {
        try{
            //判断是否为空
            if (StringUtils.isBlank(String.valueOf(userId))){
                return null;
            }

            //判断数据库是否有该用户
            if (accountMapper.selectCount(new QueryWrapper<Account>().eq("id",userId)) < 0){
                return null;
            }

            AccountVip accountVip = accountVipMapper.selectOne(new QueryWrapper<AccountVip>().eq("account_id", userId));
            if (accountVip.getEndTime().getTime() < Calendar.getInstance().getTime().getTime()){
                if (accountVipMapper.deleteById(accountVip.getId()) < 0){
                    throw new RuntimeException("删除失败");
                }
                return false;
            }else {
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
