package com.hcyacg.pixiv.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.hcyacg.pixiv.bean.JwtOperation;
import com.hcyacg.pixiv.dto.Result;
import com.hcyacg.pixiv.entity.*;
import com.hcyacg.pixiv.mapper.*;
import com.hcyacg.pixiv.service.PayService;
import com.hcyacg.pixiv.utils.CodePayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.*;

/**
 * Created: 黄智文
 * Desc:
 * Date: 2020/7/13 12:54
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PayServiceImpl implements PayService {
    @Autowired
    private JwtOperation jwtOperation;
    @Autowired
    private CodePayUtils codePayUtils;
    @Autowired
    private VipMapper vipMapper;
    @Autowired
    private VipPackageMapper vipPackageMapper;
    @Autowired
    private AccountVipMapper accountVipMapper;
    @Autowired
    private AccountRoleMapper accountRoleMapper;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Value("${code.pay.key}")
    private String key;

    @Override
    public Result createUrl(String authorization, Integer type, Integer vipId, Integer vipPackAge) {
        try {
            if (StringUtils.isBlank(authorization)) {
                return new Result(403, "你未登录", null, null);
            }

            Account account = JSON.parseObject(String.valueOf(jwtOperation.parseJwt(authorization).get("account")), Account.class);
            if (null == account) {
                return new Result(403, "你未登录", null, null);
            }

            if (vipMapper.selectCount(new QueryWrapper<Vip>().eq("id", vipId)) < 1) {
                return new Result(400, "支付方式不存在", null, null);
            }

            if (vipPackageMapper.selectCount(new QueryWrapper<VipPackage>().eq("id", vipPackAge).eq("vip_id", vipId)) < 1) {
                return new Result(400, "套餐不存在", null, null);
            }

            VipPackage vipPackage = vipPackageMapper.selectOne(new QueryWrapper<VipPackage>().eq("id", vipPackAge).eq("vip_id", vipId));
            BigDecimal money = new BigDecimal(0);
            if (vipPackage.getDiscount() != null && vipPackage.getDiscount()!= 0) {
                money = money.add(vipPackage.getPrice().multiply(BigDecimal.valueOf(Long.parseLong(String.valueOf(vipPackage.getDiscount() / 100)))));
            } else {
                money = money.add(vipPackage.getPrice());
            }

            return new Result(201, "获取成功", codePayUtils.createUrl(type, account.getId(), money, vipPackage.getId()), null);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(500, "获取失败", null, e.getMessage());
        }
    }

    @Override
    public String notifyUrl(HttpServletRequest request) {
        try {
            Map<String, String> params = new HashMap<String, String>(); //申明hashMap变量储存接收到的参数名用于排序
            Map requestParams = request.getParameterMap(); //获取请求的全部参数

            String valueStr = ""; //申明字符变量 保存接收到的变量
            for (Object o : requestParams.keySet()) {

                String name = (String) o;
                String[] values = (String[]) requestParams.get(name);
                valueStr = values[0];
                //乱码解决，这段代码在出现乱码时使用。如果sign不相等也可以使用这段代码转化
                //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
                System.out.println(name + "=>" + valueStr);
                params.put(name, valueStr);//增加到params保存
            }
            List<String> keys = new ArrayList<String>(params.keySet()); //转为数组
            Collections.sort(keys); //重新排序
            StringBuilder prestr = new StringBuilder();
            String sign = params.get("sign"); //获取接收到的sign 参数

            for (int i = 0; i < keys.size(); i++) { //遍历拼接url 拼接成a=1&b=2 进行MD5签名
                String key_name = keys.get(i);
                String value = params.get(key_name);
                if (value == null || value.equals("") || key_name.equals("sign")) { //跳过这些 不签名
                    continue;
                }
                if (prestr.toString().equals("")) {
                    prestr = new StringBuilder(key_name + "=" + value);
                } else {
                    prestr.append("&").append(key_name).append("=").append(value);
                }
            }
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update((prestr + key).getBytes());
            String mySign = new BigInteger(1, md.digest()).toString(16).toLowerCase();
            if (mySign.length() != 32) {
                mySign = "0" + mySign;
            }

            if (mySign.equals(sign)) {
                String param = params.get("param");
                BigDecimal money = BigDecimal.valueOf(Long.parseLong(params.get("money")));
                VipPackage vipPackage = vipPackageMapper.selectById(Integer.parseInt(param));
                BigDecimal dataMoney = new BigDecimal(0);
                if (vipPackage.getDiscount() != null && vipPackage.getDiscount()!= 0){
                    dataMoney = dataMoney.add(vipPackage.getPrice().multiply(BigDecimal.valueOf(Long.parseLong(String.valueOf(vipPackage.getDiscount() / 100)))));
                } else {
                    dataMoney = dataMoney.add(vipPackage.getPrice());
                }

                if (dataMoney.compareTo(money) == 0){
                    //以每月31天计算
                    Integer everyMonth = 31;
                    everyMonth *= vipPackage.getMonth();

                    Calendar now = Calendar.getInstance();

                    if (accountMapper.selectCount(new QueryWrapper<Account>().eq("id",params.get("pay_id"))) < 1){
                        throw new RuntimeException("fail");
                    }

                    if (accountVipMapper.selectCount(new QueryWrapper<AccountVip>().eq("account_id",params.get("pay_id"))) < 1){
                        AccountVip accountVip = new AccountVip();
                        accountVip.setPayNo(String.valueOf(params.get("pay_no")));
                        accountVip.setCreateTime(now.getTime());
                        now.add(Calendar.DATE,everyMonth);
                        accountVip.setEndTime(now.getTime());
                        accountVip.setVipPackAgeId(Integer.parseInt(params.get("param")));




                        accountVip.setAccountId(Integer.parseInt(params.get("pay_id")));

                        if (accountVipMapper.insert(accountVip) < 1){
                            throw new RuntimeException("fail");
                        }
                        Role role = roleMapper.selectOne(new QueryWrapper<Role>().eq("role", "ROLE_VIP"));
                        AccountRole accountRole = new AccountRole();
                        accountRole.setRoleId(role.getId());
                        accountRole.setAccountId(accountVip.getAccountId());
                        if (accountRoleMapper.insert(accountRole) < 1) {
                            throw new RuntimeException("fail");
                        }
                    }else {
                        AccountVip accountVip = accountVipMapper.selectOne(new QueryWrapper<AccountVip>().eq("account_id",params.get("pay_id")));
                        now.add(Calendar.DATE,everyMonth);
                        accountVip.setEndTime(now.getTime());
                        if (accountVipMapper.updateById(accountVip) < 1){
                            throw new RuntimeException("fail");
                        }
                    }


                }
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
            }

            return "fail";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }
}
