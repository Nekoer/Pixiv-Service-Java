package com.hcyacg.pixiv.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hcyacg.pixiv.constant.AppConstant;
import com.hcyacg.pixiv.dto.*;
import com.hcyacg.pixiv.entity.*;
import com.hcyacg.pixiv.mapper.*;
import com.hcyacg.pixiv.service.AccountService;
import com.hcyacg.pixiv.service.PublicService;
import com.hcyacg.pixiv.utils.HttpUtils;
import com.hcyacg.pixiv.utils.RedisUtils;
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
import java.io.InputStream;
import java.lang.System;
import java.util.*;

/**
 * @Author: Nekoer
 * @Desc: 公共接口实现类
 * @Date: 2020/5/13 20:21
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PublicServiceImpl implements PublicService {
    @Autowired
    private HttpUtils httpUtils;
    @Autowired
    private HttpServletResponse res;
    @Autowired
    private HttpServletRequest req;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private AccountService accountService;
    @Autowired
    private OriginalMapper originalMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private TokenMapper tokenMapper;
    @Autowired
    private TagOfIllustMapper tagOfIllustMapper;
    @Autowired
    private IllustMapper illustMapper;
    @Value("${minio.domin}")
    private String domin;

    @Override
    public String ranking(String rankingType, String mode, String page, String perPage, String date, Boolean delCache) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet();
        ByteArrayOutputStream infoStream = new ByteArrayOutputStream();
        StringBuilder url = null;
        try {
            if (StringUtils.isBlank(rankingType)) {
                url = new StringBuilder("https://public-api.secure.pixiv.net/v1/ranking/all.json");

            } else {
                url = new StringBuilder("https://public-api.secure.pixiv.net/v1/ranking/" + rankingType + ".json");

            }

            if (delCache) {
                redisUtils.del(AppConstant.RANKING_REDIS + rankingType + mode + page + perPage + date);
            }

            if (redisUtils.hasKey(AppConstant.RANKING_REDIS + rankingType + mode + page + perPage + date)) {
                return String.valueOf(redisUtils.get(AppConstant.RANKING_REDIS + rankingType + mode + page + perPage + date));
            }

            mode = StringUtils.isBlank(mode) ? "daily" : mode;
            page = StringUtils.isBlank(page) ? "1" : page;
            perPage = StringUtils.isBlank(perPage) ? "50" : perPage;

            url.append("?mode=").append(StringUtils.isBlank(mode) ? "daily" : mode);
            url.append("&page=").append(StringUtils.isBlank(page) ? "1" : page);
            url.append("&per_page=").append(StringUtils.isBlank(perPage) ? "50" : perPage);
            url.append("&image_sizes=").append("px_128x128,px_480mw,large");
            url.append("&profile_image_sizes=").append("px_170x170,px_50x50");
            url.append("&include_stats=").append("True");
            url.append("&include_sanity_level=").append("True");


            Date time = new Date();
            url.append("&date=").append(StringUtils.isBlank(date) ? AppConstant.SDF.format(time) : AppConstant.SDF.format(AppConstant.SDF.parse(date)));

            httpGet = httpUtils.get(String.valueOf(url));
            httpGet.addHeader("Referer", "http://spapi.pixiv.net/");
            httpGet.addHeader("User-Agent", "PixivIOSApp/5.8.7");

            Object token1 = redisUtils.get("token");
            if (StringUtils.isBlank(String.valueOf(token1)) || null == token1) {
                accountService.refreshToken();
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
//            String json = infoStream.toString("UTF-8");
//            JSONObject jsonObject = JSONObject.parseObject(json);
//            JSONArray jsonArray = JSONArray.parseArray(String.valueOf(jsonObject.get("response")));
//            jsonObject = JSONObject.parseObject(String.valueOf(jsonArray.get(0)));
//            jsonArray = JSONArray.parseArray(String.valueOf(jsonObject.get("works")));
//            List<Object> list = new ArrayList<Object>();
//            for (int i = 0; i < jsonArray.size(); i++) {
//                jsonObject = JSONObject.parseObject(String.valueOf(jsonArray.get(i)));
//                list.add(jsonObject.get("work"));
//            }
//            Map<String,Object> map = new LinkedHashMap<>();
//            map.put("message","获取成功");
//            map.put("data",infoStream.toString("UTF-8"));
            redisUtils.set(AppConstant.RANKING_REDIS + rankingType + mode + page + perPage + date, infoStream.toString("UTF-8"), 7 * 24 * 60 * 60L);
            return infoStream.toString("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return JSON.toJSONString(new Result(500, "请求失败", null, e.getMessage()));
        } finally {
            assert httpGet != null;
            httpGet.abort();
        }
    }

    @Override
    public String search(String word, String offset) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet();
        ByteArrayOutputStream infoStream = new ByteArrayOutputStream();
        StringBuilder url = null;
        Integer code = null;
        try {

            if (StringUtils.isBlank(word)) {
                return JSON.toJSONString(new Result(400, "搜索字为空", null, null));
            }
            if (StringUtils.isBlank(offset)) {
                return JSON.toJSONString(new Result(400, "offset为空", null, null));
            }

            if (redisUtils.hasKey(AppConstant.SEARCH_REDIS + word + offset)) {
                infoStream = (ByteArrayOutputStream) redisUtils.get(AppConstant.SEARCH_REDIS + word + offset);
            } else {
                url = new StringBuilder("https://app-api.pixiv.net/v1/search/illust");

                url.append("?word=").append(word);
                url.append("&search_target=").append("partial_match_for_tags");
                url.append("&sort=").append("date_desc");
                url.append("&filter=").append("for_ios");
//            url.append("&duration=").append("None");
                url.append("&offset=").append(offset);

                httpGet = httpUtils.get(String.valueOf(url));
                //httpGet.addHeader("host", "http://app-api.pixiv.net/");
                httpGet.addHeader("User-Agent", "PixivIOSApp/7.6.2 (iOS 12.2; iPhone9,1)");
                httpGet.addHeader("App-OS", "ios");
                httpGet.addHeader("App-OS-Version", "12.2");
                httpGet.addHeader("App-Version", "7.6.2");

                Object token1 = redisUtils.get("token");
                if (StringUtils.isBlank(String.valueOf(token1)) || token1 == null) {
                    accountService.refreshToken();
                    token1 = redisUtils.get("token");
                }
                PixivToken token = (PixivToken) token1;
                System.out.println(token);
                httpGet.addHeader("Authorization", "Bearer " + token.getAccessToken());


                //发送请求
                CloseableHttpResponse response = httpClient.execute(httpGet);
                code = response.getStatusLine().getStatusCode();
                InputStream in = response.getEntity().getContent();
                byte[] buffer = new byte[1];
                int len = 0;
                String data = "";
                while ((len = in.read(buffer)) > 0) {
                    infoStream.write(buffer, 0, len);
                }

                if (code == 200) {
                    redisUtils.set(AppConstant.SEARCH_REDIS + word + offset, infoStream);
                }
            }


            infoStream.close();
            httpClient.close();
            return infoStream.toString("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return JSON.toJSONString(new Result(500, "请求失败", null, e.getMessage()));
        } finally {
            assert httpGet != null;
            httpGet.abort();
        }
    }

    @Override
    public String tags() {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet();
        ByteArrayOutputStream infoStream = new ByteArrayOutputStream();
        StringBuilder url = null;
        Integer code = null;
        try {

            if (redisUtils.hasKey(AppConstant.TAGS_REDIS + AppConstant.SDF.format(new Date()))) {
                infoStream = (ByteArrayOutputStream) redisUtils.get(AppConstant.TAGS_REDIS + AppConstant.SDF.format(new Date()));            } else {
                url = new StringBuilder("https://app-api.pixiv.net/v1/trending-tags/illust");
                url.append("?filter=").append("for_ios");
                url.append("&date=").append(AppConstant.SDF.format(new Date()));

                httpGet = httpUtils.get(String.valueOf(url));
                //httpGet.addHeader("host", "http://app-api.pixiv.net/");
                httpGet.addHeader("User-Agent", "PixivIOSApp/7.6.2 (iOS 12.2; iPhone9,1)");
                httpGet.addHeader("App-OS", "ios");
                httpGet.addHeader("App-OS-Version", "12.2");
                httpGet.addHeader("App-Version", "7.6.2");
                httpGet.addHeader("Accept-Language", "zh-cn");

                Object token1 = redisUtils.get("token");
                if (StringUtils.isBlank(String.valueOf(token1)) || token1 == null) {
                    accountService.refreshToken();
                    token1 = redisUtils.get("token");
                }
                PixivToken token = (PixivToken) token1;
                httpGet.addHeader("Authorization", "Bearer " + token.getAccessToken());


                //发送请求
                CloseableHttpResponse response = httpClient.execute(httpGet);
                code = response.getStatusLine().getStatusCode();
                InputStream in = response.getEntity().getContent();
                byte[] buffer = new byte[1];
                int len = 0;
                String data = "";
                while ((len = in.read(buffer)) > 0) {
                    infoStream.write(buffer, 0, len);
                }
                if (code == 200) {
                    redisUtils.set(AppConstant.TAGS_REDIS + AppConstant.SDF.format(new Date()), infoStream, 24 * 60 * 60L);
                }
            }
            infoStream.close();
            httpClient.close();
            return infoStream.toString("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return JSON.toJSONString(new Result(500, "请求失败", null, e.getMessage()));
        } finally {
            assert httpGet != null;
            httpGet.abort();
        }
    }

    @Override
    public Result amazingPic(String token) {
        try {

            if (tokenMapper.selectCount(new QueryWrapper<Token>().eq("token",token)) < 1){
                return httpUtils.setBuild(res,new Result(400, "token不存在", null, null));
            }


            Date date = new Date();
            String time = AppConstant.SDF.format(date);



//            AmazingPic amazingPic = new AmazingPic();
            List<Illust> illusts = illustMapper.selectList(new QueryWrapper<Illust>().eq("x_restrict", 1).eq("type", "illust").gt("total_bookmarks", 3000).gt("total_view",10000).orderByAsc("id"));
//            amazingPic.setTotal(illustPage.getTotal());
//            amazingPic.setCurrent(illustPage.getCurrent());
//            amazingPic.setPages(illustPage.getPages());
//            amazingPic.setSize(illustPage.getSize());

            if(illusts.size() < 1){
                return httpUtils.setBuild(res,new Result(404, "获取失败", null, "库存为0"));
            }

            int num = (int) (Math.random() * (illusts.size() - 1) + 1);


            Illust illust = illusts.get(num - 1);

            //将数据添加到IllustDto
            IllustDto illustDto = new IllustDto();
            illustDto.setIllust(illust.getIllustId());
            illustDto.setType(illust.getType());
            illustDto.setCaption(illust.getCaption());
            illustDto.setTitle(illust.getTitle());
            illustDto.setRestrict(illust.getRestrictN());
            illustDto.setLarge(domin + illust.getLarge());
            illustDto.setCreateDate(illust.getCreateDate());
            illustDto.setPageCount(illust.getPageCount());
            illustDto.setWidth(illust.getWidth());
            illustDto.setHeight(illust.getHeight());
            illustDto.setSanityLevel(illust.getSanityLevel());
            illustDto.setxRestrict(illust.getxRestrict());
            illustDto.setSeries(illust.getSeries());
            illustDto.setTotalView(illust.getTotalView());
            illustDto.setTotalBookmarks(illust.getTotalBookmarks());
            illustDto.setIsBookmarked(illust.getIsBookmarked());
            illustDto.setVisible(illust.getVisible());
            illustDto.setIsMuted(illust.getIsMuted());
            illustDto.setTotalComments(illust.getTotalComments());

            //作者信息
            User user1 = userMapper.selectOne(new QueryWrapper<User>().eq("userid", illust.getUserId()));
            UserDto userDto = new UserDto();
            userDto.setId(user1.getUserId());
            userDto.setAccount(user1.getAccount());
            userDto.setName(user1.getName());
            userDto.setIsFollowed(user1.getIsFollowed());
            userDto.setProfileImageUrls(domin + user1.getProfileImageUrls());
            illustDto.setUser(userDto);

            //获取标签
            List<TagOfIllust> tagOfIllusts = tagOfIllustMapper.selectList(new QueryWrapper<TagOfIllust>().eq("illust", illust.getIllustId()));
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
            List<Original> originals = originalMapper.selectList(new QueryWrapper<Original>().eq("illustid", illust.getIllustId()));
            List<OriginalDto> originalDtos = new ArrayList<>();
            for (Original original : originals) {
                OriginalDto originalDto = new OriginalDto();
                originalDto.setUrl(domin + original.getUrl());

                originalDtos.add(originalDto);
            }
            illustDto.setOriginals(originalDtos);


//            amazingPic.setIllustDtos(illustDtos);


            return httpUtils.setBuild(res,new Result(201, "获取成功", illustDto, null));
        } catch (Exception e) {
            e.printStackTrace();
            return httpUtils.setBuild(res,new Result(500, "获取失败", null, "服务器内部错误"));
        }
    }

    @Override
    public String searchUser(String word,Integer offset) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet();
        ByteArrayOutputStream infoStream = new ByteArrayOutputStream();
        StringBuilder url = null;
        Integer code = null;
        try {

            if (redisUtils.hasKey(AppConstant.SEARCH_USER_REDIS + word + String.valueOf(offset))) {
                infoStream = (ByteArrayOutputStream) redisUtils.get(AppConstant.SEARCH_USER_REDIS + word + String.valueOf(offset));
            } else {
                url = new StringBuilder("https://app-api.pixiv.net/v1/search/user");
                url.append("?word=").append(word);
                url.append("&filter=").append("for_ios");
                url.append("&sort=").append("date_desc");
                url.append("&offset=").append(null == offset ?  30 : offset );

                httpGet = httpUtils.get(String.valueOf(url));
                //httpGet.addHeader("host", "http://app-api.pixiv.net/");
                httpGet.addHeader("User-Agent", "PixivIOSApp/7.6.2 (iOS 12.2; iPhone9,1)");
                httpGet.addHeader("App-OS", "ios");
                httpGet.addHeader("App-OS-Version", "12.2");
                httpGet.addHeader("App-Version", "7.6.2");
                httpGet.addHeader("Accept-Language", "zh-cn");

                Object token1 = redisUtils.get("token");
                if (StringUtils.isBlank(String.valueOf(token1)) || token1 == null) {
                    accountService.refreshToken();
                    token1 = redisUtils.get("token");
                }
                PixivToken token = (PixivToken) token1;
                httpGet.addHeader("Authorization", "Bearer " + token.getAccessToken());


                //发送请求
                CloseableHttpResponse response = httpClient.execute(httpGet);
                code = response.getStatusLine().getStatusCode();
                InputStream in = response.getEntity().getContent();
                byte[] buffer = new byte[1];
                int len = 0;
                String data = "";
                while ((len = in.read(buffer)) > 0) {
                    infoStream.write(buffer, 0, len);
                }
                if (code == 200) {
                    redisUtils.set(AppConstant.SEARCH_USER_REDIS + word + String.valueOf(offset), infoStream, 24 * 60 * 60L);
                }
            }
            infoStream.close();
            httpClient.close();
            res.setStatus(201);
            return infoStream.toString("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(500);
            return JSON.toJSONString(new Result(500, "请求失败", null, e.getMessage()));
        } finally {
            assert httpGet != null;
            httpGet.abort();
        }
    }

    @Override
    public String userDetails(String userId) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet();
        ByteArrayOutputStream infoStream = new ByteArrayOutputStream();
        StringBuilder url = null;
        Integer code = null;
        try {

            if (redisUtils.hasKey(AppConstant.SEARCH_USER_ID_DETAILS_REDIS + userId)) {
                infoStream = (ByteArrayOutputStream) redisUtils.get(AppConstant.SEARCH_USER_ID_DETAILS_REDIS + userId);
            } else {
                url = new StringBuilder("https://app-api.pixiv.net/v1/user/detail");
                url.append("?user_id=").append(userId);
                url.append("&filter=").append("for_ios");

                httpGet = httpUtils.get(String.valueOf(url));
                //httpGet.addHeader("host", "http://app-api.pixiv.net/");
                httpGet.addHeader("User-Agent", "PixivIOSApp/7.6.2 (iOS 12.2; iPhone9,1)");
                httpGet.addHeader("App-OS", "ios");
                httpGet.addHeader("App-OS-Version", "12.2");
                httpGet.addHeader("App-Version", "7.6.2");
                httpGet.addHeader("Accept-Language", "zh-cn");

                Object token1 = redisUtils.get("token");
                if (StringUtils.isBlank(String.valueOf(token1)) || token1 == null) {
                    accountService.refreshToken();
                    token1 = redisUtils.get("token");
                }
                PixivToken token = (PixivToken) token1;
                httpGet.addHeader("Authorization", "Bearer " + token.getAccessToken());


                //发送请求
                CloseableHttpResponse response = httpClient.execute(httpGet);
                code = response.getStatusLine().getStatusCode();
                InputStream in = response.getEntity().getContent();
                byte[] buffer = new byte[1];
                int len = 0;
                String data = "";
                while ((len = in.read(buffer)) > 0) {
                    infoStream.write(buffer, 0, len);
                }
                if (code == 200) {
                    redisUtils.set(AppConstant.SEARCH_USER_ID_DETAILS_REDIS + userId, infoStream, 24 * 60 * 60L);
                }
            }
            infoStream.close();
            httpClient.close();
            res.setStatus(201);
            return infoStream.toString("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(500);
            return JSON.toJSONString(new Result(500, "请求失败", null, e.getMessage()));
        } finally {
            assert httpGet != null;
            httpGet.abort();
        }
    }

    @Override
    public String userIllusts(String userId,Integer offset) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet();
        ByteArrayOutputStream infoStream = new ByteArrayOutputStream();
        StringBuilder url = null;
        Integer code = null;
        try {

            if (redisUtils.hasKey(AppConstant.SEARCH_USER_ID_ILLUSTS_REDIS + userId + String.valueOf(offset))) {
                infoStream = (ByteArrayOutputStream) redisUtils.get(AppConstant.SEARCH_USER_ID_ILLUSTS_REDIS + userId + String.valueOf(offset));
            } else {
                url = new StringBuilder("https://app-api.pixiv.net/v1/user/illusts");
                url.append("?user_id=").append(userId);
                url.append("&filter=").append("for_ios");
                url.append("&type=").append("illust");
                url.append("&offset=").append(null == offset ?  30 : offset );

                httpGet = httpUtils.get(String.valueOf(url));
                //httpGet.addHeader("host", "http://app-api.pixiv.net/");
                httpGet.addHeader("User-Agent", "PixivIOSApp/7.6.2 (iOS 12.2; iPhone9,1)");
                httpGet.addHeader("App-OS", "ios");
                httpGet.addHeader("App-OS-Version", "12.2");
                httpGet.addHeader("App-Version", "7.6.2");
                httpGet.addHeader("Accept-Language", "zh-cn");

                Object token1 = redisUtils.get("token");
                if (StringUtils.isBlank(String.valueOf(token1)) || token1 == null) {
                    accountService.refreshToken();
                    token1 = redisUtils.get("token");
                }
                PixivToken token = (PixivToken) token1;
                httpGet.addHeader("Authorization", "Bearer " + token.getAccessToken());


                //发送请求
                CloseableHttpResponse response = httpClient.execute(httpGet);
                code = response.getStatusLine().getStatusCode();
                InputStream in = response.getEntity().getContent();
                byte[] buffer = new byte[1];
                int len = 0;
                String data = "";
                while ((len = in.read(buffer)) > 0) {
                    infoStream.write(buffer, 0, len);
                }
                if (code == 200) {
                    redisUtils.set(AppConstant.SEARCH_USER_ID_ILLUSTS_REDIS + userId + String.valueOf(offset), infoStream, 24 * 60 * 60L);
                }
            }
            infoStream.close();
            httpClient.close();
            res.setStatus(201);
            return infoStream.toString("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(500);
            return JSON.toJSONString(new Result(500, "请求失败", null, e.getMessage()));
        } finally {
            assert httpGet != null;
            httpGet.abort();
        }
    }
}

