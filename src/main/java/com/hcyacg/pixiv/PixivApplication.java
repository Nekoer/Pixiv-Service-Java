package com.hcyacg.pixiv;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableAsync;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;


@EnableRabbit
@SpringBootApplication
@MapperScan("com.hcyacg.pixiv.mapper")
public class PixivApplication extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(PixivApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(PixivApplication.class, args);
    }

}
