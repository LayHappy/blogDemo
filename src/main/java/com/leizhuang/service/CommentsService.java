package com.leizhuang.service;

import com.leizhuang.vo.Result;
import com.leizhuang.vo.params.CommentParam;

/**
 * @author LeiZhuang
 * @date 2021/12/13 13:24
 */
public interface CommentsService {
    /**
     * 根据文章id 查询所有的评论列表
     * @param id
     * @return
     */
    Result commentsByArticleId(Long id);

    Result comment(CommentParam commentParam);
}
