package com.leizhuang.dao.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author LeiZhuang
 * @date 2021/12/11 15:57
 */
@Data
public class Article {
    public static final int Article_TOP=0;//是否置顶
    public static final int Article_common=0;//
//    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;//文章id
    private String title;//文章标题
    private String summary;//文章摘要
    private int commentCounts;//评论数量
    private int viewCounts;//浏览量
    private Long authorId;//作者id
    private Long bodyId;//文章id
    private Long categoryId;//类别id
    private int weight=Article_common;//置顶
    private Long createDate;//创建时间
}
