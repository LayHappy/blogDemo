package com.leizhuang.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author LeiZhuang
 * @date 2021/12/11 16:14
 */
@Data
@AllArgsConstructor
public class Result {
    private boolean success;
    private int code;
    private String msg;
    private Object data;

    public static Result success(Object data) {
        return new Result(true, 200, "success", data);
    }

    public static Result fail(int code, String msg) {
        return new Result(false, code, msg, null);
    }
}
