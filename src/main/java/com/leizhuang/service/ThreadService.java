package com.leizhuang.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.leizhuang.dao.mapper.ArticleMapper;
import com.leizhuang.dao.pojo.Article;
import com.leizhuang.dao.pojo.SysUser;
import com.leizhuang.utils.UserThreadLocal;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author LeiZhuang
 * @date 2021/12/13 10:55
 */
@Component
public class ThreadService {

//    希望此操作与在线程池执行，不会影响原有的主线程
    @Async("taskExecutor")
    public void updateArticleViewCount(ArticleMapper articleMapper, Article article) {
        int viewCounts = article.getViewCounts();
        Article articleUpdate=new Article();
        articleUpdate.setViewCounts(viewCounts+1);
        LambdaUpdateWrapper<Article> updateWrapper=new LambdaUpdateWrapper();
        updateWrapper.eq(Article::getId,article.getId());
//        设置一个 为了在多线程的环境下，线程安全
//        如果直接修改原来的article对象的viewCounts值（而不是new一个Article对象再赋值），再将修改后的article作为参数传入update()中，就不会有这个问题
//        摘自bilibili评论，
        updateWrapper.eq(Article::getViewCounts,viewCounts);
//        update article set view_count=100 where view_count=99 and id =?
        articleMapper.update(articleUpdate,updateWrapper);
      /*  try{
            Thread.sleep(3000);
            System.out.println("更新完成");
        }catch (Exception e){

        }*/
       /* SysUser sysUser = UserThreadLocal.get();
        System.out.println(sysUser.getId());*/
    }
}
