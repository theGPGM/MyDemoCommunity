package com.demo.community.mapper;

import com.demo.community.model.Question;

import java.util.List;

public interface QuestionExtMapper {

    int incView(Question record);

    int incCommentCount(Question record);

    List<Question>  selectRelatedTopic(Question question);
}