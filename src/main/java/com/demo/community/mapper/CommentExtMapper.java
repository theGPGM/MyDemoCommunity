package com.demo.community.mapper;

import com.demo.community.model.Comment;
import com.demo.community.model.CommentExample;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface CommentExtMapper {
    int incCommentCount(Comment comment);

    int incLikeCount(Comment comment);

    int delLikeCount(Comment comment);
}