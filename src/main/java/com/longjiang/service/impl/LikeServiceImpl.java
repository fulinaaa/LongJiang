package com.longjiang.service.impl;

import com.longjiang.service.CommentService;
import com.longjiang.service.LikeService;
import com.longjiang.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl  implements LikeService {
    @Autowired
    RedisTemplate redisTemplate;
    //某人给对象点赞
    @Override
    public void like(int userId, int entityType, int entityId,int entityUserId) {
        /*String entityLikeKey = RedisKeyUtil.genEntityLikeKey(entityType, entityId);
        boolean isMember=redisTemplate.opsForSet().isMember(entityLikeKey,userId);
        if(isMember==true){
            redisTemplate.opsForSet().remove(entityLikeKey,userId);
        }else{
            redisTemplate.opsForSet().add(entityLikeKey,userId);
        }*/
        //重构 当给帖子或评论点赞的时候同时给用户个人信息的获赞总数增加,要保证事务的原子性
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.genEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);
                boolean isMember=operations.opsForSet().isMember(entityLikeKey,userId);
                operations.multi();
                if(isMember){
                    operations.opsForSet().remove(entityLikeKey,userId);
                    operations.opsForValue().decrement(userLikeKey);
                }else{
                    operations.opsForSet().add(entityLikeKey,userId);
                    operations.opsForValue().increment(userLikeKey);
                }
                return operations.exec();
            }
        });
    }
    //查询某实体点赞的数量
    public long findEntityLikeCount(int entityType,int entityId){
        String entityLikeKey = RedisKeyUtil.genEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }
    //查询某人对某实体的点赞状态
    public int findEntityLikeStatus(int userId,int entityType,int entityId){
        String entityLikeKey = RedisKeyUtil.genEntityLikeKey(entityType, entityId);
        boolean isMember=redisTemplate.opsForSet().isMember(entityLikeKey,userId);
        return isMember?1:0;
    }
    //查询某人的赞的整数
    @Override
    public int findUserLikeCount( int userId){
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer likeCount = (Integer)redisTemplate.opsForValue().get(userLikeKey);
        return likeCount==null?0:likeCount.intValue();
    }
}
