package com.hcyacg.pixiv.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.hcyacg.pixiv.bean.JwtOperation;
import com.hcyacg.pixiv.constant.AppConstant;
import com.hcyacg.pixiv.dto.*;
import com.hcyacg.pixiv.entity.*;
import com.hcyacg.pixiv.mapper.*;
import com.hcyacg.pixiv.service.AccountService;
import com.hcyacg.pixiv.service.IllustService;
import com.hcyacg.pixiv.utils.COSUtils;
import com.hcyacg.pixiv.utils.HttpUtils;
import com.hcyacg.pixiv.utils.MinioUilts;
import com.hcyacg.pixiv.utils.RedisUtils;
import io.minio.PutObjectOptions;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.System;
import java.util.*;

/**
 * Created: 黄智文
 * Desc: 插画业务层实现
 * Date: 2020/5/12 17:42
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class IllustServiceImpl implements IllustService {

    @Autowired
    private AccountService accountService;
    @Autowired
    private HttpUtils httpUtils;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private MinioUilts minioUilts;
    @Autowired
    private IllustMapper illustMapper;
    @Autowired
    private RoleMapper accessLevelMapper;
    @Autowired
    private OriginalMapper originalMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private TagOfIllustMapper tagOfIllustMapper;
    @Value("${minio.domin}")
    private String domin;
    @Autowired
    private COSUtils cosUtils;
    @Autowired
    private HttpServletResponse res;
    @Autowired
    private JwtOperation jwtOperation;

    @Override
    public Result detail(String illustId, String authorization, boolean reduction) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet();
        ByteArrayOutputStream infoStream = new ByteArrayOutputStream();
        JSONObject jsonObject = null;
        Object illustData = null;
        PutObjectOptions putObjectOptions = null;
        String suffix = null;
        String url = null;
        try {
            if (StringUtils.isBlank(illustId)) {
                return new Result(400, "请求失败", "", "插画id不能为空");
            }

            //判断是否直接将原数据返回
            if (reduction) {
                if (redisUtils.hasKey(AppConstant.DETAIL_REDIS + illustId + "::" + reduction)) {
                    return httpUtils.setBuild(res, new Result(201, "获取成功", redisUtils.get(AppConstant.DETAIL_REDIS + illustId + "::" + reduction), null));
                }

                httpGet = httpUtils.get(AppConstant.APP_API_URL + "/v1/illust/detail?illust_id=" + illustId);
                httpGet.addHeader("App-OS", "ios");
                httpGet.addHeader("App-OS-Version", "12.2");
                httpGet.addHeader("App-Version", "7.6.2");
                httpGet.addHeader("User-Agent", "PixivIOSApp/7.6.2 (iOS 12.2; iPhone9,1)");

                Object token1 = redisUtils.get("token");
                if (StringUtils.isBlank(String.valueOf(token1)) || token1 == null) {
                    accountService.getToken();
                    token1 = redisUtils.get("token");
                }
                PixivToken token = (PixivToken) token1;
                httpGet.addHeader("Authorization", "Bearer " + token.getAccessToken());


                //发送请求
                CloseableHttpResponse response = httpClient.execute(httpGet);
                InputStream in = response.getEntity().getContent();
                byte[] buffer = new byte[1];
                int len = 0;
                String data = "";
                while ((len = in.read(buffer)) > 0) {
                    infoStream.write(buffer, 0, len);
                }

                infoStream.close();
                httpClient.close();
                redisUtils.set(AppConstant.DETAIL_REDIS + illustId + "::" + reduction, JSONObject.parseObject(infoStream.toString("UTF-8")), 7 * 24 * 60 * 60L);

                return httpUtils.setBuild(res, new Result(201, "获取成功", redisUtils.get(AppConstant.DETAIL_REDIS + illustId + "::" + reduction), null));
            }

            //判断是否是r18
            if (redisUtils.hasKey(AppConstant.DETAIL_REDIS + illustId) && redisUtils.hasKey(AppConstant.DETAIL_REDIS + "login::" + illustId)) {
                //判断是否登录
                if (StringUtils.isNotBlank(authorization) && !authorization.equals("0")) {
                    Account account = JSON.parseObject(String.valueOf(jwtOperation.parseJwt(authorization).get("account")), Account.class);
                    //判断是否有权限看
                    if (AppConstant.ACCOUNT_HAS_PRON.equals(account.getHasPron())) {
                        return httpUtils.setBuild(res, new Result(201, "获取成功", redisUtils.get(AppConstant.DETAIL_REDIS + "login::" + illustId), null));
                    } else {
                        return httpUtils.setBuild(res, new Result(201, "获取成功", redisUtils.get(AppConstant.DETAIL_REDIS + illustId), null));
                    }
                }
                return httpUtils.setBuild(res, new Result(201, "获取成功", redisUtils.get(AppConstant.DETAIL_REDIS + illustId), null));
            }

            //判断缓存中是否有数据
            if (redisUtils.hasKey(AppConstant.DETAIL_REDIS + illustId)) {
                return httpUtils.setBuild(res, new Result(201, "获取成功", redisUtils.get(AppConstant.DETAIL_REDIS + illustId), null));
            }

            //如果数据库中有数据
            List<Illust> illusts = illustMapper.selectList(new QueryWrapper<Illust>().eq("illust", illustId));
            if (CollectionUtils.isNotEmpty(illusts)) {
                //从数据库中获取该插画信息
                Illust illust1 = illustMapper.selectOne(new QueryWrapper<Illust>().eq("illust", illustId));
                IllustDto illustDto = new IllustDto();
                illustDto.setIllust(illust1.getIllustId());
                illustDto.setType(illust1.getType());
                illustDto.setCaption(illust1.getCaption());
                illustDto.setTitle(illust1.getTitle());
                illustDto.setRestrict(illust1.getRestrictN());
                illustDto.setCreateDate(illust1.getCreateDate());
                illustDto.setPageCount(illust1.getPageCount());
                illustDto.setWidth(illust1.getWidth());
                illustDto.setHeight(illust1.getHeight());
                illustDto.setSanityLevel(illust1.getSanityLevel());
                illustDto.setxRestrict(illust1.getxRestrict());
                illustDto.setSeries(illust1.getSeries());
                illustDto.setTotalView(illust1.getTotalView());
                illustDto.setTotalBookmarks(illust1.getTotalBookmarks());
                illustDto.setIsBookmarked(illust1.getIsBookmarked());
                illustDto.setVisible(illust1.getVisible());
                illustDto.setIsMuted(illust1.getIsMuted());
                illustDto.setTotalComments(illust1.getTotalComments());

                //作者信息
                User user1 = userMapper.selectOne(new QueryWrapper<User>().eq("userid", illust1.getUserId()));
                UserDto userDto = new UserDto();
                userDto.setId(user1.getUserId());
                userDto.setAccount(user1.getAccount());
                userDto.setName(user1.getName());
                userDto.setIsFollowed(user1.getIsFollowed());
                userDto.setProfileImageUrls(domin + user1.getProfileImageUrls());
                illustDto.setUser(userDto);

                //获取标签
                List<TagOfIllust> tagOfIllusts = tagOfIllustMapper.selectList(new QueryWrapper<TagOfIllust>().eq("illust", illust1.getIllustId()));
                List<TagDto> tagDtos = new ArrayList<>();
                for (TagOfIllust tagOfIllust : tagOfIllusts) {

                    Tag tag = tagMapper.selectById(tagOfIllust.getTagId());
                    TagDto tagDto = new TagDto();
                    tagDto.setName(tag.getName());
                    tagDto.setTranslatedName(tag.getTranslatedName());
                    tagDtos.add(tagDto);
                }
                illustDto.setTags(tagDtos);

                //获取高清图片
                List<Original> originals = originalMapper.selectList(new QueryWrapper<Original>().eq("illustid", illust1.getIllustId()));
                List<OriginalDto> originalDtos = new ArrayList<>();
                for (Original original : originals) {
                    OriginalDto originalDto = new OriginalDto();
                    originalDto.setUrl(domin + original.getUrl());

                    originalDtos.add(originalDto);
                }

                //首先判读是否是18X
                if (illust1.getxRestrict().equals("1")) {

                    illustDto.setLarge(null);
                    illustDto.setOriginals(null);
                    //添加无需登录的缓存
                    redisUtils.set(AppConstant.DETAIL_REDIS + illustId, illustDto, 7 * 24 * 60 * 60L);

                    illustDto.setLarge(domin + illust1.getLarge());
                    illustDto.setOriginals(originalDtos);
                    //添加需要登录且有权限的缓存
                    redisUtils.set(AppConstant.DETAIL_REDIS + "login::" + illustId, illustDto, 7 * 24 * 60 * 60L);

                } else {
                    illustDto.setLarge(domin + illust1.getLarge());
                    illustDto.setOriginals(originalDtos);
                    //添加无需登录的缓存
                    redisUtils.set(AppConstant.DETAIL_REDIS + illustId, illustDto, 7 * 24 * 60 * 60L);
                    return httpUtils.setBuild(res, new Result(201, "获取成功", illustDto, null));
                }

                //判断是否登录
                if (StringUtils.isNotBlank(authorization) && !authorization.equals("0")) {
                    Account account = JSON.parseObject(String.valueOf(jwtOperation.parseJwt(authorization).get("account")), Account.class);
                    //判断是否有权限看
                    if (AppConstant.ACCOUNT_HAS_PRON.equals(account.getHasPron())) {
                        return httpUtils.setBuild(res, new Result(201, "获取成功", redisUtils.get(AppConstant.DETAIL_REDIS + "login::" + illustId), null));
                    } else {
                        return httpUtils.setBuild(res, new Result(201, "获取成功", redisUtils.get(AppConstant.DETAIL_REDIS + illustId), null));
                    }
                }


                return httpUtils.setBuild(res, new Result(201, "获取成功", redisUtils.get(AppConstant.DETAIL_REDIS + illustId), null));
            } else {
                //没有数据存入数据库
                httpGet = httpUtils.get(AppConstant.APP_API_URL + "/v1/illust/detail?illust_id=" + illustId);
                httpGet.addHeader("App-OS", "ios");
                httpGet.addHeader("App-OS-Version", "12.2");
                httpGet.addHeader("App-Version", "7.6.2");
                httpGet.addHeader("User-Agent", "PixivIOSApp/7.6.2 (iOS 12.2; iPhone9,1)");

                Object token1 = redisUtils.get("token");
                if (StringUtils.isBlank(String.valueOf(token1)) || token1 == null) {
                    accountService.getToken();
                    token1 = redisUtils.get("token");
                }
                PixivToken token = (PixivToken) token1;
                httpGet.addHeader("Authorization", "Bearer " + token.getAccessToken());


                //发送请求
                CloseableHttpResponse response = httpClient.execute(httpGet);
                InputStream in = response.getEntity().getContent();
                byte[] buffer = new byte[1];
                int len = 0;
                String data = "";
                while ((len = in.read(buffer)) > 0) {
                    infoStream.write(buffer, 0, len);
                }

                infoStream.close();
                httpClient.close();

                //解析json并存入数据库
                jsonObject = JSONObject.parseObject(infoStream.toString("UTF-8"));
                illustData = jsonObject.get("illust");
                jsonObject = JSONObject.parseObject(String.valueOf(illustData));

                Illust illust = new Illust();
                illust.setIllustId(illustId);
                illust.setTitle(jsonObject.getString("title"));
                illust.setType(jsonObject.getString("type"));
                illust.setCaption(jsonObject.getString("caption"));
                illust.setRestrictN(jsonObject.getString("restrict"));
                illust.setCreateDate(AppConstant.SDF4.format(AppConstant.SDF3.parse(jsonObject.getString("create_date"))));
                illust.setPageCount(jsonObject.getString("page_count"));
                illust.setWidth(jsonObject.getString("width"));
                illust.setHeight(jsonObject.getString("height"));
                illust.setSanityLevel(jsonObject.getString("sanity_level"));
                illust.setxRestrict(jsonObject.getString("x_restrict"));
                illust.setSeries(jsonObject.getString("series"));
                illust.setTotalView(jsonObject.getString("total_view"));
                illust.setTotalBookmarks(jsonObject.getString("total_bookmarks"));
                illust.setIsBookmarked(jsonObject.getString("is_bookmarked"));
                illust.setVisible(jsonObject.getString("visible"));
                illust.setIsMuted(jsonObject.getString("is_muted"));
                illust.setTotalComments(jsonObject.getString("total_comments"));


                //缩略图上传添加
                Object image_urls = jsonObject.get("image_urls");
                jsonObject = JSONObject.parseObject(String.valueOf(image_urls));
                url = jsonObject.getString("large");
                suffix = String.valueOf(url).substring((String.valueOf(url).lastIndexOf(".") + 1), url.toString().length());
                in = new ByteArrayInputStream(urlLook(url, false).toByteArray());
                putObjectOptions = new PutObjectOptions(in.available(), -1);
                putObjectOptions.setContentType("image/" + suffix);

                illust.setLarge(minioUilts.uploadThumbImage(illustId + "." + suffix, in, putObjectOptions));


                //添加高清图，判断图片张数
                jsonObject = JSONObject.parseObject(String.valueOf(illustData));
                if (illust.getPageCount().matches(AppConstant.ISNUMBER)) {
                    //多图
                    if (Integer.parseInt(illust.getPageCount()) > 1) {
                        Object meta_pages = jsonObject.get("meta_pages");
                        JSONArray jsonArray = JSONArray.parseArray(String.valueOf(meta_pages));
                        for (int i = 0; i < jsonArray.size(); i++) {
                            jsonObject = JSONObject.parseObject(String.valueOf(jsonArray.get(i)));
                            image_urls = jsonObject.getString("image_urls");
                            jsonObject = JSONObject.parseObject(String.valueOf(image_urls));
                            url = jsonObject.getString("original");
                            suffix = String.valueOf(url).substring((String.valueOf(url).lastIndexOf(".") + 1), url.toString().length());
                            in = new ByteArrayInputStream(urlLook(url, false).toByteArray());
                            putObjectOptions = new PutObjectOptions(in.available(), -1);
                            putObjectOptions.setContentType("image/" + suffix);
                            Original original = new Original();
                            original.setIllustid(illustId);
                            original.setUrl(minioUilts.uploadOriginalImage(illustId + "_" + i + "_" + System.currentTimeMillis() + "." + suffix, in, putObjectOptions));
                            if (originalMapper.insert(original) < 1) {
                                throw new RuntimeException("高清图添加失败");
                            }
                        }

                    } else {
                        //单图
                        Object meta_single_page = jsonObject.get("meta_single_page");
                        jsonObject = JSONObject.parseObject(String.valueOf(meta_single_page));
                        url = jsonObject.getString("original_image_url");
                        suffix = String.valueOf(url).substring((String.valueOf(url).lastIndexOf(".") + 1), url.toString().length());
                        in = new ByteArrayInputStream(urlLook(url, false).toByteArray());
                        putObjectOptions = new PutObjectOptions(in.available(), -1);
                        putObjectOptions.setContentType("image/" + suffix);

                        Original original = new Original();
                        original.setIllustid(illustId);
                        original.setUrl(minioUilts.uploadOriginalImage(illustId + "." + suffix, in, putObjectOptions));
                        if (originalMapper.insert(original) < 1) {
                            throw new RuntimeException("高清图添加失败");
                        }


                    }
                }

                //添加作者信息
                jsonObject = JSONObject.parseObject(String.valueOf(illustData));
                Object userData = jsonObject.get("user");
                jsonObject = JSONObject.parseObject(String.valueOf(userData));
                //判断作者是否存在数据库中
                List<User> users = userMapper.selectList(new QueryWrapper<User>().eq("userid", jsonObject.getString("id")));
                if (CollectionUtils.isEmpty(users)) {
                    User user = new User();
                    user.setUserId(jsonObject.getString("id"));
                    user.setName(jsonObject.getString("name"));
                    user.setAccount(jsonObject.getString("account"));
                    user.setIsFollowed(jsonObject.getString("is_followed"));
                    Object profile_image_urls = jsonObject.getString("profile_image_urls");
                    jsonObject = JSONObject.parseObject(String.valueOf(profile_image_urls));

                    url = jsonObject.getString("medium");
                    suffix = String.valueOf(url).substring((String.valueOf(url).lastIndexOf(".") + 1), url.toString().length());
                    in = new ByteArrayInputStream(urlLook(url, false).toByteArray());
                    putObjectOptions = new PutObjectOptions(in.available(), -1);
                    putObjectOptions.setContentType("image/" + suffix);
                    user.setProfileImageUrls(minioUilts.uploadAvatarBucketImage(illustId + "." + suffix, in, putObjectOptions));
                    if (userMapper.insert(user) < 1) {
                        throw new RuntimeException("作者添加失败");
                    }
                    illust.setUserId(user.getUserId());
                } else {
                    illust.setUserId(users.get(0).getUserId());
                }


                if (illustMapper.insert(illust) < 1) {
                    throw new RuntimeException("添加插画信息失败");
                }

                //添加tag
                jsonObject = JSONObject.parseObject(String.valueOf(illustData));
                Object tags = jsonObject.get("tags");
                JSONArray jsonArray = JSONArray.parseArray(String.valueOf(tags));
                //将标签Id记录
                List<Integer> tagList = new ArrayList<>();
                for (Object o : jsonArray) {
                    jsonObject = JSONObject.parseObject(String.valueOf(o));

                    //判断标签是否存在数据库中
                    List<Tag> tags1 = tagMapper.selectList(new QueryWrapper<Tag>().eq("name", jsonObject.getString("name")));
                    if (tags1.size() == 0) {
                        Tag tag = new Tag();
                        tag.setName(jsonObject.getString("name"));
                        tag.setTranslatedName(jsonObject.getString("translated_name"));
                        if (tagMapper.insert(tag) < 1) {
                            throw new RuntimeException("标签入库失败");
                        }

                        tagList.add(tag.getId());
                    } else {
                        tagList.add(tags1.get(0).getId());
                    }

                }

                for (Integer integer : tagList) {
                    TagOfIllust tagOfIllust = new TagOfIllust();
                    tagOfIllust.setTagId(integer);
                    tagOfIllust.setIllust(illustId);
                    if (tagOfIllustMapper.insert(tagOfIllust) < 1) {
                        throw new RuntimeException("对应标签添加失败");
                    }
                }
            }


            //从数据库中获取该插画信息
            Illust illust1 = illustMapper.selectOne(new QueryWrapper<Illust>().eq("illust", illustId));
            IllustDto illustDto = new IllustDto();
            illustDto.setIllust(illust1.getIllustId());
            illustDto.setType(illust1.getType());
            illustDto.setCaption(illust1.getCaption());
            illustDto.setTitle(illust1.getTitle());
            illustDto.setRestrict(illust1.getRestrictN());
            illustDto.setLarge(domin + illust1.getLarge());
            illustDto.setCreateDate(illust1.getCreateDate());
            illustDto.setPageCount(illust1.getPageCount());
            illustDto.setWidth(illust1.getWidth());
            illustDto.setHeight(illust1.getHeight());
            illustDto.setSanityLevel(illust1.getSanityLevel());
            illustDto.setxRestrict(illust1.getxRestrict());
            illustDto.setSeries(illust1.getSeries());
            illustDto.setTotalView(illust1.getTotalView());
            illustDto.setTotalBookmarks(illust1.getTotalBookmarks());
            illustDto.setIsBookmarked(illust1.getIsBookmarked());
            illustDto.setVisible(illust1.getVisible());
            illustDto.setIsMuted(illust1.getIsMuted());
            illustDto.setTotalComments(illust1.getTotalComments());

            //作者信息
            User user1 = userMapper.selectOne(new QueryWrapper<User>().eq("userid", illust1.getUserId()));
            UserDto userDto = new UserDto();
            userDto.setId(user1.getUserId());
            userDto.setAccount(user1.getAccount());
            userDto.setName(user1.getName());
            userDto.setIsFollowed(user1.getIsFollowed());
            userDto.setProfileImageUrls(domin + user1.getProfileImageUrls());
            illustDto.setUser(userDto);

            //获取标签
            List<TagOfIllust> tagOfIllusts = tagOfIllustMapper.selectList(new QueryWrapper<TagOfIllust>().eq("illust", illust1.getIllustId()));
            List<TagDto> tagDtos = new ArrayList<>();
            for (TagOfIllust tagOfIllust : tagOfIllusts) {

                Tag tag = tagMapper.selectById(tagOfIllust.getTagId());
                TagDto tagDto = new TagDto();
                tagDto.setName(tag.getName());
                tagDto.setTranslatedName(tag.getTranslatedName());
                tagDtos.add(tagDto);
            }
            illustDto.setTags(tagDtos);

            //获取高清图片
            List<Original> originals = originalMapper.selectList(new QueryWrapper<Original>().eq("illustid", illust1.getIllustId()));
            List<OriginalDto> originalDtos = new ArrayList<>();
            for (Original original : originals) {
                OriginalDto originalDto = new OriginalDto();
                originalDto.setUrl(domin + original.getUrl());

                originalDtos.add(originalDto);
            }
            illustDto.setOriginals(originalDtos);

            //首先判读是否是18X
            if (illust1.getxRestrict().equals("1")) {

                illustDto.setLarge(null);
                illustDto.setOriginals(null);
                //添加无需登录的缓存
                redisUtils.set(AppConstant.DETAIL_REDIS + illustId, illustDto, 7 * 24 * 60 * 60L);

                illustDto.setLarge(domin + illust1.getLarge());
                illustDto.setOriginals(originalDtos);
                //添加需要登录且有权限的缓存
                redisUtils.set(AppConstant.DETAIL_REDIS + "login::" + illustId, illustDto, 7 * 24 * 60 * 60L);

            } else {
                illustDto.setLarge(domin + illust1.getLarge());
                illustDto.setOriginals(originalDtos);
                //添加无需登录的缓存
                redisUtils.set(AppConstant.DETAIL_REDIS + illustId, illustDto, 7 * 24 * 60 * 60L);
                return httpUtils.setBuild(res, new Result(201, "获取成功", illustDto, null));
            }

            //判断是否登录
            if (StringUtils.isNotBlank(authorization) && !authorization.equals("0")) {
                Account account = JSON.parseObject(String.valueOf(jwtOperation.parseJwt(authorization).get("account")), Account.class);
                //判断是否有权限看
                if (AppConstant.ACCOUNT_HAS_PRON.equals(account.getHasPron())) {
                    return httpUtils.setBuild(res, new Result(201, "获取成功", redisUtils.get(AppConstant.DETAIL_REDIS + "login::" + illustId), null));
                } else {
                    return httpUtils.setBuild(res, new Result(201, "获取成功", redisUtils.get(AppConstant.DETAIL_REDIS + illustId), null));
                }
            }


            return httpUtils.setBuild(res, new Result(201, "获取成功", redisUtils.get(AppConstant.DETAIL_REDIS + illustId), null));
        } catch (Exception e) {
            e.printStackTrace();
            return httpUtils.setBuild(res, new Result(500, "请求失败", "", e.getMessage()));
        } finally {
            assert httpGet != null;
            httpGet.abort();
        }
    }

    @Override
    public String comments(String illustId) {
        if (StringUtils.isBlank(illustId)) {
            return JSONObject.toJSONString(new Result(400, "请求失败", "", "插画id不能为空"));
        }
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = null;
        ByteArrayOutputStream infoStream = new ByteArrayOutputStream();
        try {
            httpGet = httpUtils.get(AppConstant.APP_API_URL + "/v1/illust/comments?illust_id=" + illustId);
            httpGet.addHeader("App-OS", "ios");
            httpGet.addHeader("App-OS-Version", "12.2");
            httpGet.addHeader("App-Version", "7.6.2");
            httpGet.addHeader("User-Agent", "PixivIOSApp/7.6.2 (iOS 12.2; iPhone9,1)");

            Object token1 = redisUtils.get("token");
            if (StringUtils.isBlank(String.valueOf(token1)) || token1 == null) {
                accountService.getToken();
                token1 = redisUtils.get("token");
            }
            PixivToken token = (PixivToken) token1;
            System.out.println(token);
            httpGet.addHeader("Authorization", "Bearer " + token.getAccessToken());


            //发送请求
            CloseableHttpResponse response = httpClient.execute(httpGet);
            InputStream in = response.getEntity().getContent();
            byte[] buffer = new byte[1];
            int len = 0;
            String data = "";
            while ((len = in.read(buffer)) > 0) {
                infoStream.write(buffer, 0, len);
            }

            infoStream.close();
            httpClient.close();
            return infoStream.toString("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return JSONObject.toJSONString(new Result(500, "请求失败", "", e.getMessage()));
        } finally {
            assert httpGet != null;
            httpGet.abort();
        }
    }


    @Override
    public ByteArrayOutputStream urlLook(String url, Boolean cache) {


        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("");
        ByteArrayOutputStream infoStream = new ByteArrayOutputStream();
        Integer code = null;
        String suffix = null;
        boolean flag = false;

        try {

            //计算访问的次数
            redisUtils.incr(AppConstant.URl_LOOK_NUMBER_REDIS, 1);
            redisUtils.expire(AppConstant.URl_LOOK_NUMBER_REDIS, 60);

            if (redisUtils.get(AppConstant.URl_LOOK_REDIS + url) != null) {
                return (ByteArrayOutputStream) redisUtils.get(AppConstant.URl_LOOK_REDIS + url);
            }

            suffix = String.valueOf(url).substring((String.valueOf(url).lastIndexOf(".") + 1), url.toString().length());

            httpGet = httpUtils.get(url);
            httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.140 Safari/537.36 Edge/17.17134");
            httpGet.addHeader("referer", AppConstant.APP_API_URL);


            //发送请求
            CloseableHttpResponse response = httpClient.execute(httpGet);
            code = response.getStatusLine().getStatusCode();
            InputStream in = response.getEntity().getContent();
            byte[] buffer = new byte[1024];
            int len = 0;
            String data = "";
            while ((len = in.read(buffer)) > 0) {
                infoStream.write(buffer, 0, len);
            }
            infoStream.close();
            httpClient.close();
            if (cache) {
                redisUtils.set(AppConstant.URl_LOOK_REDIS + url, infoStream, 7 * 24 * 60 * 60L);
            }

            //infoStream.toString("UTF-8");
            return infoStream;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            assert httpGet != null;
            httpGet.abort();
        }
    }


    @Override
    public String ranking(String mode, String date, String offset) {


        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet();
        ByteArrayOutputStream infoStream = new ByteArrayOutputStream();
        try {

            if (StringUtils.isBlank(mode)) {
                mode = "day";
            }
            Date time = new Date();
            try {
                AppConstant.SDF.parse(date);
            } catch (Exception a) {
                a.printStackTrace();
                date = AppConstant.SDF.format(time);
            }

            if (StringUtils.isBlank(date)) {
                date = AppConstant.SDF.format(time);
            }

            if (StringUtils.isBlank(offset)) {
                httpGet = httpUtils.get(AppConstant.APP_API_URL + "/v1/illust/ranking?mode=" + mode + "&date=" + date + "&filter=for_ios");
            } else {
                httpGet = httpUtils.get(AppConstant.APP_API_URL + "/v1/illust/ranking?mode=" + mode + "&date=" + date + "&filter=for_ios&offset=" + offset);
            }


            httpGet.addHeader("App-OS", "ios");
            httpGet.addHeader("App-OS-Version", "12.2");
            httpGet.addHeader("App-Version", "7.6.2");
            httpGet.addHeader("User-Agent", "PixivIOSApp/7.6.2 (iOS 12.2; iPhone9,1)");

            Object token1 = redisUtils.get("token");
            if (StringUtils.isBlank(String.valueOf(token1)) || token1 == null) {
                accountService.getToken();
                token1 = redisUtils.get("token");
            }
            PixivToken token = (PixivToken) token1;
            httpGet.addHeader("Authorization", "Bearer " + token.getAccessToken());


            //发送请求
            CloseableHttpResponse response = httpClient.execute(httpGet);
            InputStream in = response.getEntity().getContent();
            byte[] buffer = new byte[1];
            int len = 0;
            String data = "";
            while ((len = in.read(buffer)) > 0) {
                infoStream.write(buffer, 0, len);
            }

            infoStream.close();
            httpClient.close();
            return infoStream.toString("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return JSONObject.toJSONString(new Result(500, "请求失败", "", e.getMessage()));
        } finally {
            assert httpGet != null;
            httpGet.abort();
        }
    }

    @Override
    public Result getPicNumber() {
        try {

            //总量
            Integer all = illustMapper.selectCount(null);

            //18x插画
            Integer x18 = illustMapper.selectCount(new QueryWrapper<Illust>().eq("type", "illust").eq("x_restrict", "1").gt("total_bookmarks", 3000).gt("total_view",10000));

            //插画
            Integer illust = illustMapper.selectCount(new QueryWrapper<Illust>().eq("type", "illust"));

            //漫画 manga
            Integer manga = illustMapper.selectCount(new QueryWrapper<Illust>().eq("type", "manga"));

            //动图 ugoira
            Integer ugoira = illustMapper.selectCount(new QueryWrapper<Illust>().eq("type", "ugoira"));

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("all", all);
            map.put("x18", x18);
            map.put("illust", illust);
            map.put("manga", manga);
            map.put("ugoira", ugoira);
            return httpUtils.setBuild(res, new Result(201, "获取成功", map, null));
        } catch (Exception e) {
            e.printStackTrace();
            return httpUtils.setBuild(res, new Result(500, "获取失败", null, e.getMessage()));
        }
    }
}
