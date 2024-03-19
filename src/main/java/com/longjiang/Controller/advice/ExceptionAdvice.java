package com.longjiang.Controller.advice;

import com.longjiang.util.LongJiangConstant;
import com.longjiang.util.LongJiangUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@ControllerAdvice(annotations = Controller.class)
@Slf4j
public class ExceptionAdvice {
    @ExceptionHandler(Exception.class)
    public void handleException(Exception e, HttpServletRequest req, HttpServletResponse res) throws IOException {
        log.error(e.getMessage());
        for (StackTraceElement element : e.getStackTrace()) {
            log.error(String.valueOf(element));
        }
        String header = req.getHeader("x-requested-with");
        if(header.equals("XMLHttpRequest")){
            res.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = res.getWriter();
            writer.write(LongJiangUtil.getJSONString(1,"服务器异常"));
        }else{
            res.sendRedirect(req.getContextPath()+"/error");
        }
    }
}
