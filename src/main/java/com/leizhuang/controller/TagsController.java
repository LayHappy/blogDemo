package com.leizhuang.controller;

import com.leizhuang.service.TagService;
import com.leizhuang.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author LeiZhuang
 * @date 2021/12/11 21:22
 */
@RestController
@RequestMapping("tags")
public class TagsController {
    @Autowired
    private TagService tagService;

    @GetMapping("/hot")//   /tags/hot
    public Result hot(){
        int limit=6;
       return tagService.hots(limit);
    }

    @GetMapping()//
    public Result findAll(){

        return tagService.findAll();
    }
}
