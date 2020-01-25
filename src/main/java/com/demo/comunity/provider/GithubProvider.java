package com.demo.comunity.provider;

import com.alibaba.fastjson.JSON;
import com.demo.comunity.dto.AccessTokenDTO;
import com.demo.comunity.dto.GithubUser;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GithubProvider {

    //获取github传来的token
    public String getAccessToken(AccessTokenDTO accessTokenDTO){
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create( mediaType,JSON.toJSONString(accessTokenDTO));
        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String string = response.body().string();
            String[] spilt = string.split("&");
            String token = spilt[0].split("=")[1];
            return token;
        }catch (Exception e){
            e.printStackTrace();
            getAccessToken(accessTokenDTO);
        }

        return null;
    }

    //上传token获取用户信息
    public GithubUser getUser(String accessToken){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.github.com/user?oauth_token="+accessToken)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String string = response.body().string();
            GithubUser githubUser = JSON.parseObject(string, GithubUser.class);
            return githubUser;
        } catch (IOException e) {
            e.printStackTrace();
            getUser(accessToken);
        }
        return null;
    }
}
