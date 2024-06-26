package com.longjiang.service.impl;

import com.longjiang.Entity.LoginTicket;
import com.longjiang.Entity.User;
import com.longjiang.mapper.LoginTicketMapper;
import com.longjiang.mapper.UserMapper;
import com.longjiang.service.UserService;
import com.longjiang.util.LongJiangConstant;
import com.longjiang.util.LongJiangUtil;
import com.longjiang.util.MailClient;
import com.longjiang.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService , LongJiangConstant  {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MailClient mailClient;
    /*@Autowired
    private LoginTicketMapper loginTicketMapper;*/

    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    RedisTemplate redisTemplate;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${longjiang.path.domain}")
    private String domain;
    @Override
    public User selectUserById(int id) {
        User user=getCache(id);
        if(user==null){
            user=initCache(id);
        }
        return user;
    }

    @Override
    public User selectUserByName(String name) {
        return userMapper.selectByName(name);
    }

    @Override
    public User selectUserByEmail(String email) {
        return selectUserByEmail(email);
    }
    @Override
    public Map<String,Object>register(User user){
        //注册用户
        HashMap<String, Object> map = new HashMap<>();
        if(user==null){
            throw new IllegalArgumentException("参数不能为空");
        }
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","账号不能为空");
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passswordMsg","密码不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空");
            return map;
        }
         if(userMapper.selectByName(user.getUsername())!=null){
             map.put("usernameMsg","账户已经存在");
             return map;
         }
         if(userMapper.selectByEmail(user.getEmail())!=null){
             map.put("emailMsg","邮箱已经存在");
             return map;
         }
         user.setSalt(LongJiangUtil.getRandomUUID().substring(0,5));
         user.setPassword(LongJiangUtil.md5(user.getPassword()+user.getSalt()));
         user.setType(0);
         user.setStatus(0);
         user.setActivationCode(LongJiangUtil.getRandomUUID());
         user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
         user.setCreateTime(new Date());
         userMapper.insertUser(user);
         //激活邮件
         Context context=new Context();
         context.setVariable("email",user.getEmail());
         String url=domain+contextPath+"/activation/"+user.getId()+"/"+user.getActivationCode();
         context.setVariable("url",url);
         String content= templateEngine.process("/mail/activation", context);
         mailClient.sendMail(user.getEmail(),"激活账号",content);
         return map;
    }
    @Override
    public int activation(int userId,String code){
        User user=userMapper.selectById(userId);
        if(user.getStatus()==1){
            return ACTIVATION_REPEAT;
        }else if(user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId,1);
            clearCache(userId);
            return ACTIVATION_SUCCESS;
        }else{
            return ACTIVATION_FAILURE;
        }
    }
    @Override
    public Map<String,Object> login(String username,String password,int expiredSeconds){
        HashMap<String, Object> map = new HashMap<>();
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        User user = userMapper.selectByName(username);
        if(user==null){
            map.put("usernameMsg","账户不存在");
            return map;
        }
        if(user.getStatus()==0){
            map.put("usernameMsg","该账号未激活");
            return map;
        }
        //对密码进行md5加密
        //password=LongJiangUtil.md5(password+user.getSalt());
        if(!user.getPassword().equals(password)){
            map.put("passwordMsg","密码不正确");
            return map;
        }
        LoginTicket loginTicket=new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(LongJiangUtil.getRandomUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+3600*60));
        //loginTicketMapper.insertLoginTicket(loginTicket);
        String ticketKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(ticketKey,loginTicket);
        map.put("ticket",loginTicket.getTicket());
        return map;
    }

    @Override
    public void logout(String ticket) {
        /*loginTicketMapper.updateStatus(ticket,1);*/
        String redisKey=RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1);

        redisTemplate.opsForValue().set(redisKey,loginTicket);
    }

    @Override
    public LoginTicket findLoginTicket(String ticket) {
        String redisKey=RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }

    @Override
    public int updateHeader(int userId, String headerUrl) {
        int rows = userMapper.updateHeader(userId, headerUrl);
        clearCache(userId);
        return rows;
    }
    //1.优先从缓存中取值
    private User getCache(int userId){
        String redisKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(redisKey);
    }
    //2.取不到初始化缓存数据
    private User initCache(int userId){
        User user=userMapper.selectById(userId);
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey,user,3600, TimeUnit.SECONDS);
        return user;
    }
    //3.数据变更时，删除缓存
    private void clearCache(int userId){
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }
    //根据用户查询用户权限
    public Collection<? extends GrantedAuthority> getAuthorities(int userId){
        User user = this.selectUserById(userId);
        List<GrantedAuthority> list=new ArrayList<>();
        list.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                switch (user.getType()){
                    case 1:return AUTHORITY_ADMIN;
                    case 2:return AUTHORITY_MODERATOR;
                    default:return AUTHORITY_USER;
                }
            }
        });
        return list;
    }
}
