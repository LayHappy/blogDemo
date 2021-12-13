package com.leizhuang.handler;

import com.alibaba.fastjson.JSON;
import com.leizhuang.dao.pojo.SysUser;
import com.leizhuang.service.LoginService;
import com.leizhuang.utils.UserThreadLocal;
import com.leizhuang.vo.ErrorCode;
import com.leizhuang.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author LeiZhuang
 * @date 2021/12/12 22:02
 */
@Component//标明一个配置类
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private LoginService loginService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        在执行controller方法（Handler）之前进行执行，
        /**
         * 1.需要判断请求的接口路径是否为HandlerMethod(controller方法)
         * 2.判断token是否为空，如果为空，未登录
         * 3.如果token不为空，登陆验证，loginService checkToken
         * 4.如果认证成功，即可放行
         */
        if (!(handler instanceof HandlerMethod)) {
//            handler可能是RequestResourceHandler springboot 程序访问静态资源，默认去classpath下的static目录查询
            return true;
        }


        String token=request.getHeader("Authorization");
        log.info("======================request start=======================");
        String requestURI=request.getRequestURI();
        log.info("request uri:{}",requestURI);
        log.info("request method:{}",request.getMethod());
        log.info("token:{}",token);
        log.info("=====================request end====================");

        if (StringUtils.isBlank(token)) {
            Result result = Result.fail(ErrorCode.NO_LOGIN.getCode(), "未登录");
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(JSON.toJSONString(result));
            return false;
        }
        SysUser user = loginService.checkToken(token);
        if (user==null){
            Result result = Result.fail(ErrorCode.NO_LOGIN.getCode(), "未登录");
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(JSON.toJSONString(result));
            return false;
        }
        UserThreadLocal.put(user);
//        验证成功，放行
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        如果不删除ThreadLocal中用完的信息，会有内存泄漏的风险
        UserThreadLocal.remove();
    }
}

