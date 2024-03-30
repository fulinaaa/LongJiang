package com.longjiang.quartz;

import com.longjiang.Entity.DiscussPost;
import com.longjiang.service.DiscussPostService;
import com.longjiang.service.ElasticsearchService;
import com.longjiang.service.LikeService;
import com.longjiang.util.LongJiangConstant;
import com.longjiang.util.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class PostScoreRefreshJob implements Job , LongJiangConstant {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private ElasticsearchService elasticsearchService;
    @Autowired
    private static final Date epoch;
    static {
        try {
            epoch=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-08-01 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("初始化失败",e);
        }
    }
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String redisKey= RedisKeyUtil.getPostScoreKey();
        BoundSetOperations operations=redisTemplate.boundSetOps(redisKey);
        if(operations.size()==0){
            log.info("任务取消，没有刷新的帖子");
            return;
        }
        log.info("任务开始,正在刷新帖子分数");
        while(operations.size()>0){
            this.refresh((Integer)operations.pop());
        }
        log.info("任务结束，帖子分数刷新完毕");
    }
    private void refresh(int postId){
        DiscussPost post = discussPostService.findDiscussPostById(postId);
        if(post==null){
            log.error("该帖子不存在: id="+postId);
            return;
        }
        boolean wonderful = post.getStatus()==1;
        int commentcount = post.getCommentCount();
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, postId);
        double w=(wonderful?75:0)+commentcount*10+likeCount*2;
        double score=Math.log10(Math.max(w,1))+(post.getCreateTime().getTime()-epoch.getTime())/(1000*3600*24);
        discussPostService.updateScore(postId,score);
        post.setScore(score);
        try {
            elasticsearchService.saveDiscussPost(post);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
