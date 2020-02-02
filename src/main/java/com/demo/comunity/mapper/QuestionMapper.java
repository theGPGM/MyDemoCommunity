package com.demo.comunity.mapper;

import com.demo.comunity.model.Question;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface QuestionMapper {

    @Insert("insert into question (title, description, gmt_create, gmt_modified, creator_id,tag) values (#{title}, #{description}, #{gmtCreate},#{gmtModified},#{creatorId},#{tag})")
    void insert(Question question);

    @Select("SELECT * FROM question limit #{offset}, #{size}")
    List<Question> list(@Param(value = "offset") Integer offset, @Param(value = "size")Integer size);

    @Select("SELECT COUNT(1) FROM question")
    Integer count();
}
