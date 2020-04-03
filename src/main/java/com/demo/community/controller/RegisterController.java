package com.demo.community.controller;

import com.demo.community.dto.AuthUserDTO;
import com.demo.community.dto.ResultDTO;
import com.demo.community.exception.CustomizeErrorCode;
import com.demo.community.model.User;
import com.demo.community.service.AuthUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class RegisterController {

    @Autowired
    private AuthUserService authUserService;

    @ResponseBody
    @RequestMapping(value = "/register.do", method = RequestMethod.POST)
    public Object post(@RequestBody AuthUserDTO authUserDTO,
                       HttpServletResponse response,
                       HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        if(user != null)
            return ResultDTO.errorOf(CustomizeErrorCode.LOGIN_USER_CANNOT_USE);

        if(authUserDTO == null)
            return ResultDTO.errorOf(CustomizeErrorCode.REGISTER_FAILED);

        if(StringUtils.isBlank(authUserDTO.getPassword()))
            return ResultDTO.errorOf(CustomizeErrorCode.PASSWORD_IS_EMPTY);

        if(StringUtils.isBlank(authUserDTO.getUserName()))
            return ResultDTO.errorOf(CustomizeErrorCode.USERNAME_IS_EMPTY);

        return authUserService.userNameRegister(authUserDTO, response);
    }

    @GetMapping("/register")
    public String registerPage(
            HttpServletRequest request
    ){
        User user = (User) request.getSession().getAttribute("user");
        if(user != null)
            return "redirect:/";

        return "register";
    }
}
