package com.longjiang.service.impl;

import com.longjiang.Entity.User;
import com.longjiang.mapper.UserMapper;
import com.longjiang.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;
    @Override
    public User selectUserById(int id) {
        return userMapper.selectById(id);
    }

    @Override
    public User selectUserByName(String name) {
        return userMapper.selectByName(name);
    }

    @Override
    public User selectUserByEmail(String email) {
        return selectUserByEmail(email);
    }
}
