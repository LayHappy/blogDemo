package com.leizhuang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leizhuang.dao.dos.Archives;
import com.leizhuang.dao.mapper.ArticleBodyMapper;
import com.leizhuang.dao.mapper.ArticleMapper;
import com.leizhuang.dao.mapper.ArticleTagMapper;
import com.leizhuang.dao.pojo.Article;
import com.leizhuang.dao.pojo.ArticleBody;
import com.leizhuang.dao.pojo.ArticleTag;
import com.leizhuang.dao.pojo.SysUser;
import com.leizhuang.service.*;
import com.leizhuang.utils.UserThreadLocal;
import com.leizhuang.vo.ArticleBodyVo;
import com.leizhuang.vo.ArticleVo;
import com.leizhuang.vo.Result;
import com.leizhuang.vo.TagVo;
import com.leizhuang.vo.params.ArticleParam;
import com.leizhuang.vo.params.PageParams;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LeiZhuang
 * @date 2021/12/11 16:21
 */
@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;//

    @Autowired
    private TagService tagService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private ArticleTagMapper articleTagMapper;

    @Override
    public Result hotArticle(int limit) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getViewCounts);
        queryWrapper.select(Article::getId, Article::getTitle);
        queryWrapper.last("limit " + limit);
        //select id,titile from article order by view_count desc limit 5
        List<Article> articles = articleMapper.selectList(queryWrapper);
        return Result.success(copyList(articles, false, false));
    }

    @Override
    public Result newArticle(int limit) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getCreateDate);
        queryWrapper.select(Article::getId, Article::getTitle);
        queryWrapper.last("limit " + limit);
        List<Article> articles = articleMapper.selectList(queryWrapper);

        return Result.success(copyList(articles, false, false));
    }

    @Override
    public Result listArchives() {
        List<Archives> archivesList = articleMapper.listArchives();
        return Result.success(archivesList);
    }

    @Override
    public Result listArticle(PageParams pageParams) {
         Page<Article> page = new Page<>(pageParams.getPage(), pageParams.getPageSize());
         IPage<Article> articleIPage = articleMapper.listArticle(page,
                 pageParams.getCategoryId(),
                 pageParams.getTagId(),
                 pageParams.getYear(),
                 pageParams.getMonth());
         List<Article> records = articleIPage.getRecords();
         return Result.success(copyList(records,true,true));
     }
  /*  @Override
    public Result listArticle(PageParams pageParams) {
        *//**
         * ????????????article????????????
         *//*
        Page<Article> page = new Page<>(pageParams.getPage(), pageParams.getPageSize());
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        //????????????????????????
        if (pageParams.getCategoryId() != null) {
//    and category_id =#{categoryId}
            queryWrapper.eq(Article::getCategoryId, pageParams.getCategoryId());
        }
        List<Long> articleIdList=new ArrayList<>();
        if (pageParams.getTagId() != null) {
//    ??????????????????????????????Article???????????????????????????
            LambdaQueryWrapper<ArticleTag> articleTagLambdaQueryWrapper=new LambdaQueryWrapper<>();
            articleTagLambdaQueryWrapper.eq(ArticleTag::getTagId,pageParams.getTagId());
            List<ArticleTag> articleTags = articleTagMapper.selectList(articleTagLambdaQueryWrapper);
            for (ArticleTag articleTag : articleTags) {
                articleIdList.add(articleTag.getArticleId());
            }
            if (articleIdList.size()>0){
                //and id in (1,2,3,54,5);
                queryWrapper.in(Article::getId,articleIdList);
            }
        }
        // order by create_data desc
        queryWrapper.orderByDesc(Article::getWeight, Article::getCreateDate);
        Page<Article> articlePage = articleMapper.selectPage(page, queryWrapper);
        List<Article> records = articlePage.getRecords();
        List<ArticleVo> articleVoList = copyList(records, true, true);
        return Result.success(articleVoList);
    }*/

    private List<ArticleVo> copyList(List<Article> records, boolean isTag, boolean isAuthor) {
        List<ArticleVo> articleVoList = new ArrayList<>();
        for (Article record : records) {
            articleVoList.add(copy(record, isTag, isAuthor));
        }

        return articleVoList;
    }

    /*//    ??????
        private List<ArticleVo> copyList(List<Article> records, boolean isTag, boolean isAuthor,boolean isBody,boolean isCategory) {
            List<ArticleVo> articleVoList = new ArrayList<>();
            for (Article record : records) {
                articleVoList.add(copy(record, isTag, isAuthor,isBody,isCategory));
            }

            return articleVoList;
        }*/
    @Autowired
    private ThreadService threadService;

    @Override
    public Result findArticleById(Long articleId) {
        /**
         * 1.??????id??????????????????
         * 2.??????bodyId???categoryId??????????????????
         */

        Article article = this.articleMapper.selectById(articleId);

        ArticleVo articleVo = copy(article, true, true, true, true);

//        ???????????????????????????????????????
        //        Q:???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
//        ?????? ??????????????????????????????????????????????????????????????????????????????????????????

//        ??????????????????????????????????????????????????????????????????????????????????????????

        threadService.updateArticleViewCount(articleMapper, article);
        return Result.success(articleVo);
    }

    //??????????????????
    @Override
    public Result publish(ArticleParam articleParam) {

        SysUser sysUser = UserThreadLocal.get();
        /**
         * 1.???????????? ???????????????Article??????
         * 2.??????id????????????????????????
         * 3.??????  ???????????????????????????????????????
         * 4.body???????????????article bodyId
         */
        Article article = new Article();
        article.setAuthorId(sysUser.getId());
        article.setWeight(Article.Article_common);
        article.setViewCounts(0);
        article.setTitle(articleParam.getTitle());
        article.setSummary(articleParam.getSummary());
        article.setCommentCounts(0);
        article.setCreateDate(System.currentTimeMillis());
        article.setCategoryId(articleParam.getCategory().getId());
//        insert???????????????????????????id
        this.articleMapper.insert(article);
//        tag
        List<TagVo> tags = articleParam.getTags();
        if (tags != null) {
            for (TagVo tag : tags) {
                Long articleId = article.getId();
                ArticleTag articleTag = new ArticleTag();

                articleTag.setTagId(tag.getId());

                articleTag.setArticleId(articleId);
                articleTagMapper.insert(articleTag);

            }

        }
        //body
        ArticleBody articleBody = new ArticleBody();
        articleBody.setArticleId(article.getId());
        articleBody.setContent(articleParam.getBody().getContent());
        articleBody.setContentHtml(articleParam.getBody().getContentHtml());
        articleBodyMapper.insert(articleBody);
        article.setBodyId(articleBody.getId());

        articleMapper.updateById(article);

        Map<String, String> map = new HashMap<>();
        map.put("id", article.getId().toString());

        return Result.success(map);
    }

    private ArticleVo copy(Article article, boolean isTag, boolean isAuthor) {

        ArticleVo articleVo = new ArticleVo();
        BeanUtils.copyProperties(article, articleVo);
        articleVo.setCreateDate(new DateTime(article.getCreateDate()).toString("yyyy-MM-dd HH:mm"));
//        ?????????????????????????????????

        if (isTag) {
            Long articleId = article.getId();
            articleVo.setTags(tagService.findTagsByArticleId(articleId));
        }
        if (isAuthor) {
            Long authorId = article.getAuthorId();
            articleVo.setAuthor(sysUserService.findUserById(authorId).getNickname());
        }
        return articleVo;
    }

    @Autowired
    private CategoryService categoryService;

    //???????????????????????????????????????
    private ArticleVo copy(Article article, boolean isTag, boolean isAuthor, boolean idBody, boolean isCategory) {

        ArticleVo articleVo = new ArticleVo();
        BeanUtils.copyProperties(article, articleVo);
        articleVo.setCreateDate(new DateTime(article.getCreateDate()).toString("yyyy-MM-dd HH:mm"));
//        ?????????????????????????????????
        if (isTag) {
            Long articleId = article.getId();
            articleVo.setTags(tagService.findTagsByArticleId(articleId));
        }
        if (isAuthor) {
            Long authorId = article.getAuthorId();
            articleVo.setAuthor(sysUserService.findUserById(authorId).getNickname());
        }
        if (idBody) {
            Long bodyId = article.getBodyId();
            articleVo.setBody(findArticleBodyById(bodyId));
        }
        if (isCategory) {
            Long categoryId = article.getCategoryId();
            articleVo.setCategory(categoryService.findCategoryById(categoryId));
        }
        return articleVo;
    }

    @Autowired
    private ArticleBodyMapper articleBodyMapper;

    private ArticleBodyVo findArticleBodyById(Long bodyId) {
        ArticleBody articleBody = articleBodyMapper.selectById(bodyId);
        ArticleBodyVo articleBodyVo = new ArticleBodyVo();
        articleBodyVo.setContent(articleBody.getContent());

        return articleBodyVo;
    }
}

/*2021-12-12 19:40:19.029  WARN 28096 --- [           main]
        ConfigServletWebServerApplicationContext :
        Exception encountered during context initialization - cancelling refresh attempt:
        org.springframework.beans.factory.UnsatisfiedDependencyException:
        Error creating bean with name 'articleController':
        Unsatisfied dependency expressed through field 'articleService';
nested exception is org.springframework.beans.factory.UnsatisfiedDependencyException:
        Error creating bean with name 'articleServiceImpl':
        Unsatisfied dependency expressed through field 'articleMapper';
nested exception is org.springframework.beans.factory.BeanCreationException:
        Error creating bean with name 'articleMapper' defined in file [D:\JAVA\blog-Demo\target\classes\com\leizhuang\dao\mapper\ArticleMapper.class]:
        Invocation of init method failed; nested exception is java.lang.IllegalArgumentException:
        Property 'sqlSessionFactory' or 'sqlSessionTemplate'are required*/
