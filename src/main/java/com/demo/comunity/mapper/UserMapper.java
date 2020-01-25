package com.demo.comunity.mapper;

import com.demo.comunity.Model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import javax.servlet.http.Cookie;


@Mapper
public interface UserMapper {

    @Insert("INSERT INTO user (name, account_id, token, gmt_create, gmt_modified) " +
            "values(#{name}, #{accountId}, #{token}, #{gmtCreate}, #{gmtModified});")
    void insert(User user);

    @Select("SELECT * FROM user where token = #{token};")
    User findByToken(@Param("token") String token);
}
