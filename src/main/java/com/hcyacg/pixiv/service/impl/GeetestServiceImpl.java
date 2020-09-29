package com.hcyacg.pixiv.service.impl;

import com.hcyacg.pixiv.dto.GeetestLibResult;
import com.hcyacg.pixiv.service.GeetestService;
import com.hcyacg.pixiv.utils.GeetestUtils;
import com.hcyacg.pixiv.utils.IpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author Nekoer
 * @Date 2020/9/29 12:24
 * @Desc
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class GeetestServiceImpl implements GeetestService{

    @Autowired
    private HttpServletResponse res;
    @Autowired
    private HttpServletRequest req;

    @Autowired
    private GeetestUtils geetestUtils;
    @Autowired
    private IpUtils ipUtils;

    @Override
    public void doGet(String userId,String clientType) {

        /*
        必传参数
            digestmod 此版本sdk可支持md5、sha256、hmac-sha256，md5之外的算法需特殊配置的账号，联系极验客服
        自定义参数,可选择添加
            user_id 客户端用户的唯一标识，确定用户的唯一性；作用于提供进阶数据分析服务，可在register和validate接口传入，不传入也不影响验证服务的使用；若担心用户信息风险，可作预处理(如哈希处理)再提供到极验
            client_type 客户端类型，web：电脑上的浏览器；h5：手机上的浏览器，包括移动应用内完全内置的web_view；native：通过原生sdk植入app应用的方式；unknown：未知
            ip_address 客户端请求sdk服务器的ip地址
        */
        try{
            res.setContentType("application/json;charset=UTF-8");
            String digestmod = "md5";
            Map<String,String> paramMap = new HashMap<String, String>();
            paramMap.put("digestmod", digestmod);
            paramMap.put("user_id", userId);
            paramMap.put("client_type", clientType);

            paramMap.put("ip_address", ipUtils.getIpAddr(req));
            GeetestLibResult result = geetestUtils.register(digestmod, paramMap);
            // 将结果状态写到session中，此处register接口存入session，后续validate接口会取出使用
            // 注意，此demo应用的session是单机模式，格外注意分布式环境下session的应用
            req.getSession().setAttribute(geetestUtils.GEETEST_SERVER_STATUS_SESSION_KEY, result.getStatus());
            req.getSession().setAttribute("userId", userId);
            // 注意，不要更改返回的结构和值类型
            res.getWriter().println(result.getData());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void doPost(String userId,String clientType) {
        try{
            res.setContentType("application/json;charset=UTF-8");
            String challenge = req.getParameter(geetestUtils.GEETEST_CHALLENGE);
            String validate = req.getParameter(geetestUtils.GEETEST_VALIDATE);
            String seccode = req.getParameter(geetestUtils.GEETEST_SECCODE);
            int status = 0;
            try {
                // session必须取出值，若取不出值，直接当做异常退出
                status = (Integer) req.getSession().getAttribute(geetestUtils.GEETEST_SERVER_STATUS_SESSION_KEY);
                userId = (String) req.getSession().getAttribute("userId");
            } catch (Exception e) {
                res.getWriter().println(String.format("{\"result\":\"fail\",\"version\":\"%s\",\"msg\":\"session取key发生异常\"}", geetestUtils.VERSION));
                return;
            }
            GeetestLibResult result = null;
            if (status == 1) {
            /*
            自定义参数,可选择添加
                user_id 客户端用户的唯一标识，确定用户的唯一性；作用于提供进阶数据分析服务，可在register和validate接口传入，不传入也不影响验证服务的使用；若担心用户信息风险，可作预处理(如哈希处理)再提供到极验
                client_type 客户端类型，web：电脑上的浏览器；h5：手机上的浏览器，包括移动应用内完全内置的web_view；native：通过原生sdk植入app应用的方式；unknown：未知
                ip_address 客户端请求sdk服务器的ip地址
            */
                Map<String, String> paramMap = new HashMap<String, String>();
                paramMap.put("user_id", userId);
                paramMap.put("client_type", clientType);
                paramMap.put("ip_address", ipUtils.getIpAddr(req));
                result = geetestUtils.successValidate(challenge, validate, seccode, paramMap);
            } else {
                result = geetestUtils.failValidate(challenge, validate, seccode);
            }
            // 注意，不要更改返回的结构和值类型
            if (result.getStatus() == 1) {
                res.getWriter().println(String.format("{\"result\":\"success\",\"version\":\"%s\"}", geetestUtils.VERSION));
            } else {
                res.getWriter().println(String.format("{\"result\":\"fail\",\"version\":\"%s\",\"msg\":\"%s\"}", geetestUtils.VERSION, result.getMsg()));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
