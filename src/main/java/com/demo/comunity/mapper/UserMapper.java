package com.demo.comunity.mapper;

import com.demo.comunity.Model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface UserMapper {

    @Insert("INSERT INTO user (name, account_id, token, gmt_create, gmt_modified) " +
            "values(#{name}, #{accountId}, #{token}, #{gmtCreate}, #{gmtModified});")
    void insert(User user);
}
