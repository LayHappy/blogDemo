package com.leizhuang.controller;

import com.leizhuang.service.CategoryService;
import com.leizhuang.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author LeiZhuang
 * @date 2021/12/19 15:19
 */
@RestController
@RequestMapping("categorys")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    @GetMapping
    public Result categorys(){
        return categoryService.findAll();
    }

    @GetMapping("detail")
    public Result categoriesDetail(){
        return categoryService.findAllDetail();
    }
    @GetMapping("detail/{id}")
    public Result categorysDetailById(@PathVariable("id")Long id){
        return categoryService.categorysDetailById(id);
    }
}
