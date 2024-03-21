package com.longjiang.Controller;

import com.longjiang.Entity.User;
import com.longjiang.annotation.LoginRequired;
import com.longjiang.service.FollowService;
import com.longjiang.service.LikeService;
import com.longjiang.service.UserService;
import com.longjiang.util.BaseContext;
import com.longjiang.util.LongJiangConstant;
import com.longjiang.util.LongJiangUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@Slf4j
@RequestMapping("/user")
public class UserController  implements LongJiangConstant {
    @Value("${longjiang.path.upload}")
    private String uploadPath;
    @Value("${longjiang.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Autowired
    private UserService userService;
    @Autowired
    private BaseContext baseContext;
    @Autowired
    private LikeService likeService;
    @Autowired
    private FollowService followService;
    @GetMapping("/setting")
    @LoginRequired
    public String getSettingPage(){
        return "/site/setting";
    }
    @PostMapping("/upload")
    @LoginRequired
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(headerImage==null){
            model.addAttribute("error","你还没有上传图片");
            return "/site/setting";
        }
        String fileName=headerImage.getOriginalFilename();
        String suf = fileName.substring(fileName.lastIndexOf("."));
        if(StringUtils.isBlank(suf)){
            model.addAttribute("error","文件的格式不正确");
            return "/site/setting";
        }
        fileName= LongJiangUtil.getRandomUUID()+suf;
        File file=new File(uploadPath+"/"+fileName);
        try {
            //存储文件
            headerImage.transferTo(file);
        } catch (IOException e) {
            log.error("上传文件失败"+e.getMessage());
            throw  new RuntimeException("上传文件失败，服务器发生异常!",e);
        }
        User user = baseContext.getUser();
        String headerUrl=domain+contextPath+"/user/header/"+fileName;
        userService.updateHeader(user.getId(), headerUrl);
        return "redirect:/index";
    }
    @GetMapping("/header/{fileName}")
    public void getHeader(@PathVariable("fileName")String fileName, HttpServletResponse res){
        //服务器存放路径
        fileName=uploadPath+"/"+fileName;
        String suf=fileName.substring(fileName.lastIndexOf("."));
        res.setContentType("image/"+suf);
        try(
                OutputStream ops = res.getOutputStream();
                FileInputStream fis = new FileInputStream(fileName);
                ) {
            byte[] buffer=new byte[1024];
            int b=0;
            while((b=fis.read(buffer))!=-1){
                ops.write(buffer,0,b);
            }
            }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //个人主页
    @GetMapping("/profile/{userId}")
    public String getProfilePage(@PathVariable("userId")int userId,Model model){
        User user = userService.selectUserById(userId);
        if(user==null){
            throw new RuntimeException("用户不存在");
        }
        model.addAttribute("user",user);
        //用户点赞数
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount",likeCount);
        //关注数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount",followeeCount);
        //被关注数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount",followerCount);
        boolean hasFollowed=false;
        if(baseContext.getUser()!=null){
            hasFollowed=followService.hasFollowed(baseContext.getUser().getId(),ENTITY_TYPE_USER,userId);
        }
        model.addAttribute("hasFollowed",hasFollowed);
        return "/site/profile";
    }
}
