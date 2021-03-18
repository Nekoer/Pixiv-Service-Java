package com.hcyacg.pixiv.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger2Config {

    /**
     * @Description: swagger2的配置文件，这里可以配置swagger2的一些基本的内容，比如扫描的包等等
     */
    @Bean
    public Docket createRestApi() {
        //指定扫描生成文档的接口
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select()
                .apis(RequestHandlerSelectors.basePackage("com.hcyacg.pixiv.controller"))
                .paths(PathSelectors.any()).build();
        //.globalOperationParameters(pars); //加载上面注释的参数
    }

    /**
     * @Description: 构建 api文档的信息
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                // 设置页面标题
                .title("使用swagger2构建Pixiv插画后端api接口文档")
                // 设置联系人
                .contact(new Contact("HCYACG", "http://www.hcyacg.com", "hcyacg@vip.qq.com"))
                // 描述
                .description("欢迎访问Pixiv插画接口文档，这里是描述信息")
                // 定义版本号
                .version("1.0").build();
    }

}