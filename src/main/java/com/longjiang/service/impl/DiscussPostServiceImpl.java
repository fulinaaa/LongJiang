package com.longjiang.service.impl;

import com.longjiang.Entity.DiscussPost;
import com.longjiang.mapper.DiscussPostMapper;
import com.longjiang.service.DiscussPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class DiscussPostServiceImpl implements DiscussPostService {
    @Autowired
    DiscussPostMapper discussPostMapper;
    @Override
    public List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit) {
        return discussPostMapper.selectAll(userId,offset,limit);
    }

    @Override
    public int selectDiscussPostsCount(int userId) {
        return discussPostMapper.selectDiscussPostCount(userId);
    }
}
