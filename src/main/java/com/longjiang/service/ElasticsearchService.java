package com.longjiang.service;

import com.longjiang.Entity.DiscussPost;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ElasticsearchService {
    public void saveDiscussPost(DiscussPost discussPost) throws IOException;
    public void deleteDiscussPost(int id) throws IOException;
    public List<DiscussPost> searchDiscussPost(String keyword, int current, int limit) throws IOException;
    public long searchDiscussPostCount(String keyword) throws IOException;
}
