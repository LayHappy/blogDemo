package com.leizhuang.dao.pojo;

import lombok.Data;

/**
 * @author LeiZhuang
 * @date 2021/12/13 13:20
 */
@Data
public class Comment {
    private Long id;
    private String content;
    private Long createDate;
    private Long articleId;
    private Long authorId;
    private Long parentId;
    private Long toUid;
    private Integer level;
}
