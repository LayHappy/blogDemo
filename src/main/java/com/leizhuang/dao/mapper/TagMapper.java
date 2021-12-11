package com.leizhuang.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leizhuang.dao.pojo.Tag;

import java.util.List;

/**
 * @author LeiZhuang
 * @date 2021/12/11 16:10
 */

public interface TagMapper extends BaseMapper<Tag> {
    /**
     * 根据文章id查询标签列表
     * @param articleId
     * @return
     */
    List<Tag> findTagsByArticleId(Long articleId);

    /**
     *查询最热标签前n条
     * @param limit
     * @return
     */
    List<Long> findHotsTagIds(int limit);

    List<Tag> findTagsByTagIds(List<Long> tagIds);
}
