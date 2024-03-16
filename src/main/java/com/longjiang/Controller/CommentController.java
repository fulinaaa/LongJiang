package com.longjiang.Controller;

import com.longjiang.Entity.Comment;
import com.longjiang.service.CommentService;
import com.longjiang.util.BaseContext;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.SimpleTimeZone;

@Controller
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private BaseContext baseContext;
    @PostMapping("/add/{discussPostId}")
    public String addComment(@PathVariable("discussPostId")int discussPostId, Comment comment){
        comment.setUserId(baseContext.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);
        return "redirect:/discuss/detail/"+discussPostId;
    }
}
