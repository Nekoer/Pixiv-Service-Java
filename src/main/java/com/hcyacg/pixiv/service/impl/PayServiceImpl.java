package com.hcyacg.pixiv.service.impl;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.hcyacg.pixiv.bean.JwtOperation;
import com.hcyacg.pixiv.config.AliPayConfig;
import com.hcyacg.pixiv.dto.Result;
import com.hcyacg.pixiv.entity.*;
import com.hcyacg.pixiv.mapper.*;
import com.hcyacg.pixiv.service.PayService;
import com.hcyacg.pixiv.utils.AliPayUtils;
import com.hcyacg.pixiv.utils.CodePayUtils;
import com.hcyacg.pixiv.utils.RandomUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @Author: Nekoer
 * @Desc:
 * @Date: 2020/7/13 12:54
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
    private OrderMapper orderMapper;
    @Autowired
    private VipPackageMapper vipPackageMapper;
    @Autowired
    private AccountVipMapper accountVipMapper;
    @Autowired
    private AccountRoleMapper accountRoleMapper;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private AliPayUtils aliPayUtils;
    @Autowired
    private RoleMapper roleMapper;
    @Value("${code.pay.key}")
    private String key;
    @Autowired
    private HttpServletResponse res;
    @Autowired
    private RandomUtils randomUtils;
    @Autowired
    private AliPayConfig aliPayConfig;

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
            if (vipPackage.getDiscount() != null && vipPackage.getDiscount() != 0) {
                money = money.add(vipPackage.getPrice().multiply(BigDecimal.valueOf(Long.parseLong(String.valueOf(vipPackage.getDiscount() / 100)))));
            } else {
                money = money.add(vipPackage.getPrice());
            }
            DecimalFormat df = new DecimalFormat("#0.00");

            return new Result(201, "获取成功", codePayUtils.createUrl(type, account.getId(), BigDecimal.valueOf(Double.parseDouble(df.format(money))), vipPackage.getId()), null);
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
                if (vipPackage.getDiscount() != null && vipPackage.getDiscount() != 0) {
                    dataMoney = dataMoney.add(vipPackage.getPrice().multiply(BigDecimal.valueOf(Long.parseLong(String.valueOf(vipPackage.getDiscount() / 100)))));
                } else {
                    dataMoney = dataMoney.add(vipPackage.getPrice());
                }

                if (dataMoney.compareTo(money) == 0) {
                    //以每月31天计算
                    Integer everyMonth = 31;
                    everyMonth *= vipPackage.getMonth();

                    Calendar now = Calendar.getInstance();

                    if (accountMapper.selectCount(new QueryWrapper<Account>().eq("id", params.get("pay_id"))) < 1) {
                        throw new RuntimeException("fail");
                    }

                    if (accountVipMapper.selectCount(new QueryWrapper<AccountVip>().eq("account_id", params.get("pay_id"))) < 1) {
                        AccountVip accountVip = new AccountVip();
                        accountVip.setPayNo(String.valueOf(params.get("pay_no")));
                        accountVip.setCreateTime(now.getTime());
                        now.add(Calendar.DATE, everyMonth);
                        accountVip.setEndTime(now.getTime());
                        accountVip.setVipPackAgeId(Integer.parseInt(params.get("param")));


                        accountVip.setAccountId(Integer.parseInt(params.get("pay_id")));

                        if (accountVipMapper.insert(accountVip) < 1) {
                            throw new RuntimeException("fail");
                        }
                        Role role = roleMapper.selectOne(new QueryWrapper<Role>().eq("role", "ROLE_VIP"));
                        if(accountRoleMapper.selectCount(new QueryWrapper<AccountRole>().eq("account_id",params.get("pay_id")).eq("role_id",role.getId())) < 1){
                            AccountRole accountRole = new AccountRole();
                            accountRole.setRoleId(role.getId());
                            accountRole.setAccountId(accountVip.getAccountId());
                            if (accountRoleMapper.insert(accountRole) < 1) {
                                throw new RuntimeException("fail");
                            }
                        }

                    } else {
                        AccountVip accountVip = accountVipMapper.selectOne(new QueryWrapper<AccountVip>().eq("account_id", params.get("pay_id")));
                        now.add(Calendar.DATE, accountVip.getEndTime().getDay());
                        now.add(Calendar.DATE, everyMonth);
                        accountVip.setEndTime(now.getTime());
                        if (accountVipMapper.updateById(accountVip) < 1) {
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

    @Override
    public void aliPay(String authorization,int vip,Boolean json) {

        try {
            if (StringUtils.isBlank(authorization)) {
                res.setStatus(400);
                res.setCharacterEncoding("UTF-8");
                res.setContentType("application/json; charset=utf-8");
                res.getWriter().println(JSON.toJSONString(new Result(403, "您未登录", null, null)));
                return;
            }
            Account account = JSON.parseObject(String.valueOf(jwtOperation.parseJwt(authorization).get("account")), Account.class);

            VipPackage vipPackages = vipPackageMapper.selectById(vip);
            if (null == vipPackages) {
                res.setStatus(400);
                res.setCharacterEncoding("UTF-8");
                res.setContentType("application/json; charset=utf-8");
                res.getWriter().println(JSON.toJSONString(new Result(400, "会员套餐不存在", null, null)));
                return;
            }


            res.setContentType("image/jpeg;charset=utf-8");
            //获得初始化的AlipayClient

            AlipayClient alipayClient = new DefaultAlipayClient(aliPayConfig.gatewayUrl, aliPayConfig.app_id, aliPayConfig.merchant_private_key, "json", aliPayConfig.charset, aliPayConfig.alipay_public_key, aliPayConfig.sign_type);
            //设置请求参数
            AlipayTradePrecreateRequest aliPayRequest = new AlipayTradePrecreateRequest();
            aliPayRequest.setReturnUrl(aliPayConfig.return_url);
            aliPayRequest.setNotifyUrl(aliPayConfig.notify_url);

            DecimalFormat df = new DecimalFormat("#0.00");
            if(null == vipPackages.getDiscount()|| vipPackages.getDiscount()  <= 0){
                vipPackages.setDiscount(1);
            }

            BigDecimal price = vipPackages.getPrice().multiply(BigDecimal.valueOf(Long.parseLong(String.valueOf(vipPackages.getDiscount()))));

            //订单名称，必填
            String subject = "UUID-" +account.getId() + "-" + vipPackages.getTitle();
            String outTrade = randomUtils.generateOrderId() +"-"+ randomUtils.getRandomStr();

            Order order = new Order();
            order.setOutTrade(outTrade);
            order.setStatus(0);
            order.setAmount(BigDecimal.valueOf(Double.parseDouble(df.format(price))));
            order.setVip(vip);
            order.setAccount(account.getId());
            if(orderMapper.insert(order) < 1){
                throw new RuntimeException("生成订单失败");
            }

            aliPayRequest.setBizContent("{\"out_trade_no\":\"" + outTrade + "\",\"total_amount\":\"" + df.format(price) + "\",\"subject\":\"" + subject + "\",\"timeout_express\":\"90m\"}"); //订单允许的最晚付款时间
            //请求
//            String result = alipayClient.pageExecute(aliPayRequest).getBody();
            AlipayTradePrecreateResponse alipayTradePrecreateResponse = alipayClient.execute(aliPayRequest);

            if(null == json){
                json = false;
            }
            if (json){
                Map<String,Object> map = new LinkedHashMap<>();
                map.put("pay",alipayTradePrecreateResponse.getQrCode());
                map.put("trade_no",outTrade);
                res.setCharacterEncoding("UTF-8");
                res.setContentType("application/json; charset=utf-8");
                res.getOutputStream().write(JSON.toJSONString(new Result(201,"获取成功",map,null)).getBytes(StandardCharsets.UTF_8));
            }else {
                res.getOutputStream().write(aliPayUtils.createQrCode(alipayTradePrecreateResponse.getQrCode()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                res.setStatus(400);
                res.setCharacterEncoding("UTF-8");
                res.setContentType("application/json; charset=utf-8");
                res.getWriter().println(JSON.toJSONString(new Result(500, "服务器错误", null, e.getMessage())));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    @Override
    public String notify(HttpServletRequest request, HttpServletResponse response) {
        // 一定要验签，防止黑客篡改参数
        Map<String, String[]> parameterMap = request.getParameterMap();
        try {
            StringBuilder notifyBuild = new StringBuilder("/****************************** alipay notify ******************************/\n");
            parameterMap.forEach((key, value) -> notifyBuild.append(key + "=" + value[0] + "\n"));
            System.out.println(notifyBuild.toString());

            boolean flag = this.rsaCheckV1(request);
            if (flag) {
                /**
                 * TODO 需要严格按照如下描述校验通知数据的正确性
                 *
                 * 商户需要验证该通知数据中的out_trade_no是否为商户系统中创建的订单号，
                 * 并判断total_amount是否确实为该订单的实际金额（即商户订单创建时的金额），
                 * 同时需要校验通知中的seller_id（或者seller_email) 是否为out_trade_no这笔单据的对应的操作方（有的时候，一个商户可能有多个seller_id/seller_email），
                 *
                 * 上述有任何一个验证不通过，则表明本次通知是异常通知，务必忽略。
                 * 在上述验证通过后商户必须根据支付宝不同类型的业务通知，正确的进行不同的业务处理，并且过滤重复的通知结果数据。
                 * 在支付宝的业务通知中，只有交易通知状态为TRADE_SUCCESS或TRADE_FINISHED时，支付宝才会认定为买家付款成功。
                 */
                //交易状态
                String tradeStatus = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");
                // 商户订单号
                String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
                //支付宝交易号
                String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
                //付款金额
                String total_amount = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"), "UTF-8");
                String seller_id = new String(request.getParameter("seller_id").getBytes("ISO-8859-1"), "UTF-8");
                //2088612816566379
                Order order = orderMapper.selectOne(new QueryWrapper<Order>().eq("out_trade", out_trade_no));
                if (!order.getOutTrade().equals(out_trade_no)){
                    return "error";
                }

                if (order.getAmount().compareTo(BigDecimal.valueOf(Double.parseDouble(total_amount))) != 0){
                    return "error";
                }

                if(!seller_id.equals("2088612816566379")){
                    return "error";
                }
                // TRADE_FINISHED(表示交易已经成功结束，并不能再对该交易做后续操作);
                // TRADE_SUCCESS(表示交易已经成功结束，可以对该交易做后续操作，如：分润、退款等);
                if (tradeStatus.equals("TRADE_FINISHED")) {
                    //判断该笔订单是否在商户网站中已经做过处理
                    //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，
                    // 并判断total_amount是否确实为该订单的实际金额（即商户订单创建时的金额），并执行商户的业务程序
                    //请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
                    //如果有做过处理，不执行商户的业务程序

                    //注意：
                    //如果签约的是可退款协议，退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
                    //如果没有签约可退款协议，那么付款完成后，支付宝系统发送该交易状态通知。




                    VipPackage vipPackage = vipPackageMapper.selectById(order.getVip());
                    Calendar now = Calendar.getInstance();

                    Integer everyMonth = 31;
                    everyMonth *= vipPackage.getMonth();

                    Account account = accountMapper.selectById(order.getAccount());

                    if(accountVipMapper.selectCount(new QueryWrapper<AccountVip>().eq("account_id",account.getId())) < 1){
                        AccountVip accountVip = new AccountVip();
                        accountVip.setPayNo(out_trade_no);
                        accountVip.setAccountId(account.getId());
                        accountVip.setVipPackAgeId(vipPackage.getId());
                        accountVip.setCreateTime(now.getTime());
                        now.add(Calendar.DATE, everyMonth);
                        accountVip.setEndTime(now.getTime());
                        if(accountVipMapper.insert(accountVip) < 1){
                            throw new RuntimeException("更新vip状态失败");
                        }
                        Role role = roleMapper.selectOne(new QueryWrapper<Role>().eq("role", "ROLE_VIP"));
                        if(accountRoleMapper.selectCount(new QueryWrapper<AccountRole>().eq("account_id",account.getId()).eq("role_id",role.getId())) < 1){
                            AccountRole accountRole = new AccountRole();
                            accountRole.setRoleId(role.getId());
                            accountRole.setAccountId(accountVip.getAccountId());
                            if (accountRoleMapper.insert(accountRole) < 1) {
                                throw new RuntimeException("fail");
                            }
                        }
                    }else {
                        AccountVip accountVip = accountVipMapper.selectOne(new QueryWrapper<AccountVip>().eq("account_id", account.getId()));
                        now.add(Calendar.DATE, accountVip.getEndTime().getDay());
                        now.add(Calendar.DATE, everyMonth);
                        accountVip.setEndTime(now.getTime());
                        if (accountVipMapper.updateById(accountVip) < 1) {
                            throw new RuntimeException("更新vip状态失败");
                        }
                    }

                    order.setTrade(trade_no);
                    order.setStatus(1);
                    if (orderMapper.updateById(order) < 1){
                        throw new RuntimeException("更新订单失败");
                    }


                } else if (tradeStatus.equals("TRADE_SUCCESS")) {
                    //判断该笔订单是否在商户网站中已经做过处理
                    //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，
                    // 并判断total_amount是否确实为该订单的实际金额（即商户订单创建时的金额），并执行商户的业务程序
                    //请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
                    //如果有做过处理，不执行商户的业务程序
                    //注意：
                    //如果签约的是可退款协议，那么付款完成后，支付宝系统发送该交易状态通知。

                    VipPackage vipPackage = vipPackageMapper.selectById(order.getVip());
                    Calendar now = Calendar.getInstance();

                    Integer everyMonth = 31;
                    everyMonth *= vipPackage.getMonth();

                    Account account = accountMapper.selectById(order.getAccount());

                    if(accountVipMapper.selectCount(new QueryWrapper<AccountVip>().eq("account_id",account.getId())) < 1){
                        AccountVip accountVip = new AccountVip();
                        accountVip.setPayNo(out_trade_no);
                        accountVip.setAccountId(account.getId());
                        accountVip.setVipPackAgeId(vipPackage.getId());
                        accountVip.setCreateTime(now.getTime());
                        now.add(Calendar.DATE, everyMonth);
                        accountVip.setEndTime(now.getTime());
                        if(accountVipMapper.insert(accountVip) < 1){
                            throw new RuntimeException("更新vip状态失败");
                        }
                        Role role = roleMapper.selectOne(new QueryWrapper<Role>().eq("role", "ROLE_VIP"));
                        if(accountRoleMapper.selectCount(new QueryWrapper<AccountRole>().eq("account_id",account.getId()).eq("role_id",role.getId())) < 1){
                            AccountRole accountRole = new AccountRole();
                            accountRole.setRoleId(role.getId());
                            accountRole.setAccountId(accountVip.getAccountId());
                            if (accountRoleMapper.insert(accountRole) < 1) {
                                throw new RuntimeException("fail");
                            }
                        }
                    }else {
                        AccountVip accountVip = accountVipMapper.selectOne(new QueryWrapper<AccountVip>().eq("account_id", account.getId()));
                        now.add(Calendar.DATE, accountVip.getEndTime().getDay());
                        now.add(Calendar.DATE, everyMonth);
                        accountVip.setEndTime(now.getTime());
                        if (accountVipMapper.updateById(accountVip) < 1) {
                            throw new RuntimeException("更新vip状态失败");
                        }
                    }

                    order.setTrade(trade_no);
                    order.setStatus(1);
                    if (orderMapper.updateById(order) < 1){
                        throw new RuntimeException("更新订单失败");
                    }

                }

                return "success";
            }
            return "error";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    @Override
    public boolean rsaCheckV1(HttpServletRequest request) {
        // https://docs.open.alipay.com/54/106370
        // 获取支付宝POST过来反馈信息
        Map<String, String> params = new HashMap<>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }

        try {

            return AlipaySignature.rsaCheckV1(params,
                    aliPayConfig.merchant_private_key,
                    aliPayConfig.charset,
                    aliPayConfig.sign_type);
        } catch (AlipayApiException e) {

            return true;
        }
    }
}
