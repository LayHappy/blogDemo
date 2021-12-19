package com.leizhuang.controller;

import com.leizhuang.aop.LogAnnotation;
import com.leizhuang.service.ArticleService;

import com.leizhuang.vo.Result;
import com.leizhuang.vo.params.ArticleParam;
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
    private ArticleService articleService;//

    /**
     * 首页文章列表
     *
     * @param pageParams
     * @return
     */
    @PostMapping
    @LogAnnotation(module = "文章",operator = "获取文章列表")
    public Result listArticle(@RequestBody PageParams pageParams) {

        return articleService.listArticle(pageParams);
    }

    @PostMapping("hot")
    public Result hotArticle() {
        int limit = 5;
        return articleService.hotArticle(limit);
    }

    @PostMapping("new")
    public Result newArticle() {
        int limit = 5;
        return articleService.newArticle(limit);
    }

    @PostMapping("listArchives")
    public Result listArchives() {

        return articleService.listArchives();
    }

    @PostMapping("/view/{id}")
    public Result findArticleById(@PathVariable("id") Long articleId) {
        return articleService.findArticleById(articleId);
    }

    @PostMapping("publish")
    public Result publish(@RequestBody ArticleParam articleParam) {
        return articleService.publish(articleParam);
    }
}
