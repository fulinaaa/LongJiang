package com.longjiang.service;

import com.longjiang.Entity.DiscussPost;
import com.longjiang.mapper.DiscussPostMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public interface DiscussPostService {
    List<DiscussPost> selectDiscussPosts(int userId,int offset,int limit);
    public int selectDiscussPostsCount(int userId);
}
