package com.leizhuang.dao.dos;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author LeiZhuang
 * @date 2021/12/12 11:09
 */
@Data
@NoArgsConstructor
public class Archives {
    private Integer year;
    private Integer month;
    private Long count;
}
