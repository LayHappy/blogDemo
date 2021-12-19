package com.leizhuang.aop;

import java.lang.annotation.*;
//,ElementType.TYPE,
//             可放在类上
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogAnnotation {
    String module() default "";
    String operator() default  "";
}
