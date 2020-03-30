package com.demo.community.controller;

import com.demo.community.dto.PaginationDTO;
import com.demo.community.dto.QuestionDTO;
import com.demo.community.exception.CustomizeErrorCode;
import com.demo.community.exception.CustomizeException;
import com.demo.community.service.QuestionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SearchController {

    @Autowired
    private QuestionService questionService;

    @RequestMapping("/search")
    public String search(
            @RequestParam(name="search", required = false) String searchContent,
            @RequestParam(name="page",defaultValue="1") Integer page,
            @RequestParam(name="size",defaultValue="5") Integer size,
            Model model
    ){
        if(StringUtils.isBlank(searchContent))
            throw new CustomizeException(CustomizeErrorCode.SEARCH_CONTENT_IS_EMPTY);

        PaginationDTO<QuestionDTO> pagination = questionService.list(searchContent, page, size);
        model.addAttribute("pagination", pagination);
        return "index";
    }
}
