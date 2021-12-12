package com.leizhuang.controller;

import com.leizhuang.service.LoginService;
import com.leizhuang.vo.Result;
import com.leizhuang.vo.params.LoginParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author LeiZhuang
 * @date 2021/12/12 20:52
 */
@RestController
@RequestMapping("register")
public class RegisterController {
    @Autowired
    private LoginService loginService;

    @PostMapping
    public Result register(@RequestBody LoginParam loginParam) {
        //sso单点登陆
        return loginService.register(loginParam);
    }
}
