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
    public Binding DirectLikeBinding(@Qualifier("DirectExchange") DirectExchange directExchange, Queue DirectLikeQueue){
        return BindingBuilder.bind(DirectLikeQueue).to(directExchange).with("like");
    }
    @Bean
    public Binding DirectFollowBinding(@Qualifier("DirectExchange") DirectExchange directExchange,Queue DirectFollowQueue){
        return BindingBuilder.bind(DirectFollowQueue).to(directExchange).with("follow");
    }
    @Bean
    public Binding DirectCommentBinding(@Qualifier("DirectExchange")DirectExchange directExchange,Queue DirectCommentQueue){
        return BindingBuilder.bind(DirectCommentQueue).to(directExchange).with("comment");
    }
}
