package com.demo.community.controller;

import com.demo.community.dto.CommentCreateDTO;
import com.demo.community.dto.CommentDTO;
import com.demo.community.dto.ResultDTO;
import com.demo.community.enums.CommentTypeEnum;
import com.demo.community.exception.CustomizeErrorCode;
import com.demo.community.mapper.NotificationMapper;
import com.demo.community.model.Comment;
import com.demo.community.model.Notification;
import com.demo.community.model.User;
import com.demo.community.service.CommentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class CommentController {
    @Autowired
    private CommentService commentService;


    @ResponseBody
    @RequestMapping(value = "/comment", method = RequestMethod.POST)
    public Object post(@RequestBody CommentCreateDTO commentCreateDTO,
                       HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        if(user == null)
            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);

        if(commentCreateDTO == null || StringUtils.isBlank(commentCreateDTO.getContent()))
            return ResultDTO.errorOf(CustomizeErrorCode.CONTENT_IS_EMPTY);


        Comment comment = createComment(commentCreateDTO, user);
        commentService.insert(comment);

        return ResultDTO.okOf();
    }

    @ResponseBody
    @RequestMapping(value = "/comment/{id}", method = RequestMethod.GET)
    public ResultDTO<List<CommentDTO>> subComments(@PathVariable(name = "id") Long id){
        List<CommentDTO> commentDTOS = commentService.listByTargetId(id, CommentTypeEnum.COMMENT);
        return ResultDTO.okOf(commentDTOS);
    }

    private Comment createComment(@RequestBody CommentCreateDTO commentCreateDTO, User user) {
        Comment comment = new Comment();
        comment.setParentId(commentCreateDTO.getParentId());
        comment.setContent(commentCreateDTO.getContent());
        comment.setType(commentCreateDTO.getType());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setGmtModified(comment.getGmtCreate());
        comment.setCommentatorId(user.getId());
        comment.setLikeCount(0);
        comment.setCommentCount(0);
        return comment;
    }
}
