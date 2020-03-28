package com.demo.community.controller;

import com.demo.community.dto.LikeCommentDTO;
import com.demo.community.dto.ResultDTO;
import com.demo.community.enums.NotificationTypeEnum;
import com.demo.community.exception.CustomizeErrorCode;
import com.demo.community.model.Comment;
import com.demo.community.model.User;
import com.demo.community.service.CommentService;
import com.demo.community.service.LikeCommentService;
import com.demo.community.service.NotificationService;
import com.demo.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class LikeCommentController {

    @Autowired
    CommentService commentService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    UserService userService;

    @Autowired
    LikeCommentService likeCommentService;

    @ResponseBody
    @RequestMapping(value = "/like", method = RequestMethod.POST)
    public Object post(@RequestBody LikeCommentDTO likeCommentDTO,
                       HttpServletRequest request) {

        User user = (User) request.getSession().getAttribute("user");
        if (user == null)
            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);

        if (likeCommentDTO == null || likeCommentDTO.getCommentId() == null)
            return ResultDTO.errorOf(CustomizeErrorCode.LIKE_FAILED);

        likeCommentDTO.setLikerId(user.getId());

        if(likeCommentService.isLiked(likeCommentDTO)){
            likeCommentService.delete(likeCommentDTO);
            commentService.delLikeCount(likeCommentDTO.getCommentId());
            return ResultDTO.okOf();
        }

        likeCommentService.create(likeCommentDTO);
        commentService.incLikeCount(likeCommentDTO.getCommentId());
        Comment comment = commentService.getByCommentId(likeCommentDTO.getCommentId());
        User liker = userService.getById(likeCommentDTO.getLikerId());
        notificationService.createNotify(comment, likeCommentDTO.getLikerId(), comment.getContent(), liker.getName(), NotificationTypeEnum.LIKE_COMMENT.getType(), comment.getParentId());
        return ResultDTO.okOf();
    }
}
