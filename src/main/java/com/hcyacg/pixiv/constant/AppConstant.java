package com.hcyacg.pixiv.constant;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @Author: Nekoer
 * @Desc: 公共常量
 * @Date: 2020/5/13 15:27
 */
public class AppConstant {

    /**
     * pixiv接口api
     */
    public static final String APP_API_URL = "https://app-api.pixiv.net";

    /**
     * 登录地址
     */
    public static final String OAUTH_URL = "https://oauth.secure.pixiv.net";


    /**
     * 时间校验
     */
    public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat SDF2 = new SimpleDateFormat("yyyyMMdd");
    public static final SimpleDateFormat SDF3 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    public static final SimpleDateFormat SDF4 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 判断是否是数字
     */
    public static final String ISNUMBER = "^-?[0-9]+$";

    /**
     * HTTP状态码
     */
    public static final List<Integer> HTTP_CODE_SUCCESS = Arrays.asList(200, 201);
    public static final List<Integer> HTTP_CODE_ERROR = Arrays.asList(100, 101, 202, 203, 204, 205, 206, 300, 301, 302, 303, 304, 305, 306, 307, 308, 400, 401, 402, 403, 404, 405, 406, 407, 408, 409, 410, 411, 412, 413, 414, 415, 416, 417, 500, 501, 502, 503, 504, 505);

    /**
     * 标签存在redis的key
     */
    public static final String TAGS_REDIS = "tags::";
    /**
     * 排行榜key
     */
    public static final String RANKING_REDIS = "ranking::";

    /**
     * 搜索 redisKey
     */
    public static final String SEARCH_REDIS = "search::";

    /**
     * 搜索作者
     */
    public static final String SEARCH_USER_REDIS = "search:user:";

    /**
     * 作者id寻信息
     */
    public static final String SEARCH_USER_ID_DETAILS_REDIS = "search:user:id::details:";

    /**
     * 作者id寻作品
     */
    public static final String SEARCH_USER_ID_ILLUSTS_REDIS = "search:user:id:illusts:";



    /**
     * 详情 redisKey
     */
    public static final String DETAIL_REDIS = "detail::";



    /**
     * 图片访问 redisKey
     */
    public static final String URl_LOOK_REDIS = "urlLook::";

    /**
     * 使用UrlLook的次数
     */
    public static final String URl_LOOK_NUMBER_REDIS = "urlLook::number";

    /**
     * 注册邮件验证码
     */
    public static final String CODE_EMAIL = "email:code:register:";


    /**
     * 更新用户数据 邮件验证码
     */
    public static final String CODE_EMAIL_UPDATE = "email:code:update:";

    /**
     * 登录验证码
     */
    public static final String CODE_EMAIL_VALIDATE = "login:code:";

    /**
     * 更换邮箱验证码
     */
    public static final String CODE_EMAIL_CHANGE_EMAIL_CODE = "email:code:changeEmail::";

    /**
     * 忘记密码状态 发送验证码
     */
    public static final String CODE_EMAIL_FORGET_CHANGE_PASSWORD_CODE = "email:code:forget:change:password::";


    /**
     * 是否拥有18权限
     */
    public static final Integer ACCOUNT_HAS_PRON = 1;
    public static final Integer ACCOUNT_NOT_HAS_PRON = 0;

    /**
     * 每日爬取的数据总量
     */
    public static final String PIC_INFO_NUMBER = "pic::number";


    /**
     * 性别 1为男，0为女，-1保密
     */
    public static final Integer ACCOUNT_MALE = 1;
    public static final Integer ACCOUNT_FEMALE = 0;
    public static final Integer ACCOUNT_SECRECY = -1;


    /**
     *校验手机格式
     */
    public static final Pattern CHINA_IPHONE = Pattern.compile("^((13[0-9])|(14[0,1,4-9])|(15[0-3,5-9])|(16[2,5,6,7])|(17[0-8])|(18[0-9])|(19[0-3,5-9]))\\d{8}$");

    public static final Pattern EMAIL_PATTERN = Pattern.compile("^\\w+@(\\w+\\.){1,2}\\w+$");
    /**
     * 用户封号key
     */
    public static final Integer ACCOUNT_BAN_YES = 1;
    public static final Integer ACCOUNT_BAN_NO = 0;


    /**
     * 有趣的图key
     */
    public static final String AMAZING_PIC = "amazing::pic::";


    /**
     * 普通用户权限
     */
    public static final List<Integer> ACCOUNT_ACCESS_LEVEL = Arrays.asList(3,4,5,6,7,8,9);


    /**
     * 系统参数
     */
    //TOKEN默认调用次数
    public static final String SYSTEM_TOKEN_DEFAULT_NUMBER = "TOKEN_DEFAULT_NUMBER";
    //IP默认调用次数
    public static final String SYSTEM_IP_DEFAULT_NUMBER = "IP_DEFAULT_NUMBER";
    //未知默认调用次数
    public static final String SYSTEM_UNKNOWN_IP_DEFAULT_NUMBER = "UNKNOWN_IP_DEFAULT_NUMBER";
    //是否允许注册
    public static final String SYSTEM_ALLOWED_TO_REGISTER = "ALLOWED_TO_REGISTER";
    //邀请码注册
    public static final String SYSTEM_LNVITATION_CODE = "LNVITATION_CODE";



}
