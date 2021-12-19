package com.leizhuang.config;

import com.leizhuang.handler.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author LeiZhuang
 * @date 2021/12/11 15:46
 */
@Configuration
public class WebMVCConfig implements WebMvcConfigurer {
    @Autowired
    private LoginInterceptor loginInterceptor;

    //    前后端分离的跨域配置
    @Override
    public void addCorsMappings(CorsRegistry registry) {

//        WebMvcConfigurer.super.addCorsMappings(registry);
        registry.addMapping("/**").allowedOrigins("http://localhost:8080");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        拦截test接口，后续实际遇到需要拦截的接口时，再配置真正的拦截接口
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/test")
                .addPathPatterns("/comments/create/change")
        .addPathPatterns("/articles/publish")
                /*.excludePathPatterns("/login")
                .excludePathPatterns("/register")*/;

    }
}
