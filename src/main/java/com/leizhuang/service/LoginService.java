package com.leizhuang.service;

import com.leizhuang.dao.pojo.SysUser;
import com.leizhuang.vo.Result;
import com.leizhuang.vo.params.LoginParam;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface LoginService {
    Result login(LoginParam loginParam);

    SysUser checkToken(String token);

    Result logout(String token);

    Result register(LoginParam loginParam);
}
