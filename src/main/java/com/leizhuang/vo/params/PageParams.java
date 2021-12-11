package com.leizhuang.vo.params;

import lombok.Data;

/**
 * @author LeiZhuang
 * @date 2021/12/11 16:13
 */
@Data
public class PageParams {
    private int page=1;
    private int pageSize=10;
}
