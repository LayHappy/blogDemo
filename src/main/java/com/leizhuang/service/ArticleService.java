package com.leizhuang.service;

import com.leizhuang.vo.Result;
import com.leizhuang.vo.params.PageParams;

public interface ArticleService {
    /**
     * 分页查询文章列表
     * @param pageParams
     * @return
     */
    Result listArticle(PageParams pageParams);

}
