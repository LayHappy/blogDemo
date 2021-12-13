package com.leizhuang.vo;

import lombok.Data;

import java.util.List;

/**
 * @author LeiZhuang
 * @date 2021/12/13 13:32
 */
@Data
public class CommentVo {
    private Long id;
    private UserVo author;
    private String content;
    private List<CommentVo> children;
    private String createDate;
    private Integer level;
    private UserVo toUser;
}
