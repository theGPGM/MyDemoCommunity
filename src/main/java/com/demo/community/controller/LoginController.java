package com.demo.community.controller;

import com.demo.community.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class LoginController {

    @RequestMapping("/login")
    public String login(
            HttpServletRequest request
    ){

        User user = (User) request.getSession().getAttribute("user");
        if(user != null)
            return "index";

        return "login";
    }

    @RequestMapping("/register")
    public String register(
            HttpServletRequest request
    ){
        User user = (User) request.getSession().getAttribute("user");
        if(user != null)
            return "index";

        return "register";
    }
}
