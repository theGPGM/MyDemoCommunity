package com.demo.community.service;

import com.demo.community.dto.CommentDTO;
import com.demo.community.enums.CommentTypeEnum;
import com.demo.community.enums.NotificationStatusEnum;
import com.demo.community.enums.NotificationTypeEnum;
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

    @Autowired
    private NotificationMapper notificationMapper;

    //评论
    @Transactional
    public void insert(Comment comment) {
        //主体是否为空
        if (comment.getParentId() == null || comment.getParentId() == 0)
            throw new CustomizeException(CustomizeErrorCode.TARGET_PRAM_NOT_FOUND);

        //类型是否出错
        if (comment.getType() == null || !CommentTypeEnum.isExist(comment.getType()))
            throw new CustomizeException(CustomizeErrorCode.TYPE_PRAM_NO_FOUND);

        if (comment.getType() == CommentTypeEnum.COMMENT.getType()) {

            //回复评论
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if (dbComment == null) {//要回复的评论不存在
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }
            commentMapper.insert(comment);

            //增加回复数
            incCommentCount(comment);

            //添加通知
            Comment parentComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            User commentator = userMapper.selectByPrimaryKey(comment.getCommentatorId());
            createNotify(comment, parentComment.getCommentatorId(), comment.getContent(), commentator.getName(),  NotificationTypeEnum.REPLY_COMMENT.getType(), parentComment.getParentId());
        } else {

            //回复问题
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if (question == null) {//要回复的问题不存在
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            commentMapper.insert(comment);

            //增加评论数
            question.setCommentCount(1);
            questionExtendMapper.incCommentCount(question);

            //添加通知
            User commentator = userMapper.selectByPrimaryKey(comment.getCommentatorId());
            createNotify(comment, question.getCreatorId(), question.getTitle(), commentator.getName(),  NotificationTypeEnum.REPLY_QUESTION.getType(), question.getId());
        }
    }

    /*
    *通过 targetId 获取评论列表
    * */
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

        //该问题所有评论者的 ID
        //set 去重
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

    private void incCommentCount(Comment comment) {
        Comment parentComment = new Comment();
        parentComment.setId(comment.getParentId());
        parentComment.setCommentCount(1);
        commentExtMapper.incCommentCount(parentComment);
    }

    private void createNotify(Comment comment, Long receivedId, String outerTitle, String notifierName, Integer type, Long outerId) {

        Notification notification = new Notification();
        notification.setOuterId(outerId);                                       //类型 id
        notification.setType(type);                                             //类型：问题、评论、点赞
        notification.setNotifierId(comment.getCommentatorId());                 //发送 id
        notification.setReceiverId(receivedId);                                 //接收 id
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());      //是否已读
        notification.setGmtCreate(System.currentTimeMillis());                  //创建时间
        notification.setNotifierName(notifierName);
        notification.setOuterTitle(outerTitle);
        notificationMapper.insert(notification);
    }
}

