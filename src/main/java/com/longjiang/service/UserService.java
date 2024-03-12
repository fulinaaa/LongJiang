package com.longjiang.service;

import com.longjiang.Entity.User;

import java.util.Map;

public interface UserService {
    public User selectUserById(int id);
    public User selectUserByName(String name);
    public User selectUserByEmail(String email);
    public Map<String,Object>register(User user);
    public int activation(int userId,String code);
    public Map<String,Object> login(String username,String password,int expiredSecond);
    public void logout(String ticket);
}
