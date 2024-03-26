package com.longjiang.Controller;

import com.longjiang.Entity.DiscussPost;
import com.longjiang.Entity.Page;
import com.longjiang.Entity.User;
import com.longjiang.service.DiscussPostService;
import com.longjiang.service.LikeService;
import com.longjiang.service.UserService;
import com.longjiang.util.LongJiangConstant;
import com.longjiang.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.*;
@Controller
public class HomeController {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;
    @GetMapping("/index")
    public String getIndexPage(Model model, Page page,@RequestParam(name="orderMode",defaultValue = "0") int orderMode){
        page.setRows(discussPostService.selectDiscussPostsCount(0));
        page.setPath("/index?orderMode="+orderMode);
        //同时返回帖子信息，与用户信息
        List<DiscussPost> list = discussPostService.selectDiscussPosts(0, page.getOffset(), page.getLimit(),orderMode);
        List<Map<String, Object>> discussPostVo=new ArrayList<>();
        if(list!=null){
            for (DiscussPost post : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("post",post);
                User user = userService.selectUserById(post.getUserId());
                map.put("user",user);
                Long like = likeService.findEntityLikeCount(LongJiangConstant.ENTITY_TYPE_POST,post.getId());
                map.put("like",like);
                discussPostVo.add(map);
            }
        }
        System.out.println(page.getCurrent());
        model.addAttribute("discussPostVo",discussPostVo);
        model.addAttribute("orderMode",orderMode);
        return "index";
    }
    @GetMapping("/error")
    public String getErrorPage(){
        return "/error/500";
    }
    @GetMapping("/denied")
    public String getDeniedPage(){
        return "/error/404";
    }
}
