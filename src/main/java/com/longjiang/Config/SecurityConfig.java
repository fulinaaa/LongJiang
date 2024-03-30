package com.longjiang.Config;

import com.longjiang.util.LongJiangConstant;
import com.longjiang.util.LongJiangUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements LongJiangConstant {
    @Override
    public void configure(WebSecurity web) throws Exception {
        //取消对静态资源的拦截
        web.ignoring().antMatchers("/resources/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //授权
        http.authorizeRequests()
                .antMatchers("/user/setting","/user/upload","/discuss/add",
                        "/comment/add/**","/letter/**",
                        "/like","/follow",
                        "/unfollow","/notice/**")
                .hasAnyAuthority(AUTHORITY_USER,AUTHORITY_ADMIN,AUTHORITY_MODERATOR)
                .antMatchers("/discuss/top","/discuss/wonderful").hasAnyAuthority(
                        AUTHORITY_MODERATOR
                )
                .antMatchers("/discuss/delete","/data/**")
                .hasAnyAuthority(
                        AUTHORITY_ADMIN
                )
                .anyRequest().permitAll()
                .and().csrf().disable();
        //权限不够处理
        //authenticationEntryPoint(没有登录怎么处理)
        //accessDeniedHandler 权限不足怎么处理
        http.exceptionHandling().authenticationEntryPoint(new AuthenticationEntryPoint() {
            @Override
            public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                String xRequestedWith = request.getHeader("x-requested-with");
                if("XMLHttpRequest".equals(xRequestedWith)){
                    //异步请求
                    response.setContentType("application/plain;charset=utf-8");
                    PrintWriter writer = response.getWriter();
                    writer.write(LongJiangUtil.getJSONString(403,"你还没有登录"));
                }else{
                    System.out.println("没有登录");
                    //同步请求
                    response.sendRedirect(request.getContextPath()+"/login");
                }
            }
        }).accessDeniedHandler(new AccessDeniedHandler() {
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                String xRequestedWith = request.getHeader("x-requested-with");
                if("XMLHttpRequest".equals(xRequestedWith)){
                    //异步请求
                    response.setContentType("application/plain;charset=utf-8");
                    PrintWriter writer = response.getWriter();
                    writer.write(LongJiangUtil.getJSONString(403,"你没有访问此功能的权限"));
                }else{
                    response.sendRedirect(request.getContextPath()+"/denied");
                }
            }
        });
        //security底层默认会拦截/logout请求，进行退出处理
        //覆盖它的默认的逻辑，才能执行自己的代码
        //善意的欺骗
        http.logout().logoutUrl("/securitylogout");
    }
}
