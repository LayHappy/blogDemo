package com.leizhuang.vo.params;

import lombok.Data;

/**
 * @author LeiZhuang
 * @date 2021/12/12 14:40
 */
@Data
public class LoginParam {
    private String account;
    private String password;
    private String nickname;
}
