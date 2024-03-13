package com.longjiang;

import com.longjiang.Entity.DiscussPost;
import com.longjiang.Entity.LoginTicket;
import com.longjiang.Entity.User;
import com.longjiang.mapper.DiscussPostMapper;
import com.longjiang.mapper.LoginTicketMapper;
import com.longjiang.mapper.UserMapper;
import com.longjiang.util.JWTUtil;
import com.longjiang.util.MailClient;
import com.longjiang.util.SensitiveFilter;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.HashMap;
import java.util.List;


@SpringBootTest
public class MapperTest {
    @Autowired
    LoginTicketMapper loginTicketMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Test
    public void mytest(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("你好吗","我很好");
        String longJiang = JWTUtil.createJWT("LongJiang", map);
        Claims claims = JWTUtil.parseJwt("LongJiang", longJiang);
        claims.forEach((o,s)-> System.out.println(o+": "+s));
    }
    @Test void testMapper(){
        LoginTicket loginTicket=new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("lol");
        loginTicket.setExpired(new Date(System.currentTimeMillis()+1000*60));
        loginTicket.setStatus(0);
        loginTicketMapper.insertLoginTicket(loginTicket);
        LoginTicket lol = loginTicketMapper.selectByTicket("lol");
        loginTicketMapper.updateStatus("lol",1);
    }
    @Test
    public void testSensitiveFilter(){

    }
}
