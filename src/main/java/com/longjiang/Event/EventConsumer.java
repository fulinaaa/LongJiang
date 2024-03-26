package com.longjiang.Event;

import com.alibaba.fastjson.JSONObject;
import com.longjiang.Entity.DiscussPost;
import com.longjiang.Entity.Event;
import com.longjiang.Entity.Message;
import com.longjiang.mapper.DiscussPostMapper;
import com.longjiang.service.DiscussPostService;
import com.longjiang.service.ElasticsearchService;
import com.longjiang.service.MessageService;
import com.longjiang.service.impl.DiscussPostServiceImpl;
import com.longjiang.util.LongJiangConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class EventConsumer implements LongJiangConstant {
    @Autowired
    private MessageService messageService;
    @Autowired
    private ElasticsearchService elasticsearchService;
    @Autowired
    private DiscussPostService discussPostService;
    @RabbitListener(queues = "follow")
    @RabbitListener(queues = "like")
    @RabbitListener(queues = "comment")
    public void handleCommentMessage(String msg){
        if(msg==null|| StringUtils.isBlank(msg)){
            log.error("消息内容为空");
            return;
        }
        Event event= JSONObject.parseObject(msg,Event.class);
        if(event==null){
            log.error("消息格式错误");
            return;
        }
        System.out.println(event);
        //发送系统通知
        Message message=new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());
        HashMap<String, Object> content = new HashMap<>();
        content.put("userId",event.getUserId());
        content.put("entityType",event.getEntityType());
        content.put("entityId",event.getEntityId());
        if(!event.getData().isEmpty()){
            for(Map.Entry<String,Object> entry:event.getData().entrySet()){
                content.put(entry.getKey(),entry.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }
    @RabbitListener(queues = "publish")
    public void handlePublishMessage(String msg) throws IOException {
        if(msg==null|| StringUtils.isBlank(msg)){
            log.error("消息内容为空");
            return;
        }
        Event event= JSONObject.parseObject(msg,Event.class);
        if(event==null){
            log.error("消息格式错误");
            return;
        }
        System.out.println(event);
        DiscussPost post = discussPostService.findDiscussPostById(event.getEntityId());
        elasticsearchService.saveDiscussPost(post);
    }
    @RabbitListener(queues = "delete")
    public void handleDeleteMessage(String msg) throws IOException {
        if(msg==null|| StringUtils.isBlank(msg)){
            log.error("消息内容为空");
            return;
        }
        Event event= JSONObject.parseObject(msg,Event.class);
        if(event==null){
            log.error("消息格式错误");
            return;
        }
        System.out.println(event);

        elasticsearchService.deleteDiscussPost(event.getEntityId());
    }
}
