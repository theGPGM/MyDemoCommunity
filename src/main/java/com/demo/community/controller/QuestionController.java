package com.demo.community.controller;

import com.demo.community.dto.CommentDTO;
import com.demo.community.dto.QuestionDTO;
import com.demo.community.enums.CommentTypeEnum;
import com.demo.community.service.CommentService;
import com.demo.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class QuestionController {

    private static final Integer VIEW_COOKIE_TIME = 10;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentService commentService;

    @GetMapping("/question/{id}")
    public String question(
            Model model,
            @PathVariable(name = "id") Long id,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        QuestionDTO questionDTO = questionService.getById(id);
        List<QuestionDTO> relatedTopics = questionService.selectRelatedTopic(questionDTO);
        List<CommentDTO> comments = commentService.listByTargetId(id, CommentTypeEnum.QUESTION);
        Cookie[] cookies = request.getCookies();
        Boolean flag = false;
        for (Cookie cookie : cookies) {
            if(cookie.getName().equals("viewTourist-"+id)){
                flag = true;
            }
        }
        if(!flag){

            //累加阅读数
            questionService.incView(id);
            Cookie viewTourist = new Cookie("viewTourist-" + id, "on");
            viewTourist.setMaxAge(VIEW_COOKIE_TIME);
            response.addCookie(viewTourist);
        }

        model.addAttribute("question", questionDTO);
        model.addAttribute("comments", comments);
        model.addAttribute("relatedTopic", relatedTopics);
        return "question";
    }
}
