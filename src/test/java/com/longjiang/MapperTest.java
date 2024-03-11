package com.longjiang;

import com.longjiang.Entity.DiscussPost;
import com.longjiang.Entity.User;
import com.longjiang.mapper.DiscussPostMapper;
import com.longjiang.mapper.UserMapper;
import com.longjiang.util.JWTUtil;
import com.longjiang.util.MailClient;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;


@SpringBootTest
public class MapperTest {
    @Test
    public void mytest(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("你好吗","我很好");
        String longJiang = JWTUtil.createJWT("LongJiang", map);
        Claims claims = JWTUtil.parseJwt("LongJiang", longJiang);
        claims.forEach((o,s)-> System.out.println(o+": "+s));
    }
}
