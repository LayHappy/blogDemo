package com.leizhuang.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * @author LeiZhuang
 * @date 2021/12/13 11:01
 */
@Configuration
@EnableAsync//开启多线程
public class ThreadPoolConfig {
    @Bean("taskExecutor")
    public Executor asyncServiceExecutor(){
        ThreadPoolTaskExecutor executor=new ThreadPoolTaskExecutor();
//        设置核心线程数
        executor.setCorePoolSize(5);
//        设置最大线程数
        executor.setMaxPoolSize(20);
//        设置队列大小
        executor.setQueueCapacity(Integer.MAX_VALUE);
//        设置线程活跃时间
        executor.setKeepAliveSeconds(60);
//        设置默认线程名称
        executor.setThreadNamePrefix("已设置的默认线程名:");
//        等待所有任务结束后在关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
//        执行初始化
        executor.initialize();
        return executor;
    }
}
