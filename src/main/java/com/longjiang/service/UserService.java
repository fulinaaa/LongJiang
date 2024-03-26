package com.longjiang.service;

import com.longjiang.Entity.LoginTicket;
import com.longjiang.Entity.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;

public interface UserService {
    public User selectUserById(int id);
    public User selectUserByName(String name);
    public User selectUserByEmail(String email);
    public Map<String,Object>register(User user);
    public int activation(int userId,String code);
    public Map<String,Object> login(String username,String password,int expiredSecond);
    public void logout(String ticket);
    public LoginTicket findLoginTicket(String ticket);
    public int updateHeader(int userId,String headerUrl);
    public Collection<? extends GrantedAuthority> getAuthorities(int userId);
}
