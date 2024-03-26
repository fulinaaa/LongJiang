package com.longjiang.service.impl;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.longjiang.Entity.DiscussPost;
import com.longjiang.mapper.DiscussPostMapper;
import com.longjiang.service.DiscussPostService;
import com.longjiang.util.SensitiveFilter;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class DiscussPostServiceImpl implements DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Value("${caffeine.post.max-size}")
    private int maxSize;
    @Value("${caffeine.post.expire-seconds}")
    private int expireSeconds;

    //帖子列表缓存
    private LoadingCache<String,List<DiscussPost>>postListCache;
    //帖子总数缓存
    private LoadingCache<Integer,Integer>postRowCache;
    @PostConstruct
    public void init(){
        //初始化帖子列表缓存
        postListCache= Caffeine.newBuilder().maximumSize(maxSize).expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Override
                    public @Nullable List<DiscussPost> load(@NonNull String key) throws Exception {
                        if(key==null ||key.length()==0){
                            throw  new IllegalArgumentException("参数错误");
                        }
                        String [] params=key.split(":");
                        if(params==null||params.length!=2){
                            throw new IllegalArgumentException("参数错误");
                        }
                        int offset=Integer.valueOf(params[0]);
                        int limit=Integer.valueOf(params[1]);
                        log.info("从数据库查询");
                        return discussPostMapper.selectAll(0,offset,limit,1);
                    }
                });
        //初始化帖子总数缓存
        postRowCache=Caffeine.newBuilder().maximumSize(maxSize).expireAfterWrite(expireSeconds,TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Override
                    public @Nullable Integer load(@NonNull Integer integer) throws Exception {
                        log.info("从数据库查询");
                        return discussPostMapper.selectDiscussPostCount(integer);
                    }
                });
    }
    @Override
    public List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit,int orderMode) {
        if(userId==0&&orderMode==1){
            return postListCache.get(offset+":"+limit);
        }
        log.info("从数据库查的数据");
        return discussPostMapper.selectAll(userId,offset,limit,orderMode);
    }

    @Override
    public int selectDiscussPostsCount(int userId) {
        if(userId==0){
            return postRowCache.get(userId);
        }
        log.info("从数据库查的数据");
        return discussPostMapper.selectDiscussPostCount(userId);
    }

    @Override
    public int addDiscussPost(DiscussPost discussPost) {
        if(discussPost==null){
            throw  new IllegalArgumentException("参数不能为空");
        }
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));
        //过滤敏感词
        discussPost.setTitle(sensitiveFilter.filter(discussPost.getTitle()));
        discussPost.setContent(sensitiveFilter.filter(discussPost.getContent()));
        return discussPostMapper.insertDiscussPost(discussPost);
    }

    @Override
    public DiscussPost findDiscussPostById(int id) {
        return discussPostMapper.selectDiscussPostById(id);
    }

    @Override
    public int updateCommentCount(int id,int commentCount) {
        return discussPostMapper.updateCommentCount(id,commentCount);
    }

    @Override
    public int updateType(int id, int type) {
        return discussPostMapper.updateType(id,type);
    }

    @Override
    public int updateStatus(int id, int status) {
        return discussPostMapper.updateStatus(id,status);
    }

    @Override
    public int updateScore(int id, double score) {
        return discussPostMapper.updateScore(id,score);
    }
}
