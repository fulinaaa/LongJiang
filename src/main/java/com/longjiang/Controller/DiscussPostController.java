package com.longjiang.Controller;

import com.longjiang.Entity.Comment;
import com.longjiang.Entity.DiscussPost;
import com.longjiang.Entity.Page;
import com.longjiang.Entity.User;
import com.longjiang.service.CommentService;
import com.longjiang.service.DiscussPostService;
import com.longjiang.service.LikeService;
import com.longjiang.service.UserService;
import com.longjiang.util.BaseContext;
import com.longjiang.util.LongJiangConstant;
import com.longjiang.util.LongJiangUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements LongJiangConstant{
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private BaseContext baseContext;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private LikeService likeService;
    @PostMapping("/add")
    @ResponseBody
    public String addDiscussPost(String title,String content){
        User user= baseContext.getUser();
        if(user==null){
            return LongJiangUtil.getJSONString(403,"你还没有登录");
        }
        DiscussPost discussPost = new DiscussPost();
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setUserId(user.getId());
        discussPost.setCreateTime(new Date());
        discussPostService.addDiscussPost(discussPost);
        return LongJiangUtil.getJSONString(0,"发布成功");
    }
    @GetMapping("/detail/{discussPostId}")
    public String getDiscussPost(@PathVariable("discussPostId")int discussPostId, Model model,Page page){
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        //点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPostId);
        model.addAttribute("likeCount",likeCount);
        int likeStatus=baseContext.getUser()==null?0:likeService.findEntityLikeStatus(baseContext.getUser().getId(),ENTITY_TYPE_POST,discussPostId);
        model.addAttribute("likeStatus",likeStatus);
        model.addAttribute("post",post);
        User user = userService.selectUserById(post.getUserId());
        model.addAttribute("user",user);
        //评论分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/"+discussPostId);
        page.setRows(post.getCommentCount());
        //评论:给帖子的评论
        //回复:给评论的评论
        List<Comment> commentList = commentService.findCommentsByEntity(ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());
        List<Map<String,Object>>commentVoList=new ArrayList<>();
        if(commentList!=null){
            for (Comment comment : commentList) {
                //评论VO
                Map<String,Object>commentVo=new HashMap<>();
                //评论
                commentVo.put("comment",comment);
                //作者
                commentVo.put("user",userService.selectUserById(comment.getUserId()));
                //点赞数量与点赞状态
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeCount",likeCount);
                likeStatus=baseContext.getUser()==null?0:likeService.findEntityLikeStatus(baseContext.getUser().getId(),ENTITY_TYPE_COMMENT,comment.getId());
                commentVo.put("likeStatus",likeStatus);
                //回复列表
                List<Comment> replyList = commentService.findCommentsByEntity(ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                //回复VO列表
                List<Map<String,Object>>replyVoList=new ArrayList<>();
                if(replyList!=null){
                    for (Comment reply : replyList) {
                        Map<String,Object>replyVo=new HashMap<>();
                        //回复
                        replyVo.put("reply",reply);
                        //作者
                        replyVo.put("user",userService.selectUserById(reply.getUserId()));
                        //回复目标
                        User target=reply.getTargetId()==0?null:userService.selectUserById(reply.getTargetId());
                        //点赞数量与点赞状态
                        likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeCount",likeCount);
                        likeStatus=baseContext.getUser()==null?0:likeService.findEntityLikeStatus(baseContext.getUser().getId(),ENTITY_TYPE_COMMENT,reply.getId());
                        replyVo.put("likeStatus",likeStatus);
                        replyVo.put("target",target);
                        replyVo.put("likeCount",likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT,reply.getId()));
                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys",replyVoList);
                //回复数量
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount",replyCount);
                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("comments",commentVoList);
        return "/site/discuss-detail";
    }

}
