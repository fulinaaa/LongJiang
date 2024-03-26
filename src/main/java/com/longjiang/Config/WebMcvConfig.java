package com.longjiang.Config;

import com.longjiang.Interceptor.DataInterceptor;
import com.longjiang.Interceptor.LoginRequiredInterceptor;
import com.longjiang.Interceptor.LoginTicketInterceptor;
import com.longjiang.Interceptor.MessageInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMcvConfig implements WebMvcConfigurer {
    @Autowired
    LoginTicketInterceptor loginTicketInterceptor;
    /*@Autowired
    LoginRequiredInterceptor loginRequiredInterceptor;*/
    @Autowired
    MessageInterceptor messageInterceptor;
    @Autowired
    DataInterceptor dataInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/.jpeg");
//        registry.addInterceptor(loginRequiredInterceptor)
//                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/.jpeg");
        registry.addInterceptor(messageInterceptor)
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/.jpeg");
        registry.addInterceptor(dataInterceptor).excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/.jpeg");
    }
}
