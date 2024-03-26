package com.longjiang.service.impl;

import com.alibaba.fastjson.JSON;
import com.longjiang.Entity.DiscussPost;
import com.longjiang.service.ElasticsearchService;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ElasticsearchServiceImpl implements ElasticsearchService {
    @Autowired
    private RestHighLevelClient client;

    @Override
    public void saveDiscussPost(DiscussPost discussPost) throws IOException {
        BulkRequest request = new BulkRequest();
        request.add(new IndexRequest("discusspost").id(String.valueOf(discussPost.getId())).source(JSON.toJSONString(discussPost), XContentType.JSON));
        client.bulk(request, RequestOptions.DEFAULT);
    }

    @Override
    public void deleteDiscussPost(int id) throws IOException {
        DeleteRequest request = new DeleteRequest("discusspost",String.valueOf(id));
        client.delete(request,RequestOptions.DEFAULT);
    }

    @Override
    public List<DiscussPost> searchDiscussPost(String keyword, int offset, int limit) throws IOException {
        ArrayList<DiscussPost> list=new ArrayList<>();
        SearchRequest request = new SearchRequest("discusspost");
        //多字段查询
        request.source().query(QueryBuilders.multiMatchQuery(keyword,"title","content"));
        //排序
        request.source().sort("createTime", SortOrder.DESC);
        //分页
        request.source().from(offset).size(limit).highlighter(new HighlightBuilder().field("title").field("content").requireFieldMatch(false));
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        //解析响应
        SearchHits hits = response.getHits();
        SearchHit[] hitsHits = hits.getHits();
        for (SearchHit hitsHit : hitsHits) {
            String source = hitsHit.getSourceAsString();
            Map<String, HighlightField> fields = hitsHit.getHighlightFields();
            if(!CollectionUtils.isEmpty(fields)){
                DiscussPost post = JSON.parseObject(source, DiscussPost.class);
                HighlightField highlightField = fields.get("title");
                if(highlightField!=null){
                    String title = highlightField.getFragments()[0].string();
                    post.setTitle(title);
                }
                 highlightField = fields.get("content");
                if(highlightField!=null){
                    String content=highlightField.getFragments()[0].string();
                    post.setContent(content);
                }
                list.add(post);
            }
        }
        return list;
    }

    @Override
    public long searchDiscussPostCount(String keyword) throws IOException {
        SearchRequest request = new SearchRequest("discusspost");
        //多字段查询
        request.source().query(QueryBuilders.multiMatchQuery(keyword,"title","content"));
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        //解析响应
        SearchHits hits = response.getHits();
        //获取总条数
        long value = hits.getTotalHits().value;
        return value;
    }

}
