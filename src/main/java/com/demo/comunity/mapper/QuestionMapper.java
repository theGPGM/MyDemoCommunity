package com.demo.comunity.mapper;

import com.demo.comunity.model.Question;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface QuestionMapper {

    @Insert("insert into question (title, description, gmt_create, gmt_modified, creator_id,tag) values (#{title}, #{description}, #{gmtCreate},#{gmtModified},#{creatorId},#{tag})")
    void insert(Question question);
}
