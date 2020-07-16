package com.hcyacg.pixiv.controller;

import com.hcyacg.pixiv.dto.Result;
import com.hcyacg.pixiv.service.IllustService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Created: 黄智文
 * Desc:
 * Date: 2020/5/12 17:34
 */
@RestController
@RequestMapping("illusts")
public class IllustController {

    @Autowired
    private IllustService illustService;
    @Autowired
    private HttpServletResponse res;

    /**
     * @api {GET} /illust/detail 详细数据
     * @apiVersion 1.0.0
     * @apiGroup 插画
     * @apiName 详细数据
     * @apiDescription 插画详细数据
     * @apiParam (请求参数) {String} illustId 插画id
     * @apiParamExample 请求参数示例
     * illustId=59580629
     * @apiSuccess (响应结果) {String} response json数据
     * @apiSuccessExample 响应结果示例
     * "{"illust":{"id":59580629,"title":"\u6d77\u6cbf\u3044\u306e\u8857","type":"illust","image_urls":{"square_medium":"https:\/\/i.pximg.net\/c\/540x540_10_webp\/img-master\/img\/2016\/10\/22\/10\/11\/37\/59580629_p0_square1200.jpg","medium":"https:\/\/i.pximg.net\/c\/540x540_70\/img-master\/img\/2016\/10\/22\/10\/11\/37\/59580629_p0_master1200.jpg","large":"https:\/\/i.pximg.net\/c\/600x1200_90_webp\/img-master\/img\/2016\/10\/22\/10\/11\/37\/59580629_p0_master1200.jpg"},"caption":"","restrict":0,"user":{"id":275527,"name":"\u7d50\u57ce\u30ea\u30ab","account":"issadaikiti","profile_image_urls":{"medium":"https:\/\/i.pximg.net\/user-profile\/img\/2019\/08\/22\/16\/30\/44\/16179102_d068220343b92783e323ac474efb1046_170.jpg"},"is_followed":false},"tags":[{"name":"\u30aa\u30ea\u30b8\u30ca\u30eb","translated_name":null},{"name":"\u91d1\u9aea\u30ed\u30f3\u30b0","translated_name":null},{"name":"\u65e5\u5098","translated_name":null},{"name":"\u30aa\u30ea\u30b8\u30ca\u30eb10000users\u5165\u308a","translated_name":null},{"name":"\u30af\u30e9\u30b7\u30ab\u30eb","translated_name":null}],"tools":[],"create_date":"2016-10-22T10:11:37+09:00","page_count":1,"width":1200,"height":854,"sanity_level":2,"x_restrict":0,"series":null,"meta_single_page":{"original_image_url":"https:\/\/i.pximg.net\/img-original\/img\/2016\/10\/22\/10\/11\/37\/59580629_p0.jpg"},"meta_pages":[],"total_view":65511,"total_bookmarks":12868,"is_bookmarked":false,"visible":true,"is_muted":false,"total_comments":33}}"
     */
    @RequestMapping(value = "detail", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result detail(@RequestParam(value = "illustId") String illustId,@RequestHeader(value = "authorization",required = false) String authorization) throws ExecutionException, InterruptedException {
        return illustService.detail(illustId,authorization);
    }

    @RequestMapping(value = "urlLook", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public void urlLook(@RequestParam(value = "url") String url, @RequestParam(value = "cache",required = false) Boolean cache) throws IOException {
        res.getOutputStream().write(illustService.urlLook(url,cache).toByteArray());
        res.getOutputStream().flush();
    }

    /**
     * @api {GET} /illust/comments 评论
     * @apiVersion 1.0.0
     * @apiGroup 插画
     * @apiName 评论
     * @apiDescription 插画评论
     * @apiParam (请求参数) {String} illustId 插画id
     * @apiParamExample 请求参数示例
     * illustId=59580629
     * @apiSuccess (响应结果) {String} response
     * @apiSuccessExample 响应结果示例
     * "{"total_comments":0,"comments":[{"id":69773809,"comment":"\u559c\u6b22\u8fd9\u4e2a\u98ce\u666f (shine4)","date":"2017-07-23T02:59:22+09:00","user":{"id":6835395,"name":"\u843d\u843d\u841d\u6d1b","account":"zhebingfeixinsheng","profile_image_urls":{"medium":"https:\/\/s.pximg.net\/common\/images\/no_profile.png"}},"parent_comment":{}},{"id":69773806,"comment":"(smile3)","date":"2017-07-23T02:59:11+09:00","user":{"id":6835395,"name":"\u843d\u843d\u841d\u6d1b","account":"zhebingfeixinsheng","profile_image_urls":{"medium":"https:\/\/s.pximg.net\/common\/images\/no_profile.png"}},"parent_comment":{}},{"id":65846908,"comment":"How   beautiful\uff01","date":"2017-03-16T15:52:32+09:00","user":{"id":20034065,"name":"\u9189\u68a6\u753b\u4e2d\u4ed9","account":"blood_night_angel","profile_image_urls":{"medium":"https:\/\/i.pximg.net\/user-profile\/img\/2016\/09\/04\/16\/21\/39\/11451616_09a682b7570527e0b0780d6531c5afce_170.jpg"}},"parent_comment":{}},{"id":65420766,"comment":"(love3) (love2) (interesting2)","date":"2017-03-01T22:53:16+09:00","user":{"id":23101946,"name":"HAKUREI","account":"skygreene","profile_image_urls":{"medium":"https:\/\/i.pximg.net\/user-profile\/img\/2017\/02\/26\/11\/31\/00\/12198790_c22698f06f73b830fc5ccf4b4cdb799d_170.jpg"}},"parent_comment":{}},{"id":64306805,"comment":"(heart)","date":"2017-01-24T00:30:29+09:00","user":{"id":6315622,"name":"\u661f\u6cb3\u87a2@\u751f\u547d\u30a8\u30f3\u30b8\u30cb\u30a2","account":"quantum-vita_akademeia","profile_image_urls":{"medium":"https:\/\/i.pximg.net\/user-profile\/img\/2013\/04\/27\/00\/27\/18\/6153747_b46f81c61d1f639e52412f23c918c357_170.jpg"}},"parent_comment":{}},{"id":62097151,"comment":"\u8d5e","date":"2016-11-08T19:13:14+09:00","user":{"id":7083999,"name":"\u9396\u661f","account":"asdqwella","profile_image_urls":{"medium":"https:\/\/s.pximg.net\/common\/images\/no_profile.png"}},"parent_comment":{}},{"id":62003383,"comment":"cool\uff01","date":"2016-11-05T08:50:40+09:00","user":{"id":19242206,"name":"liar0961","account":"liar0961","profile_image_urls":{"medium":"https:\/\/s.pximg.net\/common\/images\/no_profile.png"}},"parent_comment":{}},{"id":61706743,"comment":"\u53d1\u8868\u8bc4\u8bba...","date":"2016-10-25T00:08:41+09:00","user":{"id":8283809,"name":"\u91cd\u529b\u52a0\u901f\u5ea6","account":"p-u-ck","profile_image_urls":{"medium":"https:\/\/i.pximg.net\/user-profile\/img\/2013\/09\/07\/23\/30\/47\/6785617_062cb8018421e0f403bd667e0f948a9e_170.jpg"}},"parent_comment":{}},{"id":61649991,"comment":"\u50d5\u306f\u3053\u306e\u30b9\u30bf\u30a4\u30eb\u304c\u597d\u304d","date":"2016-10-23T03:57:33+09:00","user":{"id":9620554,"name":"K.K.B","account":"kingkongb","profile_image_urls":{"medium":"https:\/\/i.pximg.net\/user-profile\/img\/2017\/06\/03\/16\/23\/33\/12645678_6e09941a9f9d0bd8a91e6f6730fdcbe9_170.jpg"}},"parent_comment":{}},{"id":61642071,"comment":"\u304d\u308c\u3044\u2026\u2026\uff01","date":"2016-10-22T22:40:02+09:00","user":{"id":18091509,"name":"asita3110","account":"asita3110","profile_image_urls":{"medium":"https:\/\/i.pximg.net\/user-profile\/img\/2018\/02\/14\/07\/51\/35\/13824598_e42cba3ebed0b660bfe228134a96e80a_170.png"}},"parent_comment":{}},{"id":61630621,"comment":"\u3042\u3063\u305f\u304b\u3044\u5149\u304c\u3068\u3066\u3082\u3044\u3044\u3067\u3059\u306d\u3002\u304a\u5b22\u69d8\u306e\u8857\uff01","date":"2016-10-22T14:55:09+09:00","user":{"id":888058,"name":"\u30bc\u30c3\u30c0\u30eb","account":"zeddaru","profile_image_urls":{"medium":"https:\/\/s.pximg.net\/common\/images\/no_profile.png"}},"parent_comment":{}}],"next_url":null}"
     */
    @RequestMapping(value = "comments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String comments(@RequestParam(value = "illustId") String illustId){
        return illustService.comments(illustId);
    }




    /**
     * @api {GET} /illust/ranking 排行榜
     * @apiVersion 1.0.0
     * @apiGroup 插画
     * @apiName ranking
     * @apiDescription 排行榜
     * @apiParam (请求参数) {String} mode day, week, month, day_male, day_female, week_original, week_rookie, day_manga
     * @apiParam (请求参数) {String} date 2020-05-13
     * @apiParam (请求参数) {String} offset (n-1)*30
     * @apiParamExample 请求参数示例
     * mode=day&date=2020-05-13
     * @apiSuccess (响应结果) {String} response
     * @apiSuccessExample 响应结果示例
     * "{"illusts":[{"id":76171798,"title":"\u30c0\u30d6\u30eb\u30a8\u30c3\u30af\u30b9","type":"illust","image_urls":{"square_medium":"https:\/\/i.pximg.net\/c\/540x540_10_webp\/img-master\/img\/2019\/08\/10\/00\/03\/46\/76171798_p0_square1200.jpg","medium":"https:\/\/i.pximg.net\/c\/540x540_70\/img-master\/img\/2019\/08\/10\/00\/03\/46\/76171798_p0_master1200.jpg","large":"https:\/\/i.pximg.net\/c\/600x1200_90_webp\/img-master\/img\/2019\/08\/10\/00\/03\/46\/76171798_p0_master1200.jpg"},"caption":"","restrict":0,"user":{"id":853087,"name":"YD","account":"ydh2101","profile_image_urls":{"medium":"https:\/\/i.pximg.net\/user-profile\/img\/2020\/01\/16\/14\/48\/43\/16861065_79c5ff0ba43761888a5fdcda6624f179_170.png"},"is_followed":false},"tags":[{"name":"Fate\/GrandOrder","translated_name":null},{"name":"FGO","translated_name":null},{"name":"\u8b0e\u306e\u30d2\u30ed\u30a4\u30f3XX","translated_name":null},{"name":"\u8b0e\u306e\u30d2\u30ed\u30a4\u30f3X","translated_name":null},{"name":"\u30e1\u30ab\u30cb\u30c3\u30af","translated_name":null}],"tools":["Photoshop"],"create_date":"2019-08-10T00:01:08+09:00","page_count":3,"width":2079,"height":3409,"sanity_level":2,"x_restrict":0,"series":null,"meta_single_page":{},"meta_pages":[{"image_urls":{"square_medium":"https:\/\/i.pximg.net\/c\/360x360_10_webp\/img-master\/img\/2019\/08\/10\/00\/03\/46\/76171798_p0_square1200.jpg","medium":"https:\/\/i.pximg.net\/c\/540x540_70\/img-master\/img\/2019\/08\/10\/00\/03\/46\/76171798_p0_master1200.jpg","large":"https:\/\/i.pximg.net\/c\/600x1200_90_webp\/img-master\/img\/2019\/08\/10\/00\/03\/46\/76171798_p0_master1200.jpg","original":"https:\/\/i.pximg.net\/img-original\/img\/2019\/08\/10\/00\/03\/46\/76171798_p0.jpg"}},{"image_urls":{"square_medium":"https:\/\/i.pximg.net\/c\/360x360_10_webp\/img-master\/img\/2019\/08\/10\/00\/03\/46\/76171798_p1_square1200.jpg","medium":"https:\/\/i.pximg.net\/c\/540x540_70\/img-master\/img\/2019\/08\/10\/00\/03\/46\/76171798_p1_master1200.jpg","large":"https:\/\/i.pximg.net\/c\/600x1200_90_webp\/img-master\/img\/2019\/08\/10\/00\/03\/46\/76171798_p1_master1200.jpg","original":"https:\/\/i.pximg.net\/img-original\/img\/2019\/08\/10\/00\/03\/46\/76171798_p1.jpg"}},{"image_urls":{"square_medium":"https:\/\/i.pximg.net\/c\/360x360_10_webp\/img-master\/img\/2019\/08\/10\/00\/03\/46\/76171798_p2_square1200.jpg","medium":"https:\/\/i.pximg.net\/c\/540x540_70\/img-master\/img\/2019\/08\/10\/00\/03\/46\/76171798_p2_master1200.jpg","large":"https:\/\/i.pximg.net\/c\/600x1200_90_webp\/img-master\/img\/2019\/08\/10\/00\/03\/46\/76171798_p2_master1200.jpg","original":"https:\/\/i.pximg.net\/img-original\/img\/2019\/08\/10\/00\/03\/46\/76171798_p2.jpg"}}],"total_view":288494,"total_bookmarks":26180,"is_bookmarked":false,"visible":true,"is_muted":false}],"next_url":"https:\/\/app-api.pixiv.net\/v1\/illust\/ranking?mode=day\u0026date=2019-08-11\u0026filter=for_ios\u0026offset=40"}"
     */
    @RequestMapping(value = "ranking", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String ranking(@RequestParam(value = "mode",required = false) String mode,@RequestParam(value = "date",required = false) String date,@RequestParam(value = "offset",required = false)  String offset){
        return illustService.ranking(mode,date,offset);
    }

    @RequestMapping(value = "picNumbers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getPicNumber(){
        return illustService.getPicNumber();
    }
}
