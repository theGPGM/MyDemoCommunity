package com.demo.comunity.dto;

import lombok.Data;

@Data
public class GithubUser {

    private Long id;
    private String name;
    private String bio;
    private String avatar_url;
}
