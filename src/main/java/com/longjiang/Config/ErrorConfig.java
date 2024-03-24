package com.longjiang.Config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "spring.rabbitmq.listener.simple.retry",name="enabled",havingValue = "true")
public class ErrorConfig {
    @Bean
    public DirectExchange errorExchange(){
        return new DirectExchange("error");
    }
    @Bean
    public Queue errorQueue(){
        return new Queue("error.queue");
    }
    @Bean
    public Binding errorBinding(Queue errorQueue,DirectExchange errorExchange){
        return  BindingBuilder.bind(errorQueue).to(errorExchange).with("error");
    }
    @Bean
    public MessageRecoverer messageRecoverer(RabbitTemplate rabbitTemplate){
        return new RepublishMessageRecoverer(rabbitTemplate,"error","error");
    }
}
