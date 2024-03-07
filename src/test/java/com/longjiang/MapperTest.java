package com.longjiang;

import com.longjiang.Entity.DiscussPost;
import com.longjiang.mapper.DiscussPostMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class MapperTest {
    @Autowired(required = true)
    DiscussPostMapper discussPostMapper;
    @Test
    public void test(){
        int i = discussPostMapper.selectDiscussPostCount(0);
        System.out.println(i);
    }
}
