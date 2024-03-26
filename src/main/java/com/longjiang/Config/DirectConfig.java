package com.longjiang.Config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DirectConfig {
    @Bean
    public DirectExchange DirectExchange(){
        return new DirectExchange("direct");
    }
    @Bean
    public Queue DirectLikeQueue(){
        return new Queue("like");
    }
    @Bean
    public Queue DirectFollowQueue(){
        return new Queue("follow");
    }
    @Bean
    public Queue DirectCommentQueue(){
        return new Queue("comment");
    }
    @Bean
    public Queue DirectPublishQueue(){
        return new Queue("publish");
    }
    @Bean
    public Queue DirectDeleteQueue(){
        return new Queue("delete");
    }
    @Bean
    public Binding DirectLikeBinding(@Qualifier("DirectExchange") DirectExchange directExchange, Queue DirectLikeQueue){
        return BindingBuilder.bind(DirectLikeQueue).to(directExchange).with("like");
    }
    @Bean
    public Binding DirectDeleteBinding(@Qualifier("DirectExchange") DirectExchange directExchange, Queue DirectDeleteQueue){
        return BindingBuilder.bind(DirectDeleteQueue).to(directExchange).with("delete");
    }
    @Bean
    public Binding DirectFollowBinding(@Qualifier("DirectExchange") DirectExchange directExchange,Queue DirectFollowQueue){
        return BindingBuilder.bind(DirectFollowQueue).to(directExchange).with("follow");
    }
    @Bean
    public Binding DirectCommentBinding(@Qualifier("DirectExchange")DirectExchange directExchange,Queue DirectCommentQueue){
        return BindingBuilder.bind(DirectCommentQueue).to(directExchange).with("comment");
    }
    @Bean
    public Binding DirectPublishBinding(@Qualifier("DirectExchange") DirectExchange directExchange, Queue DirectPublishQueue){
        return BindingBuilder.bind(DirectPublishQueue).to(directExchange).with("publish");
    }
}
