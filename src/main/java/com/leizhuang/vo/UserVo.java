package com.leizhuang.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * @author LeiZhuang
 * @date 2021/12/13 13:33
 */
@Data
public class UserVo {
    private String nickname;
    private String avatar;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

}
