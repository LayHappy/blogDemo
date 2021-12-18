package com.leizhuang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.leizhuang.dao.mapper.SysUserMapper;
import com.leizhuang.dao.pojo.SysUser;
import com.leizhuang.service.LoginService;
import com.leizhuang.service.SysUserService;
import com.leizhuang.vo.ErrorCode;
import com.leizhuang.vo.LoginUserVo;
import com.leizhuang.vo.Result;
import com.leizhuang.vo.UserVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author LeiZhuang
 * @date 2021/12/11 17:14
 */
@Service
public class SysUserServiceImpl implements SysUserService {
    @Autowired
    private SysUserMapper sysUserMapper;
    /*@Autowired
    private RedisTemplate<String,String> redisTemplate;*/
    @Autowired
    @Lazy
    private LoginService loginService;

    @Override
    public SysUser findUserById(Long id) {
        SysUser sysUser = sysUserMapper.selectById(id);
        if (sysUser == null) {
            sysUser = new SysUser();
            sysUser.setNickname("leizhuang:BlogUser");
        }
        return sysUser;
    }
    @Override
    public UserVo findUserVoById(Long id) {
        SysUser sysUser = sysUserMapper.selectById(id);

        if (sysUser == null) {
            sysUser = new SysUser();
            sysUser.setId(1L);
            sysUser.setAvatar("/static/img/logo.b3a48c0.png");
            sysUser.setNickname("默认Nickname");
        }
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(sysUser, userVo);
        return userVo;
    }

    @Override
    public SysUser findUserByAccount(String account) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getAccount, account);
        queryWrapper.last("limit 1");
      /*  this.sysUserMapper.selectOne(queryWrapper);
        return null;*/

        return  this.sysUserMapper.selectOne(queryWrapper);
    }

    @Override
    public void save(SysUser sysUser) {
//        这里用户id会自动生成，这个地方默认生成的id是分布式id，采用雪花算法
        this.sysUserMapper.insert(sysUser);
    }

    @Override
    public SysUser findUser(String account, String password) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getAccount, account);
        queryWrapper.eq(SysUser::getPassword, password);
        queryWrapper.select(SysUser::getAccount, SysUser::getId, SysUser::getNickname);

/**
 * ==>  Preparing: SELECT account,id,nickname FROM ms_sys_user WHERE (account = ? AND password = ?) limit 1
 * ==> Parameters: admin(String), 0360ab97db54d09e12fd9bf7a163fb8f(String)
 * <==    Columns: account, id, nickname
 * <==        Row: admin, 1, 李四
 * <==      Total: 1
 * 这个注释是要注意：给用户评论是需要判断是否登陆的，判断用户登陆就需要对比数据库的信息，
 * 因为findUser()方法查错了信息，导致，JWTUtils创建了一个错误的token，redis也存了一个错误的TOKEN
 * 导致最终ThreadLocal存错信息，get出来的id为null
 */
//        queryWrapper.select(SysUser::getAccount, SysUser::getAvatar, SysUser::getNickname);
        queryWrapper.last("limit 1");
        return sysUserMapper.selectOne(queryWrapper);
    }

    @Override
    public Result findUserByToken(String token) {
        /**
         * 1.token合法性校验
         *         是否为空，解析是否成功 redis是否存在
         * 2.如果校验失败，返回错误
         * 3.如果成功，返回对应的结果 LoginUserVo
         */

        SysUser sysUser = loginService.checkToken(token);
        if (sysUser == null) {
            Result.fail(ErrorCode.TOKEN_ERROR.getCode(), ErrorCode.TOKEN_ERROR.getMsg());
        }
        LoginUserVo loginUserVo = new LoginUserVo();
        loginUserVo.setId(sysUser.getId());
        loginUserVo.setNickname(sysUser.getNickname());
        loginUserVo.setAvatar(sysUser.getAvatar());
        loginUserVo.setAccount(sysUser.getAccount());
        return Result.success(loginUserVo);
    }
}
