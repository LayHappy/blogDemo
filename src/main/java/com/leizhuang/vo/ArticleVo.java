package com.leizhuang.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.leizhuang.dao.pojo.Article;
import lombok.Data;

import java.util.List;

/**
 * @author LeiZhuang
 * @date 2021/12/11 16:30
 */
@Data
public class ArticleVo {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String title;
    private String summary;
    private int commentCounts;
    private int viewCounts;
    private int weight;
    private String createDate;
    private String author;
    private ArticleBodyVo body;
    private CategoryVo category;
    private List<TagVo> tags;
}

