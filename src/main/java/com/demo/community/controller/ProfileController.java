package com.demo.community.controller;

import com.demo.community.dto.NotificationDTO;
import com.demo.community.dto.PaginationDTO;
import com.demo.community.dto.QuestionDTO;
import com.demo.community.exception.CustomizeErrorCode;
import com.demo.community.exception.CustomizeException;
import com.demo.community.exception.ICustomizeErrorCode;
import com.demo.community.model.User;
import com.demo.community.service.NotificationService;
import com.demo.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class ProfileController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/profile/{action}")
    public String profile(
            Model model,
            @PathVariable(name = "action") String action,
            HttpServletRequest request,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "5") Integer size
    ) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null)
            throw new CustomizeException(CustomizeErrorCode.NO_LOGIN);
        switch (action) {
            case "questions":
                model.addAttribute("section", "questions");
                model.addAttribute("sectionName", "我的问题");
                PaginationDTO<QuestionDTO> questionPaginationDTO = questionService.list(user.getId(), page, size);
                model.addAttribute("pagination", questionPaginationDTO);
                break;
            case "notifications":
                Long unreadCount = notificationService.getUnreadCount(user.getId());
                PaginationDTO<NotificationDTO> notificationPaginationDTO = notificationService.list(user.getId(), page, size);
                model.addAttribute("section", "notifications");
                model.addAttribute("sectionName", "最新通知");
                model.addAttribute("unreadCount",   unreadCount);
                model.addAttribute("pagination", notificationPaginationDTO);
                break;
            default:
                return "redirect:/error";
        }

        return "profile";
    }
}
