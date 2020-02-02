package com.demo.comunity.controller;

import com.demo.comunity.dto.PaginationDTO;
import com.demo.comunity.mapper.UserMapper;
import com.demo.comunity.model.User;
import com.demo.comunity.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
public class IndexController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionService questionService;

    @GetMapping("/")
    public String index(HttpServletRequest request,
                        Model model,
                        @RequestParam(name="page",defaultValue="1") Integer page,
                        @RequestParam(name="size",defaultValue="5") Integer size
    ){
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for (Cookie cookie : cookies) {
                if(cookie.getName().equals("user_session")){
                    String token = cookie.getValue();
                    User user = userMapper.findByToken(token);
                    if(user != null){
                        request.getSession().setAttribute("user",user);
                    }
                    break;
                }
            }
        }
        //获取文章列表
        PaginationDTO pagination = questionService.select(page,size);
        model.addAttribute("pagination", pagination);
        return "index";
    }
}
