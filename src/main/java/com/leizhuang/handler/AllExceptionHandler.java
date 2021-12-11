package com.leizhuang.handler;

import com.leizhuang.vo.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author LeiZhuang
 * @date 2021/12/11 21:52
 */
@ControllerAdvice//对加了@Controller注解的方法进行拦截处理(AOP的实现)
public class AllExceptionHandler {
//    进行异常处理，处理Exception.class的异常
    @ExceptionHandler(Exception.class)
    @ResponseBody//返回json数据
    public Result doException(Exception e){
        e.printStackTrace();
        return Result.fail(-9999,"异常处理拦截到了一个未知异常");
    }
}
