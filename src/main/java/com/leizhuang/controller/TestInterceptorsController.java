package com.leizhuang.controller;

import com.leizhuang.dao.pojo.SysUser;
import com.leizhuang.utils.UserThreadLocal;
import com.leizhuang.vo.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author LeiZhuang
 * @date 2021/12/12 22:21
 */
@RestController
@RequestMapping("test")
public class TestInterceptorsController {
    @RequestMapping
    public Result test(){
//        SysUser sysUser= UserThreadLocal.get();
//        System.out.println(sysUser);
        return Result.success(null);
    }
}
