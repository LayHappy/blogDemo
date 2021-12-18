package com.leizhuang.utils;

import com.leizhuang.dao.pojo.SysUser;

/**
 * @author LeiZhuang
 * @date 2021/12/13 8:59
 */
public class UserThreadLocal {
    private UserThreadLocal() {}
//    线程变量隔离

    private static final ThreadLocal<SysUser> LOCAL = new ThreadLocal<>();

    public static void put(SysUser sysUser) {
//        System.out.println(Thread.currentThread().getName()+"set方法");
        LOCAL.set(sysUser);
      /*  SysUser user = UserThreadLocal.get();
        System.out.println("get到的id值"+user.getId());*/
    }

    public static SysUser get() {
//        System.out.println(Thread.currentThread().getName()+"get方法");
        return LOCAL.get();
    }

    public static void remove() {
        LOCAL.remove();
    }
}
