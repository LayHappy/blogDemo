package com.leizhuang.service;

import com.leizhuang.vo.CategoryVo;
import com.leizhuang.vo.Result;

/**
 * @author LeiZhuang
 * @date 2021/12/13 9:43
 */

public interface CategoryService {
    /*
   查询类别
     */
    CategoryVo findCategoryById(Long categoryId);

    Result findAll();

    Result findAllDetail();

    Result categorysDetailById(Long id);

}
