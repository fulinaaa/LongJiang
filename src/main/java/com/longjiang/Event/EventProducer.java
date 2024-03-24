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

        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean b, String s) {
                if(!b){
                    log.error(s);
                }
            }
        });
        rabbitTemplate.convertAndSend("direct123",event.getTopic(), JSONObject.toJSONString(event));
    }
}
