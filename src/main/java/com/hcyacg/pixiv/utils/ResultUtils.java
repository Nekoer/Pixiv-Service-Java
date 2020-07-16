package com.hcyacg.pixiv.utils;

import com.hcyacg.pixiv.constant.AppConstant;
import com.hcyacg.pixiv.dto.Result;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created: 黄智文
 * Desc:
 * Date: 2020/5/15 18:18
 */
@Component
public class ResultUtils {

    private ResultUtils() {

    }

    public static Result setBuild(HttpServletResponse res, Result result) {

        if (AppConstant.HTTP_CODE_SUCCESS.contains(result.getCode())) {
            return buildSuccess(res, result.getCode(), result.getData());
        } else {
            return buildFailure(res, result.getCode(), String.valueOf(result.getError()), result.getData());
        }
    }

    public static Result buildSuccess(HttpServletResponse res, Integer code, Object data) {

        Result result = new Result();
        res.setStatus(code);
        result.setCode(code);
        result.setData(data);
        return result;
    }

    public static Result buildFailure(HttpServletResponse res, Integer code, String errMsg, Object data) {

        Result result = new Result();
        res.setStatus(code);
        result.setCode(code);
        result.setError(errMsg);
        result.setData(data);
        return result;
    }

    /**
     * 判断字符串是否为URL
     *
     * @param urls 用户头像key
     * @return true:是URL、false:不是URL
     */
    public static boolean isHttpUrl(String urls) {
        String regex = "^([hH][tT]{2}[pP]:/*|[hH][tT]{2}[pP][sS]:/*|[fF][tT][pP]:/*)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+(\\?{0,1}(([A-Za-z0-9-~]+\\={0,1})([A-Za-z0-9-~]*)\\&{0,1})*)$";

        Pattern pat = Pattern.compile(regex.trim());//比对
        Matcher mat = pat.matcher(urls.trim());
        return mat.matches();
    }


    /**
     * 获取请求的具体资源路径
     *
     * @param req 请求对象
     * @return 资源路径
     */
    public static String getMappingUri(HttpServletRequest req) {
        String servletPath = req.getServletPath();
        //  /OldDriverServer/apis/heroes/paging
        String uri = req.getRequestURI();
        //替换掉前面的项目名和组件名，得到最终的处理器映射路径
        String mappingUri = uri.replace(req.getContextPath(), "").replace(servletPath, "");
        return mappingUri;
    }
}
