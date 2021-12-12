package com.leizhuang.vo;

import lombok.Data;

/**
 * @author LeiZhuang
 * @date 2021/12/12 16:21
 */
@Data
public class LoginUserVo {
    private Long id;
    private String account;
    private String nickname;
    private String avatar;
}
