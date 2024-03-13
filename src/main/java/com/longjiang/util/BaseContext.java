package com.longjiang.util;

import com.longjiang.Entity.User;
import org.springframework.stereotype.Component;

/*
* 持有用户信息,代替session对象
* */
@Component
public class BaseContext {
    private ThreadLocal<User> users=new ThreadLocal<>();
    public void setUser(User user){
        users.set(user);
    }
    public User getUser(){
        return users.get();
    }
    public void clear(){
        users.remove();
    }
}
