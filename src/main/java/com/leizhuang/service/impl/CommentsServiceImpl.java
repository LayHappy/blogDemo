package com.leizhuang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.leizhuang.dao.mapper.CommentMapper;
import com.leizhuang.dao.pojo.Comment;
import com.leizhuang.dao.pojo.SysUser;
import com.leizhuang.service.CommentsService;
import com.leizhuang.service.SysUserService;
import com.leizhuang.utils.UserThreadLocal;
import com.leizhuang.vo.CommentVo;
import com.leizhuang.vo.Result;
import com.leizhuang.vo.UserVo;
import com.leizhuang.vo.params.CommentParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LeiZhuang
 * @date 2021/10/28 20:13
 */
@Service
public class CommentsServiceImpl implements CommentsService {

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private SysUserService sysUserService;

    @Override
    public Result commentsByArticleId(Long id) {
        /**
         * 1.根据文章id查询评论列表，从comments 表中查询
         * 2.根据作者的id 查询作者的信息
         * 3.判断如果level=1 要去查询它有没有子评论，
         * 4.如果哟有 根据评论id 进行查询 (parent_id)
         */
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(Comment::getArticleId, id);

        queryWrapper.eq(Comment::getLevel, 1);

        List<Comment> comments = commentMapper.selectList(queryWrapper);

        List<CommentVo> commentVoList = copyList(comments);
        return Result.success(commentVoList);
    }

    private List<CommentVo> copyList(List<Comment> comments) {

        List<CommentVo> commentVoList = new ArrayList<>();
        for (Comment comment : comments) {
            commentVoList.add(copy(comment));
        }
        return commentVoList;
    }

    private CommentVo copy(Comment comment) {
        CommentVo commentVo = new CommentVo();
        BeanUtils.copyProperties(comment, commentVo);
//        作者信息
        Long authorId = comment.getArticleId();
        UserVo userVo = this.sysUserService.findUserVoById(authorId);
        commentVo.setAuthor(userVo);
//        子评论
        Integer level = comment.getLevel();
        if (1 == level) {
            Long id = comment.getId();
            List<CommentVo> commentVoList = findCommentsByParentId(id);
            commentVo.setChildren(commentVoList);
        }
//        to User 给谁评论
        if (level > 1) {
            Long toUid = comment.getToUid();
            UserVo toUserVo = this.sysUserService.findUserVoById(toUid);
            commentVo.setToUser(toUserVo);
        }
        return commentVo;
    }

    private List<CommentVo> findCommentsByParentId(Long id) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getParentId, id);
        queryWrapper.eq(Comment::getLevel, 2);
        return copyList(commentMapper.selectList(queryWrapper));
    }

    @Override
    public Result comment(CommentParam commentParam) {

        SysUser sysUser = UserThreadLocal.get();

        Comment comment = new Comment();

        comment.setArticleId(commentParam.getArticleId());
        System.out.println(sysUser.getId());
        comment.setAuthorId(sysUser.getId());

        comment.setContent(commentParam.getContent());

        comment.setCreateDate(System.currentTimeMillis());

        Long parent = commentParam.getParent();

        if (parent == null || parent == 0) {

            comment.setLevel(1);

        } else{
            comment.setLevel(2);
        }

        comment.setParentId(parent == null ? 0 : parent);

        Long toUserId = commentParam.getToUserId();

        comment.setToUid(toUserId == null ? 0 : toUserId);

        this.commentMapper.insert(comment);

        return Result.success(null);
    }
}
