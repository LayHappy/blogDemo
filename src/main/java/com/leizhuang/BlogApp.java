package com.leizhuang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @author LeiZhuang
 * @date 2021/12/11 15:36
 */
@SpringBootApplication/*(exclude = { DataSourceAutoConfiguration.class })*/
public class BlogApp {
    public static void main(String[] args) {
        SpringApplication.run(BlogApp.class,args);
    }
}
