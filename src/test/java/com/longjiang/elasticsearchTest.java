package com.longjiang;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.longjiang.Entity.DiscussPost;
import com.longjiang.mapper.DiscussPostMapper;
import com.mysql.cj.xdevapi.CreateIndexParams;
import com.mysql.cj.xdevapi.UpdateResult;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.security.CreateApiKeyRequest;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.util.CollectionUtils;

import javax.naming.directory.SearchResult;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import static com.longjiang.constants.DiscussPostConstants.MAPPING_TEMPLATE;

@SpringBootTest
public class elasticsearchTest {
    @Autowired
    RestHighLevelClient client;
    @Autowired
    DiscussPostMapper discussPostMapper;
    @Test
    public void send() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest("discusspost");
        request.source(MAPPING_TEMPLATE, XContentType.JSON);
        client.indices().create(request, RequestOptions.DEFAULT);
    }
    @Test
    public void testInsert() throws IOException {
        BulkRequest request = new BulkRequest();
        for(int i=100;i<300;i++){
            DiscussPost discussPost = discussPostMapper.selectDiscussPostById(i);
            if(discussPost!=null)request.add(new IndexRequest("discusspost").id(String.valueOf(discussPost.getId())).source(JSON.toJSONString(discussPost),XContentType.JSON));
        }
        client.bulk(request,RequestOptions.DEFAULT);
    }
    @Test
    public void testUpdate() throws IOException {
        UpdateRequest updateRequest=new UpdateRequest("discusspost","231");
        updateRequest.doc("content","我是新人，使劲灌水");
        client.update(updateRequest,RequestOptions.DEFAULT);
    }
    @Test
    public void testDel() throws IOException {
        DeleteRequest request = new DeleteRequest("discusspost","231");
        client.delete(request,RequestOptions.DEFAULT);
    }
    @Test
    public void testSearch() throws IOException {
        GetRequest request = new GetRequest("discusspost","111");
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        String source = response.getSourceAsString();
        DiscussPost post = JSON.parseObject(source, DiscussPost.class);
        System.out.println(post);
    }
    @Test
    public void testMatchAll() throws IOException {
        SearchRequest request = new SearchRequest("discusspost");
        //request.source().query(QueryBuilders.matchAllQuery());
        //单字段查询
        //request.source().query(QueryBuilders.matchQuery("all","互联网"));
        //多字段查询
        request.source().query(QueryBuilders.multiMatchQuery("互联网","title"));
        //排序
        request.source().sort("createTime", SortOrder.DESC);
        //分页
        request.source().from(0).size(5).highlighter(new HighlightBuilder().field("title").requireFieldMatch(false));
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        //解析响应
        SearchHits hits = response.getHits();
        //获取总条数
        long value = hits.getTotalHits().value;
        System.out.println(value);
        SearchHit[] hitsHits = hits.getHits();
        for (SearchHit hitsHit : hitsHits) {
            String source = hitsHit.getSourceAsString();
            DiscussPost post = JSON.parseObject(source, DiscussPost.class);
            Map<String, HighlightField> fields = hitsHit.getHighlightFields();
            if(!CollectionUtils.isEmpty(fields)){
                HighlightField highlightField = fields.get("title");
                if(highlightField!=null){
                    String string = highlightField.getFragments()[0].string();
                    post.setTitle(string);
                }
            }
            System.out.println(post);
        }
    }
}
