package com.longjiang.mapper;

import com.longjiang.Entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;


import java.util.List;
@Mapper
public interface DiscussPostMapper {
    //默认查询所有帖子，利用userId可以查看个人帖子，支持分页查询
    List<DiscussPost>selectAll(int userId, int offset, int limit,int orderMode);
    //默认查询所有帖子整数，利用userId可以查看个人帖子
    int selectDiscussPostCount(int userId);
    int insertDiscussPost(DiscussPost discussPost);
    DiscussPost selectDiscussPostById(int id);
    int updateCommentCount(int id,int commentCount);
    int updateType(int id,int type);
    int updateStatus(int id,int status);
    int updateScore(int id,double score);
}
