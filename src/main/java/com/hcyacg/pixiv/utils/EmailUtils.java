package com.hcyacg.pixiv.utils;

import com.hcyacg.pixiv.constant.AppConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

/**
 * Created: 黄智文
 * Desc: 邮箱工具类
 * Date: 2020/3/15 16:21
 */
@Component
public class EmailUtils {
    @Autowired
    private  JavaMailSender javaMailSender;

    public void sendHtmlCodeMail(String from, String to, String subject, String text) throws Exception {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper message = new MimeMessageHelper (mimeMessage,true);
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
//        message.setText(text);
        message.setText("<html><body><div style=\"width: 550px;height: auto;border-radius: 5px;margin:0 auto;border:1px solid #ffb0b0;box-shadow: 0px 0px 20px #888888;position: relative;\">\n" +
                "    <div style=\"background-image: url(https://www.hcyacg.com/pic.jpg);width:550px;height: 250px;background-size: cover;background-repeat: no-repeat;border-radius: 5px 5px 0px 0px;\"></div>\n" +
                "    <div style=\"background-color:white;line-height:180%;padding:0 15px 12px;width:520px;margin:10px auto;color:#555555;font-family:'Century Gothic','Trebuchet MS','Hiragino Sans GB',微软雅黑,'Microsoft Yahei',Tahoma,Helvetica,Arial,'SimSun',sans-serif;font-size:12px;margin-bottom: 0px;\">\n" +
                "        <h2 style=\"border-bottom:1px solid #DDD;font-size:14px;font-weight:normal;padding:13px 0 10px 8px;\"><span style=\"color: #12ADDB;font-weight: bold;\">&gt; </span>验证码来了哟~</h2>\n" +
                "        <div style=\"padding:0 12px 0 12px;margin-top:18px\">\n" +
                "        <p>时间：<span style=\"border-bottom:1px dashed #ccc;\" t=\"5\" times=\" 20:42\">"+ AppConstant.SDF.format(new Date()) +"</span></p> \n" +
                "            <p>&nbsp;给您的验证码：</p>\n" +
                "            <p style=\"background-color: #f5f5f5;border: 0px solid #DDD;padding: 10px 15px;text-align: center;margin:18px 0\">"+text+"</p>\n" +
//                "            <p style=\"background-color: #f5f5f5;border: 0px solid #DDD;padding: 10px 15px;margin:18px 0\">IP：{ip}<br />邮箱：<a href=\"mailto:{mail}\">{mail}</a><br />状态：{status} [<a href='{manage}' target='_blank'>管理评论</a>]</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "    <a style=\"text-decoration: none;color: rgb(255, 255, 255);width: 40%;text-align: center;background-color: rgb(255, 114, 114);height: 40px;line-height: 40px;box-shadow: 3px 3px 3px rgba(0, 0, 0, 0.3);display: block;margin: auto;\" href=\"{permalink}\" target=\"_blank\">祝贺您获得了一枚验证码哦~</a>\n" +
                "    <div style=\"color:#8c8c8c;;font-family: 'Century Gothic','Trebuchet MS','Hiragino Sans GB',微软雅黑,'Microsoft Yahei',Tahoma,Helvetica,Arial,'SimSun',sans-serif;font-size: 10px;width: 100%;text-align: center;\">\n" +
                "        <p>©2017-2020 Copyright HCYACG.inc</p>\n" +
                "    </div>\n" +
                "</div></body></html>",true);
        javaMailSender.send(mimeMessage);
    }

    public void sendEmail(String from, String to, String subject, String text) throws MessagingException {
        SimpleMailMessage message = new SimpleMailMessage ();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        javaMailSender.send(message);
    }


}
