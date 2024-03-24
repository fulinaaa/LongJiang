package com.longjiang.Controller;

import com.google.code.kaptcha.Producer;
import com.longjiang.Entity.User;
import com.longjiang.service.UserService;
import com.longjiang.util.LongJiangConstant;
import com.longjiang.util.LongJiangUtil;
import com.longjiang.util.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
@Slf4j
public class LoginController implements LongJiangConstant {
    @Autowired
    UserService userService;
    @Autowired
    private  Producer kaptchaProducer;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Autowired
    private RedisTemplate redisTemplate;
    @GetMapping("/register")
    public String getRegisterPage(){
        return "/site/register";
    }
    @PostMapping("/register")
    public String register(Model model, User user){
        Map<String, Object> map = userService.register(user);
        if(map==null||map.isEmpty()){
            model.addAttribute("msg","注册成功,已经向你的邮箱发送了激活邮件，请尽快激活");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            model.addAttribute("passswordMsg",map.get("passswordMsg"));
            return "/site/register";
        }
    }
    @GetMapping("/login")
    public String getLoginPage(){
        return "/site/login";
    }
    //生成验证码
    @GetMapping("/kaptcha")
    public void getKaptcha(HttpServletResponse res/*, HttpSession session*/) {
        String text=kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        //session.setAttribute("kaptcha",text);

        //验证码的归属
        String kaptchaOwner= LongJiangUtil.getRandomUUID();
        Cookie cookie=new Cookie("kaptchaOnwer",kaptchaOwner);
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        res.addCookie(cookie);

        //将验证码存入redis中
        String redisKey= RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(redisKey,text,60, TimeUnit.SECONDS);
        res.setContentType("image/png");
        try {
            ServletOutputStream os = res.getOutputStream();
            ImageIO.write(image,"png",os);
        } catch (IOException e) {
            log.error("响应验证码失败",e.getMessage());
        }
    }
    @GetMapping("/activation/{userId}/{code}")
    public String activation(Model model, @PathVariable("userId")int userId,@PathVariable("code")String code){
        int result=userService.activation(userId,code);
        if(result==ACTIVATION_SUCCESS){
            model.addAttribute("msg","激活成功");
            model.addAttribute("target","/login");
        }else if (result==ACTIVATION_REPEAT){
            model.addAttribute("msg","激活失败");
            model.addAttribute("target","/login");
        }else{
            model.addAttribute("msg","激活失败，你提供的激活码不正确");
            model.addAttribute("target","/index");
        }
        return "/site/operate-result";
    }
    @PostMapping("/login")
    public String login(String username,String password,String code,
                        boolean rememberme,Model model,/*HttpSession session,*/
                        HttpServletResponse res,@CookieValue("kaptchaOnwer")String kaptchaOnwer){
        /*String kaptcha=(String) session.getAttribute("kaptcha");*/
        String kaptcha=null;

        if(StringUtils.isNotBlank(kaptchaOnwer)){
            String redisKey=RedisKeyUtil.getKaptchaKey(kaptchaOnwer);
            kaptcha=(String) redisTemplate.opsForValue().get(redisKey);
        }
        if(StringUtils.isBlank(kaptcha)||StringUtils.isBlank(code)||!kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg","验证码不正确");
            return "/site/login";
        }
        //检查账号,密码
        int expiredSeconds=rememberme?REMEMBER_EXPIRED_SECONDS:DEAFULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        if(map.containsKey("ticket")){
            Cookie cookie=new Cookie("ticket",map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            res.addCookie(cookie);
            return "redirect:/index";
        }else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "/site/login";
        }
    }
    @GetMapping("/logout")
    public String logout(HttpServletRequest req,HttpServletResponse res){
        Cookie[] cookies = req.getCookies();
        for (Cookie cookie : cookies) {
            if(cookie.getName().equals("ticket")){
                String ticket=cookie.getValue();
                userService.logout(ticket);
                cookie.setMaxAge(0);
                cookie.setValue(null);
                res.addCookie(cookie);
            }
        }
        return "redirect:/login";
    }
}
