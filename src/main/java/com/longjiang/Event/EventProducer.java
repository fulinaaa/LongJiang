package com.longjiang.Event;
import com.alibaba.fastjson.JSONObject;
import com.longjiang.Entity.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
@Component
@Slf4j
public class EventProducer {
    @Autowired
    RabbitTemplate rabbitTemplate;

    public void fireEvent(Event event){
        rabbitTemplate.convertAndSend("direct",event.getTopic(), JSONObject.toJSONString(event));
    }
}
