package com.longjiang;

import com.longjiang.Entity.DiscussPost;
import com.longjiang.Entity.User;
import com.longjiang.mapper.DiscussPostMapper;
import com.longjiang.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


@SpringBootTest
public class MapperTest {
    @Autowired(required = true)
    DiscussPostMapper discussPostMapper;
    @Autowired
    UserMapper userMapper;
    @Test
    public void test(){

    }
}
