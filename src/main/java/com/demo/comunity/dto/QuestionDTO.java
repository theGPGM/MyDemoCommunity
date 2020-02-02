package com.demo.comunity.dto;

import com.demo.comunity.model.User;
import lombok.Data;

@Data
public class QuestionDTO {

    private String title;
    private String description;
    private Long gmtCreate;
    private Long gmtModified;
    private Integer creatorId;
    private String tag;
    private Integer commentCount;
    private Integer viewCount;
    private Integer likeCount;
    private User user;
}
