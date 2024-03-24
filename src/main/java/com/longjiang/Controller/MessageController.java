package com.longjiang.Controller;

import com.alibaba.fastjson.JSONObject;
import com.longjiang.Entity.Message;
import com.longjiang.Entity.Page;
import com.longjiang.Entity.User;
import com.longjiang.mapper.MessageMapper;
import com.longjiang.service.MessageService;
import com.longjiang.service.UserService;
import com.longjiang.util.BaseContext;
import com.longjiang.util.LongJiangConstant;
import com.longjiang.util.LongJiangUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

@Controller
public class MessageController implements LongJiangConstant {
    @Autowired
    private MessageService messageService;
    @Autowired
    private BaseContext baseContext;
    @Autowired
    private UserService userService;
    @GetMapping("/letter/list")
    public String getLetterList(Model model, Page page){
        User user=baseContext.getUser();
        //设置分页信息
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));
        //会话列表
        List<Message> conversationList =
                messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String,Object>>conversations=new ArrayList<>();
        if(conversationList!=null){
            for (Message message : conversationList) {
                Map<String,Object>map=new HashMap<>();
                map.put("conversation",message);
                map.put("letterCount",messageService.findLetterCount(message.getConversationId()));
                map.put("unreadCount", messageService.findLetterUnreadCount(user.getId(), message.getConversationId()));
                int targetId=user.getId()==message.getFromId()?message.getToId():message.getFromId();
                map.put("target",userService.selectUserById(targetId));
                conversations.add(map);
            }
        }
        model.addAttribute("conversations",conversations);
        //查询未读消息数量
        int letterUnreadCount=messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount",noticeUnreadCount);
        return "/site/letter";
    }
    private List<Integer> getLetterIds(List<Message>letter){
        List<Integer>ids=new ArrayList<>();
        if(letter!=null){
            for (Message message : letter) {
                if(message.getToId()==baseContext.getUser().getId()&&message.getStatus()==0){
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }
    @GetMapping("/letter/detail/{conversationId}")
    public String getLetterDetail(@PathVariable("conversationId")String conversationId, Page page, Model model){
        page.setLimit(5);
        page.setPath("/letter/detail/"+conversationId);
        page.setRows(messageService.findLetterCount(conversationId));
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String,Object>>letters=new ArrayList<>();
        if(letterList!=null){
            for (Message message : letterList) {
                Map<String,Object> map=new HashMap<>();
                map.put("letter",message);
                map.put("fromUser",userService.selectUserById(message.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters",letters);
        //私信目标
        model.addAttribute("target",getLetterTarget(conversationId));
        List<Integer> ids = getLetterIds(letterList);
        if(ids!=null){
            messageService.readMessage(ids);
        }
        return "/site/letter-detail";
    }
    private User getLetterTarget(String conversationId){
        String[] ids = conversationId.split("_");
        int d0=Integer.parseInt(ids[0]);
        int d1=Integer.parseInt(ids[1]);
        if(baseContext.getUser().getId()==d0){
            return userService.selectUserById(d1);
        }else{
            return userService.selectUserById(d0);
        }
    }
    @PostMapping("/letter/send")
    @ResponseBody
    public String sendLetter(String toName,String content){
        Integer.valueOf("abc");
        User user = userService.selectUserByName(toName);
        if(user==null){
            return LongJiangUtil.getJSONString(1,"用户目标不存在");
        }
        Message message = new Message();
        message.setFromId(baseContext.getUser().getId());
        message.setToId(user.getId());
        if(message.getFromId()<message.getToId()){
            message.setConversationId(message.getFromId()+"_"+message.getToId());
        }else{
            message.setConversationId(message.getToId()+"_"+message.getFromId());
        }
        message.setContent(content);
        message.setCreateTime(new Date());
        messageService.addMessage(message);
        return LongJiangUtil.getJSONString(0);
    }
    @GetMapping("/notice/list")
    public String getNoticeList(Model model){
        User user = baseContext.getUser();
        //查询评论类通知
        Message message=messageService.findLatestNotice(user.getId(),TOPIC_COMMENT);
        Map<String,Object>messageVo=new HashMap<>();
        messageVo.put("message",message);
        if(message!=null){
            String content= HtmlUtils.htmlUnescape(message.getContent());
            HashMap<String,Object> data = JSONObject.parseObject(content, HashMap.class);
            messageVo.put("user",userService.selectUserById((Integer)data.get("userId")));
            messageVo.put("entityType",data.get("entityType"));
            messageVo.put("entityId",data.get("entityId"));
            messageVo.put("postId",data.get("postId"));
            int count = messageService.findNoticeCount(user.getId(), TOPIC_COMMENT);
            messageVo.put("count",count);
            int unread = messageService.findNoticeUnreadCount(user.getId(), TOPIC_COMMENT);
            messageVo.put("unread",unread);
        }
        model.addAttribute("commentNotice",messageVo);
        //查询点赞类通知
         message=messageService.findLatestNotice(user.getId(),TOPIC_LIKE);
        messageVo=new HashMap<>();
        messageVo.put("message",message);
        if(message!=null){
            String content= HtmlUtils.htmlUnescape(message.getContent());
            HashMap<String,Object> data = JSONObject.parseObject(content, HashMap.class);
            messageVo.put("user",userService.selectUserById((Integer)data.get("userId")));
            messageVo.put("entityType",data.get("entityType"));
            messageVo.put("entityId",data.get("entityId"));
            messageVo.put("postId",data.get("postId"));
            int count = messageService.findNoticeCount(user.getId(), TOPIC_LIKE);
            messageVo.put("count",count);
            int unread = messageService.findNoticeUnreadCount(user.getId(), TOPIC_LIKE);
            messageVo.put("unread",unread);
        }
        model.addAttribute("likeNotice",messageVo);
        //查询关注类通知
        message=messageService.findLatestNotice(user.getId(),TOPIC_FOLLOW);
        messageVo=new HashMap<>();
        messageVo.put("message",message);
        if(message!=null){
            String content= HtmlUtils.htmlUnescape(message.getContent());
            HashMap<String,Object> data = JSONObject.parseObject(content, HashMap.class);
            messageVo.put("user",userService.selectUserById((Integer)data.get("userId")));
            messageVo.put("entityType",data.get("entityType"));
            messageVo.put("entityId",data.get("entityId"));
            int count = messageService.findNoticeCount(user.getId(), TOPIC_FOLLOW);
            messageVo.put("count",count);
            int unread = messageService.findNoticeUnreadCount(user.getId(), TOPIC_FOLLOW);
            messageVo.put("unread",unread);
        }
        model.addAttribute("followNotice",messageVo);
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(),null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount",noticeUnreadCount);
        return "/site/notice";
    }
    @GetMapping("/notice/detail/{topic}")
    public String getNoticeDetail(@PathVariable("topic")String topic,Page page ,Model model){
        User user = baseContext.getUser();
        page.setLimit(5);
        page.setPath("/notice/detail/"+topic);
        page.setRows(messageService.findNoticeCount(user.getId(),topic));
        List<Message> noticeList = messageService.findNotices(user.getId(), topic, page.getOffset(), page.getLimit());
        List<Map<String,Object>> noticeVoList=new ArrayList<>();
        if(noticeList!=null){
            for (Message notice : noticeList) {
                HashMap<String, Object> map = new HashMap<>();
                //通知
                map.put("notice",notice);
                //内容
                String content = HtmlUtils.htmlUnescape(notice.getContent());
                Map<String,Object>data=JSONObject.parseObject(content, HashMap.class);
                map.put("user",userService.selectUserById((Integer) data.get("userId")));
                map.put("entityType",data.get("entityType"));
                map.put("entityId",data.get("entityId"));
                map.put("postId",data.get("postId"));
                //通知的作者
                map.put("fromUser",userService.selectUserById(notice.getFromId()));
                noticeVoList.add(map);
            }
        }
        model.addAttribute("notices",noticeVoList);
        //设置已读
        List<Integer> ids=getLetterIds(noticeList);
        if(!ids.isEmpty()){
            messageService.readMessage(ids);
        }
        return "/site/notice-detail";
    }
}
