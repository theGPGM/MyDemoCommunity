package com.demo.community.controller;

import com.demo.community.dto.QuestionDTO;
import com.demo.community.model.Question;
import com.demo.community.model.User;
import com.demo.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class PublishController {

    @Autowired
    QuestionService questionService;

    @GetMapping("/publish")
    public String publish(
    ){
        return "publish";
    }

    @PostMapping("/publish")
    public String doPublish(
            @RequestParam(name="title",required = false)String title,
            @RequestParam(name="description",required = false) String description,
            @RequestParam(name="tag",required = false)String tag,
            @RequestParam(name="id",required = false)Long id,
            HttpServletRequest request,
            Model model
    ){
        User user = (User) request.getSession().getAttribute("user");

        //处理文本缺失
        if(title == null || title == ""){
            model.addAttribute("error", "标题不能为空");
            return "publish";
        }
        if(description  == null || description == ""){
            model.addAttribute("error", "内容不能为空");
            return "publish";
        }
        if(tag == null || tag == ""){
            model.addAttribute("error", "标签不能为空");
            return "publish";
        }

        if(user == null){
            model.addAttribute("error", "用户未登录");
            return "publish";
        }

        Question question = new Question();
        question.setCreatorId(user.getId());
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setId(id);
        questionService.createOrUpdate(question);
        return "redirect:/";
    }

    @GetMapping("/publish/{id}")
    public String edit(
            @PathVariable(name = "id") Long id,
            Model model,
            HttpServletRequest request
    ){
        User user = (User) request.getSession().getAttribute("user");
        QuestionDTO question = questionService.getById(id);

        if(question == null)
            return "redirect:/";

        if(user != null){
            if(question.getCreatorId().equals(user.getId())){
                model.addAttribute("title", question.getTitle());
                model.addAttribute("description", question.getDescription());
                model.addAttribute("tag", question.getTag());
                model.addAttribute("id", question.getId());
                return "publish";
            }
            else
                return "redirect:/";
        }
        return "redirect:/";
    }

}
