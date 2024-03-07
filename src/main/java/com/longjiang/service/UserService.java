package com.longjiang.service;

import com.longjiang.Entity.User;

public interface UserService {
    public User selectUserById(int id);
    public User selectUserByName(String name);
    public User selectUserByEmail(String email);

}
