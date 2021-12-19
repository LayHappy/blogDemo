package com.leizhuang.aop;

import com.alibaba.fastjson.JSON;
import com.leizhuang.utils.HttpContextUtils;
import com.leizhuang.utils.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * @author LeiZhuang
 * @date 2021/12/19 16:13
 */
@Component
@Aspect//切面：定义了通知和切点的关系
@Slf4j
public class LogAspect {

    @Pointcut("@annotation(com.leizhuang.aop.LogAnnotation)")
    public void pt() {

    }

    //    环绕通知，可对方法前后增强
    @Around("pt()")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
//        开始时间
        long startTime = System.currentTimeMillis();
//        执行方法
        Object proceed = joinPoint.proceed();
        long time = System.currentTimeMillis()-startTime;
recordLog(joinPoint,time);
        return  proceed;
    }

    private void recordLog(ProceedingJoinPoint joinPoint, long time) {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LogAnnotation annotation = method.getAnnotation(LogAnnotation.class);
        log.info("================ log start =================");
        log.info("module:{}", annotation.module());
        log.info("operator:{}", annotation.operator());

//        请求的方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        log.info("request method:{}", className + "." + methodName + "()");

//                请求的参数
        Object[] args = joinPoint.getArgs();

        String params = JSON.toJSONString(args[0]);
        log.info("params:{}",params);

//        获取request 设置ip地址
       HttpServletRequest request= HttpContextUtils.getHttpServletRequest();
       log.info("ip:{}", IpUtils.getIpAddr(request));

        log.info("excute time:{}ms",time);
        log.info("================log end==============");
    }
}
