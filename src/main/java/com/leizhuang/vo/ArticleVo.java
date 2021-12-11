package com.leizhuang.vo;

import com.leizhuang.dao.pojo.Article;
import lombok.Data;

import java.util.List;

/**
 * @author LeiZhuang
 * @date 2021/12/11 16:30
 */
@Data
public class ArticleVo {
    private Long id;
    private String title;
    private String summary;
    private int commentCounts;
    private int viewCounts;
    private int weight;
    private String createDate;
    private String author;


    private List<TagVo> tags;
}

