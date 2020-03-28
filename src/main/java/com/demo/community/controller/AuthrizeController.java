package com.demo.community.controller;

import com.demo.community.dto.AccessTokenDTO;
import com.demo.community.dto.GithubUser;
import com.demo.community.mapper.UserMapper;
import com.demo.community.model.User;
import com.demo.community.model.UserExample;
import com.demo.community.provider.GithubProvider;
import com.demo.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class AuthrizeController {

    private static final Integer COOKIE_TIME = 7 * 24 * 60 * 60;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private GithubProvider githubProvider;

    @Autowired
    private UserService userService;



    @Value("${github.client.id}")
    private String clientId;

    @Value("${github.client.secret}")
    private String clientSecret;

    @Value("${github.redirect.uri}")
    private String redirectUri;

    @GetMapping("/callback")
    public String callback(@RequestParam(name= "code") String code,
                           @RequestParam(name="state")String state,
                           HttpServletRequest request,
                           HttpServletResponse response){
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setCode(code);
        accessTokenDTO.setState(state);
        accessTokenDTO.setRedirect_uri(redirectUri);
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(clientSecret);

        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        GithubUser githubUser = githubProvider.getUser(accessToken);
        if(githubUser != null && githubUser.getId() != null){
            User user = new User();
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setAvatarUrl(githubUser.getAvatar_url());
            userService.createOrUpdate(user);
            Cookie user_session = new Cookie("user_session", token);
            user_session.setMaxAge(COOKIE_TIME);
            response.addCookie(user_session);
            UserExample userExample = new UserExample();
            userExample.createCriteria()
                    .andAccountIdEqualTo(user.getAccountId());
            Long id = userMapper.selectByExample(userExample).get(0).getId();
            user.setId(id);
            request.getSession().setAttribute("user",user);

            return "redirect:/";
        }else{
            //登录失败，重新登录
            return "redirect:/";
        }
    }
}
