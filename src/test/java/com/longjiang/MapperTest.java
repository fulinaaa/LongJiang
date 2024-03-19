package com.longjiang;

import com.longjiang.Entity.DiscussPost;
import com.longjiang.Entity.LoginTicket;
import com.longjiang.Entity.Message;
import com.longjiang.Entity.User;
import com.longjiang.mapper.DiscussPostMapper;
import com.longjiang.mapper.LoginTicketMapper;
import com.longjiang.mapper.MessageMapper;
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
    @Autowired
    MessageMapper messageMapper;
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
    public void testMessageMapper(){
        List<Message> messages = messageMapper.selectConversations(111, 0, 20);
        for (Message message : messages) {
            System.out.println(message);
        }
        int count = messageMapper.selectConversationCount(111);
        System.out.println(count);
        List<Message> list = messageMapper.selectLetters("111_112", 0, 10);
        for (Message message : list) {
            System.out.println(message);
        }
        int i = messageMapper.selectLetterCount("111_112");
        System.out.println(i);
        int unreadCount = messageMapper.selectLetterUnreadCount(131, "111_131");
        System.out.println(unreadCount);
    }
}
