package com.leizhuang.service.impl;

import com.alibaba.fastjson.JSON;
import com.leizhuang.dao.pojo.SysUser;
import com.leizhuang.service.LoginService;
import com.leizhuang.service.SysUserService;
import com.leizhuang.utils.JWTUtils;
import com.leizhuang.vo.ErrorCode;
import com.leizhuang.vo.Result;
import com.leizhuang.vo.params.LoginParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author LeiZhuang
 * @date 2021/12/12 14:41
 */
@Service

public class LoginServiceImpl implements LoginService {
    @Autowired
    @Lazy
    private SysUserService sysUserService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    private static final String slat = "leizhuang&%^@niubi666";

    @Override
    public Result login(LoginParam loginParam) {

        /**
         * 1.检查参数是否合法
         * 2.根据用户名和密码去user表中查询是否存在
         * 3.如果不存在，登陆失败
         * 4.如果存在，使用jwt生成token 返回给前端。
         * 5.token放入redis中，redis  token：user信息，设置过期时间(登陆认证的时候，先认证token字符串是否合法， 去redis认证是否存在)
         */

        String account = loginParam.getAccount();
        String password = loginParam.getPassword();

        if (StringUtils.isBlank(account) || StringUtils.isBlank(password)  ) {
            return Result.fail(ErrorCode.PARAMS_ERROR.getCode(), ErrorCode.PARAMS_ERROR.getMsg());
        }
        password= DigestUtils.md5DigestAsHex((password+slat).getBytes(StandardCharsets.UTF_8));
        SysUser sysUser = sysUserService.findUser(account, password);
        if (sysUser == null) {
            return Result.fail(ErrorCode.ACCOUNT_PWD_NOT_EXIST.getCode(), ErrorCode.ACCOUNT_PWD_NOT_EXIST.getMsg());
        }
        String token = JWTUtils.createToken(sysUser.getId());
        redisTemplate.opsForValue().set("TOKEN:" + token, JSON.toJSONString(sysUser),1, TimeUnit.DAYS);
        return Result.success(token);
    }

    @Override
    public SysUser checkToken(String token) {
        if (StringUtils.isBlank(token)){
            return null;
        }
        Map<String, Object> stringObjectMap = JWTUtils.checkToken(token);
        if (stringObjectMap == null) {
            return null;
        }
        String userJson = redisTemplate.opsForValue().get("TOKEN:" + token);
        if (StringUtils.isBlank(userJson)){
            return null;
        }
        SysUser sysUser=JSON.parseObject(userJson,SysUser.class);
        return sysUser;
    }

    @Override
    public Result logout(String token) {
        redisTemplate.delete("TOKEN:"+token);
        return Result.success(null);
    }

    @Override
    public Result register(LoginParam loginParam) {
        /**
         * 1.判断参数是否合法
         * 2.判断账户是否存在，存在 返回账户已经被注册
         * 3.账户不存在，注册用户
         * 4.生成token
         * 5.传入redis并返回
         * 6.注意加上事务，一旦中间的任何过程出现问题，注册的用户需要回滚。
         */
        String account = loginParam.getAccount();
        String password = loginParam.getPassword();
        String nickname=loginParam.getNickname();
        if (StringUtils.isBlank(account) || StringUtils.isBlank(password) || StringUtils.isBlank(nickname)) {
            return Result.fail(ErrorCode.PARAMS_ERROR.getCode(), ErrorCode.PARAMS_ERROR.getMsg());
        }
       SysUser sysUser= sysUserService.findUserByAccount(account);
        if (sysUser!=null){
            return Result.fail(ErrorCode.ACCOUNT_EXIST.getCode(),ErrorCode.ACCOUNT_EXIST.getMsg());
        }
        sysUser=new SysUser();
        sysUser.setNickname(nickname);
        sysUser.setAccount(account);
        sysUser.setPassword(org.apache.commons.codec.digest.DigestUtils.md5Hex(password+slat));//密码使用md5加密加盐算法
        sysUser.setCreateDate(System.currentTimeMillis());
        sysUser.setLastLogin(System.currentTimeMillis());
        sysUser.setAvatar("/static/img/logo.b3a48c0.png");
        sysUser.setAdmin(1);
        sysUser.setDeleted(0);
        sysUser.setSalt("");
        sysUser.setStatus("");
        sysUser.setEmail("");
        this.sysUserService.save(sysUser);

        String token = JWTUtils.createToken(sysUser.getId());

        redisTemplate.opsForValue().set("TOKEN:" + token, JSON.toJSONString(sysUser),1, TimeUnit.DAYS);

        return Result.success(token);
    }

    public static void main(String[] args) {
        String asdfa=DigestUtils.md5DigestAsHex(("admin"+slat).getBytes(StandardCharsets.UTF_8));
        System.out.println(asdfa);
    }
}
