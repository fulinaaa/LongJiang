package com.longjiang.service;

import com.longjiang.Entity.User;

import java.util.List;
import java.util.Map;

public interface FollowService {
    public void follow(int userId,int entityType,int entityId);
    public void unfollow(int userId,int entityType,int entityId);
    //查询某个用户关注实体的数量
    public long findFolloweeCount(int userId,int entityType);
    //查询某个实体的粉丝数量
    public long findFollowerCount(int entityType,int entityId);
    //查询当前用户受否以观察此实体
    public boolean hasFollowed(int userId,int entityType,int entityId);
    //查询某个用户关注的人
    public List<Map<String,Object>> findFollowees(int userId,int offset,int limit);
    //查询某个用户的粉丝
    public List<Map<String,Object>> findFollowers(int userId,int offset,int limit);
}
