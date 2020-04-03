package com.demo.community.controller;

import com.demo.community.model.User;
import com.demo.community.service.AuthUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class ThirdPartyController {

    @Autowired
    AuthUserService authUserService;

    @GetMapping("auth/{callback}")
    public String thirdParty(
            @PathVariable(name= "callback") String callback,
            @RequestParam(name= "state") String state,
            @RequestParam(name= "code") String code,
            HttpServletRequest request,
            HttpServletResponse response
    ){
        User user = (User) request.getSession().getAttribute("user");
        if(user != null)
           return "redirect:/";

        switch(callback){
            case "githubCallback":
                authUserService.authByGithub(response, state, code);
                break;
            default:
                break;
        }
        return "redirect:/";
    }
}
