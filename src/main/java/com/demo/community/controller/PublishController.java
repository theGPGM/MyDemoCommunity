package com.demo.community.controller;

import com.demo.community.cache.TagCache;
import com.demo.community.dto.QuestionDTO;
import com.demo.community.exception.CustomizeErrorCode;
import com.demo.community.exception.CustomizeException;
import com.demo.community.model.Question;
import com.demo.community.model.User;
import com.demo.community.service.QuestionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.swing.text.html.HTML;

@Controller
public class PublishController {

    @Autowired
    QuestionService questionService;

    @GetMapping("/publish")
    public String publish(
            Model model
    ) {
        model.addAttribute("tags", TagCache.get());
        return "publish";
    }

    @PostMapping("/publish")
    public String doPublish(
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "tag", required = false) String tag,
            @RequestParam(name = "id", required = false) Long id,
            HttpServletRequest request,
            Model model
    ) {
        User user = (User) request.getSession().getAttribute("user");

        //处理文本缺失
        if (StringUtils.isBlank(title)) {
            throw new CustomizeException(CustomizeErrorCode.TITLE_IS_EMPTY);
        }
        if (StringUtils.isBlank(description)) {
            throw new CustomizeException(CustomizeErrorCode.CONTENT_IS_EMPTY);
        }
        if (StringUtils.isBlank(tag)) {
            throw new CustomizeException(CustomizeErrorCode.TAG_IS_EMPTY);
        }

        if (user == null) {
            throw new CustomizeException(CustomizeErrorCode.NO_LOGIN);
        }

        String invalid = TagCache.filterInvalid(tag);
        if(!StringUtils.isBlank(invalid)){
            model.addAttribute("error", "输入非法标签: "+invalid);
            return "publish";
        }

        Question question = new Question();
        question.setCreatorId(user.getId());
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setId(id);
        questionService.createOrUpdate(question);
        model.addAttribute("tags", TagCache.get());
        model.addAttribute("error", null);
        return "redirect:/";
    }

    @GetMapping("/publish/{id}")
    public String editOriginalPublish(
            @PathVariable(name = "id") Long questionId,
            Model model,
            HttpServletRequest request
    ) {
        User user = (User) request.getSession().getAttribute("user");
        QuestionDTO questionDTO = questionService.getById(questionId);

        if (questionDTO == null)
            return "redirect:/";

        if (user != null) {
            if (questionDTO.getCreatorId().equals(user.getId())) {
                model.addAttribute("title", questionDTO.getTitle());
                model.addAttribute("description", questionDTO.getDescription());
                model.addAttribute("tag", questionDTO.getTag());
                model.addAttribute("id", questionDTO.getId());
                model.addAttribute("tags", TagCache.get());
                return "publish";
            } else
                return "redirect:/";
        }
        return "redirect:/";
    }

}
