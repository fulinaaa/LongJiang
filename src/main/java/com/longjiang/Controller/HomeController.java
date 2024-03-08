package com.longjiang.Controller;

import com.longjiang.Entity.DiscussPost;
import com.longjiang.Entity.Page;
import com.longjiang.Entity.User;
import com.longjiang.service.DiscussPostService;
import com.longjiang.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


import java.util.*;
@Controller
public class HomeController {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;
    @GetMapping("/index")
    public String getIndexPage(Model model, Page page){
        page.setRows(discussPostService.selectDiscussPostsCount(0));
        page.setPath("/index");
        //同时返回帖子信息，与用户信息
        List<DiscussPost> list = discussPostService.selectDiscussPosts(0, page.getOffset(), page.getLimit());
        List<Map<String, Object>> discussPostVo=new ArrayList<>();
        if(list!=null){
            for (DiscussPost post : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("post",post);
                User user = userService.selectUserById(post.getUserId());
                map.put("user",user);
                discussPostVo.add(map);
            }
        }
        System.out.println(page.getCurrent());
        model.addAttribute("discussPostVo",discussPostVo);
        return "index";
    }

}
