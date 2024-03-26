package com.longjiang;

import org.apache.http.HttpHost;
import org.apache.ibatis.annotations.Mapper;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class LongjiangApplication {
	@PostConstruct
	public void init(){
		//解决netty启动冲突问题
		//
		System.setProperty("es.set.netty.runtime.available.processors","false");
	}
	public static void main(String[] args) {
		SpringApplication.run(LongjiangApplication.class, args);
	}
	@Bean
	public RestHighLevelClient client(){
		return new RestHighLevelClient(RestClient.builder(HttpHost.create("http://localhost:9200")));
	}
}


