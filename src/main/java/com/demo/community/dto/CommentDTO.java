package com.demo.community.dto;

import com.demo.community.model.User;
import lombok.Data;

@Data
public class CommentDTO {
    private Long id;
    private Integer type;
    private Long commentatorId;
    private Long gmtCreate;
    private Long gmtModified;
    private Long likeCount;
    private String content;
    private Long parentId;
    private User user;
}
