package com.leizhuang.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author LeiZhuang
 * @date 2021/12/11 15:46
 */
@Configuration
public class WebMVCConfig implements WebMvcConfigurer {

//    前后端分离的跨域配置
    @Override
    public void addCorsMappings(CorsRegistry registry) {

//        WebMvcConfigurer.super.addCorsMappings(registry);
        registry.addMapping("/**").allowedOrigins("http://localhost:8080");
    }
}
