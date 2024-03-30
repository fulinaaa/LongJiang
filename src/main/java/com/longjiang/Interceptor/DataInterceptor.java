package com.longjiang.Interceptor;

import com.longjiang.Entity.User;
import com.longjiang.service.DataService;
import com.longjiang.util.BaseContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class DataInterceptor implements HandlerInterceptor {
    @Autowired
    private DataService dataService;
    @Autowired
    private BaseContext baseContext;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //统计uv
        String ip=request.getRemoteHost();
        dataService.recordUV(ip);
        //统计DAU
        User user=baseContext.getUser();
        if(user!=null) {
            dataService.recordDAU(user.getId());
        }
        return true;
    }
}
