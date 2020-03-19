package com.demo.community.service;

import com.demo.community.dto.CommentDTO;
import com.demo.community.enums.CommentTypeEnum;
import com.demo.community.exception.CustomizeErrorCode;
import com.demo.community.exception.CustomizeException;
import com.demo.community.mapper.*;
import com.demo.community.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionExtMapper questionExtendMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CommentExtMapper commentExtMapper;

    //插入评论
    @Transactional
    public void insert(Comment comment) {
        //评论主体是否为空
        if (comment.getParentId() == null || comment.getParentId() == 0)
            throw new CustomizeException(CustomizeErrorCode.TARGET_PRAM_NOT_FOUND);

        //评论类型是否出错
        if (comment.getType() == null || !CommentTypeEnum.isExist(comment.getType()))
            throw new CustomizeException(CustomizeErrorCode.TYPE_PRAM_NO_FOUND);

        if (comment.getType() == CommentTypeEnum.COMMENT.getType()) {
            //回复评论
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if (dbComment == null) {//要回复的评论不存在
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }
            //增加回复数
            Comment parentComment = new Comment();
            parentComment.setId(comment.getParentId());
            parentComment.setCommentCount(1);
            commentExtMapper.incCommentCount(parentComment);
            commentMapper.insert(comment);
        } else {
            //回复问题
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if (question == null) {//要回复的问题不存在
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            commentMapper.insert(comment);
            question.setCommentCount(1);
            questionExtendMapper.incCommentCount(question);     //增加评论数
        }
    }

    public List<CommentDTO> listByTargetId(Long id, CommentTypeEnum type) {

        //获取该问题下的评论
        CommentExample example = new CommentExample();
        example.createCriteria()
                .andParentIdEqualTo(id)
                .andTypeEqualTo(type.getType());
        example.setOrderByClause("gmt_create desc");
        List<Comment> comments = commentMapper.selectByExample(example);
        if (comments.size() == 0)
            return new ArrayList<>();

        //获取该问题所有去重评论者的 ID
        //使用set可以去重 ID
        Set<Long> commentators = comments.stream()
                .map(comment -> comment.getCommentatorId()).collect(Collectors.toSet());

        List<Long> userIds = new ArrayList<>(commentators);

        //通过 ID 获取 User 对象
        //将 User 与 ID 聚合为一个 Map
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andIdIn(userIds);
        List<User> users = userMapper.selectByExample(userExample);
        Map<Long, User> userMap = users.stream()
                .collect(Collectors.toMap(user -> user.getId(), user -> user));

        //使用 User 与评论组成 CommentDTO
        List<CommentDTO> commentDTOS = comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment, commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentatorId()));
            return commentDTO;
        }).collect(Collectors.toList());

        return commentDTOS;
    }
}

