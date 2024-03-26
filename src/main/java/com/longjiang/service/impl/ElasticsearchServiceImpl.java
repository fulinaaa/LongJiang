package com.longjiang.service.impl;

import com.longjiang.Entity.DiscussPost;
import com.longjiang.service.DiscussPostService;
import com.longjiang.service.ElasticsearchService;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ElasticsearchServiceImpl implements ElasticsearchService {
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public void saveDiscussPost(DiscussPost discussPost) {

    }
}
