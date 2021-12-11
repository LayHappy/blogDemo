package com.leizhuang.controller;

import com.leizhuang.service.ArticleService;

import com.leizhuang.vo.Result;
import com.leizhuang.vo.params.PageParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author LeiZhuang
 * @date 2021/12/11 16:11
 */
@RestController//json数据交互
@RequestMapping("articles")
public class ArticleController {
    @Autowired
    private ArticleService articleService;
    /**
     * 首页文章列表
     *
     * @param pageParams
     * @return
     */
    @PostMapping
    public Result listArticle(@RequestBody PageParams pageParams) {
int i=10/0;
        return articleService.listArticle(pageParams);
    }


}
