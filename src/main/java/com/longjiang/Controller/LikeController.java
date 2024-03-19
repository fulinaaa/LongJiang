package com.longjiang.Controller;

import com.longjiang.Entity.User;
import com.longjiang.annotation.LoginRequired;
import com.longjiang.service.LikeService;
import com.longjiang.util.BaseContext;
import com.longjiang.util.LongJiangConstant;
import com.longjiang.util.LongJiangUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController {
    @Autowired
    private LikeService likeService;
    @Autowired
    private BaseContext baseContext;
    @PostMapping("/like")
    @ResponseBody
    @LoginRequired
    public String like(int entityType,int entityId,int entityUserId){
        User user = baseContext.getUser();
        likeService.like(user.getId(),entityType,entityId,entityUserId);
        //数量
        long likeCount=likeService.findEntityLikeCount(entityType,entityId);
        //状态
        int likeStatus=likeService.findEntityLikeStatus(user.getId() ,entityType,entityId);
        Map<String,Object>map=new HashMap<>();
        map.put("likeCount",likeCount);
        map.put("likeStatus",likeStatus);
        return LongJiangUtil.getJSONString(0,null,map);
    }
}
