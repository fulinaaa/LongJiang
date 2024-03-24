package com.longjiang.Event;

import com.alibaba.fastjson.JSONObject;
import com.longjiang.Entity.Event;
import com.longjiang.Entity.Message;
import com.longjiang.service.MessageService;
import com.longjiang.util.LongJiangConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class EventConsumer implements LongJiangConstant {
    @Autowired
    private MessageService messageService;
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
}
