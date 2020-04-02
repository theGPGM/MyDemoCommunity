package com.demo.community.controller;

import com.demo.community.dto.PaginationDTO;
import com.demo.community.service.NotificationService;
import com.demo.community.service.QuestionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndexController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(name="page",defaultValue="1") Integer page,
                        @RequestParam(name="size",defaultValue="5") Integer size,
                        @RequestParam(name="search", required = false) String search
    ){
        //获取文章列表
        if(StringUtils.isBlank(search)) {
            PaginationDTO pagination = questionService.getIndexList(page, size);
            model.addAttribute("pagination", pagination);
            return "index";
        }else{
            PaginationDTO pagination = questionService.getSearchList(search, page, size);
            model.addAttribute("pagination", pagination);
            model.addAttribute("search", search);
            return "index";
        }
    }
}
