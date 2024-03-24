package com.longjiang.Controller;

import com.longjiang.Entity.Comment;
import com.longjiang.Entity.DiscussPost;
import com.longjiang.Entity.Event;
import com.longjiang.Event.EventProducer;
import com.longjiang.service.CommentService;
import com.longjiang.service.DiscussPostService;
import com.longjiang.util.BaseContext;
import com.longjiang.util.LongJiangConstant;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.annotation.Target;
import java.util.Date;
import java.util.SimpleTimeZone;

@Controller
@RequestMapping("/comment")
public class CommentController implements LongJiangConstant {
    @Autowired
    private CommentService commentService;
    @Autowired
    private BaseContext baseContext;
    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private DiscussPostService discussPostService;
    @PostMapping("/add/{discussPostId}")
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public String addComment(@PathVariable("discussPostId")int discussPostId, Comment comment)  {
        comment.setUserId(baseContext.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);
        //触发评论事件
        Event event=new Event().setTopic(TOPIC_COMMENT).setUserId(baseContext.getUser().getId())
                .setEntityId(comment.getEntityId()).setEntityType(comment.getEntityType())
                .setData("postId",discussPostId);
        if(comment.getEntityType()==ENTITY_TYPE_POST){
            DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }else if(comment.getEntityType()==ENTITY_TYPE_COMMENT){
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }

        eventProducer.fireEvent(event);

        return "redirect:/discuss/detail/"+discussPostId;
    }
}
