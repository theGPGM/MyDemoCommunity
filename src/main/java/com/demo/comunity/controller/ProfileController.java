package com.demo.comunity.controller;

import com.demo.comunity.dto.PaginationDTO;
import com.demo.comunity.model.User;
import com.demo.comunity.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ProfileController {

    @Autowired
    QuestionService questionService;

    @GetMapping("/profile/{action}")
    public String profile(
            Model model,
            @PathVariable(name="action") String action,
            HttpServletRequest request,
            @RequestParam(name="page",defaultValue="1") Integer page,
            @RequestParam(name="size",defaultValue="5") Integer size
    ){
        User user = (User) request.getSession().getAttribute("user");
        if(user == null)
            return "redirect:/";
        switch(action){
            case "questions":
                model.addAttribute("section", "questions");
                model.addAttribute("sectionName", "我的问题");
                break;
            case "replies":
                model.addAttribute("section", "replies");
                model.addAttribute("sectionName", "最新回复");
                break;
            default:
                return "redirect:/";
        }

        PaginationDTO paginationDTO = questionService.select(user.getId(), page, size);
        model.addAttribute("pagination", paginationDTO);
        return "profile";
    }
}
