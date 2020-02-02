package com.demo.comunity.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

public class LoginController {

    @GetMapping("/login")
    public String login(){
        return "/";
    }

    @PostMapping("/login")
    public String doLogin(){

        return "/";
    }
}
