package com.longjiang.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public interface LikeService {
    public void like(int userId,int entityType,int entityId,int entityUserId);
    public int findEntityLikeStatus(int userId,int entityType,int entityId);
    public long findEntityLikeCount(int entityType,int entityId);
    public int findUserLikeCount( int userId);
}
