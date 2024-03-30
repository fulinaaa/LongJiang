package com.longjiang.Controller;

import com.longjiang.Entity.DiscussPost;
import com.longjiang.Entity.Page;
import com.longjiang.service.ElasticsearchService;
import com.longjiang.service.LikeService;
import com.longjiang.service.UserService;
import com.longjiang.util.LongJiangConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController implements LongJiangConstant {
    @Autowired
    private ElasticsearchService elasticsearchService;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;
    @GetMapping("/search")
    public String search(String keyword, Page page, Model model) throws IOException {
        long value=elasticsearchService.searchDiscussPostCount(keyword);
        page.setRows((int) value);
        page.setLimit(5);
        page.setPath("/search?keyword="+keyword);
        //搜索帖子
        List<DiscussPost> lists = elasticsearchService.searchDiscussPost(keyword, page.getOffset(), page.getLimit());
        List<Map<String,Object>> discussPost=new ArrayList<>();
        for (DiscussPost post : lists) {
           Map<String,Object>map=new HashMap<>();
           map.put("post",post);
           map.put("user",userService.selectUserById(post.getUserId()));
           map.put("likeCount",likeService.findEntityLikeCount(ENTITY_TYPE_POST,post.getId()));
           discussPost.add(map);
        }
        model.addAttribute("discussPosts",discussPost);
        model.addAttribute("keyword",keyword);
        return "/site/search";
    }
}
