package com.leizhuang.controller;

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
        return Result.success(null);
    }
}
