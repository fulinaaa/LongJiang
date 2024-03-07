package com.longjiang.mapper;

import com.longjiang.Entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface DiscussPostMapper {
    //默认查询所有帖子，利用userId可以查看个人帖子，支持分页查询
    List<DiscussPost>selectAll(int userId, int offset, int limit);
    //默认查询所有帖子整数，利用userId可以查看个人帖子
    int selectDiscussPostCount(int userId);
}
