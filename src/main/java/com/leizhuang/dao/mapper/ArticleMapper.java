package com.leizhuang.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leizhuang.dao.dos.Archives;
import com.leizhuang.dao.pojo.Article;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ArticleMapper extends BaseMapper<Article> {

    List<Archives> listArchives();

}
