package com.demo.community.service;

import com.demo.community.mapper.UserMapper;
import com.demo.community.model.User;
import com.demo.community.model.UserExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserMapper userMapper;

    public User getById(Long likerId) {
        return userMapper.selectByPrimaryKey(likerId);
    }
}
