package com.demo.comunity.controller;

import com.demo.comunity.dto.QuestionDTO;
import com.demo.comunity.mapper.QuestionMapper;
import com.demo.comunity.model.Question;
import com.demo.comunity.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/question/{id}")
    public String question(
            Model model,
            HttpRequest request,
            @PathVariable(name = "id")Integer id
    ){
        QuestionDTO question = questionService.getById(id);
        if(question == null)
            return "redirect:/";

        model.addAttribute("question", question);
        return "question";
    }
}
