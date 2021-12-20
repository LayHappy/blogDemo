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
         * 分页查询article数据库表
         *//*
        Page<Article> page = new Page<>(pageParams.getPage(), pageParams.getPageSize());
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        //是否置顶进行排序
        if (pageParams.getCategoryId() != null) {
//    and category_id =#{categoryId}
            queryWrapper.eq(Article::getCategoryId, pageParams.getCategoryId());
        }
        List<Long> articleIdList=new ArrayList<>();
        if (pageParams.getTagId() != null) {
//    加入标签的条件查询，Article一篇文章有多个标签
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

    /*//    重载
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
         * 1.根据id查询文章信息
         * 2.根据bodyId和categoryId去做关联查询
         */

        Article article = this.articleMapper.selectById(articleId);

        ArticleVo articleVo = copy(article, true, true, true, true);

//        查看完文章之后应新增阅读数
        //        Q:查看完文章之后，本应该直接返回数据了，这时候做了一个更新操作，更新时加写锁，就会阻塞其他的读操作，性能较低
//        更新 增加了此次接口的耗时，如果更新出问题，不能影响查看文章的操作

//        线程池，可以把更新操作放到线程池中执行，和主线程就不想相关了

        threadService.updateArticleViewCount(articleMapper, article);
        return Result.success(articleVo);
    }

    //文章发布服务
    @Override
    public Result publish(ArticleParam articleParam) {

        SysUser sysUser = UserThreadLocal.get();
        /**
         * 1.发布文章 目的：构建Article对象
         * 2.作者id，当前的登陆用户
         * 3.标签  要将标签加入到关联列表当中
         * 4.body内容存储，article bodyId
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
//        insert之后会生成一个文章id
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
//        判断是否需要标签和作者

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

    //这里使用了重载，参数不相同
    private ArticleVo copy(Article article, boolean isTag, boolean isAuthor, boolean idBody, boolean isCategory) {

        ArticleVo articleVo = new ArticleVo();
        BeanUtils.copyProperties(article, articleVo);
        articleVo.setCreateDate(new DateTime(article.getCreateDate()).toString("yyyy-MM-dd HH:mm"));
//        判断是否需要标签和作者
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
