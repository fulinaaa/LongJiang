package com.longjiang.util;

public interface LongJiangConstant {
    //激活成功
    int ACTIVATION_SUCCESS=0;
    //重复激活
    int ACTIVATION_REPEAT=1;
    //激活失败
    int ACTIVATION_FAILURE=2;
    //默认状态的登录凭证的超时时间
    int DEAFULT_EXPIRED_SECONDS=3600*12;
    //记住状态下的登录凭证的超时实践
    int REMEMBER_EXPIRED_SECONDS=3600*12*100;
    /*
    * 实体类型:帖子
    * */
    int ENTITY_TYPE_POST=1;
    /*
    * 实体类型:评论
    * */
    int ENTITY_TYPE_COMMENT=2;
    /*
    * 实体类型:人
    * */
    int ENTITY_TYPE_USER=3;
    //评论
    String TOPIC_COMMENT="comment";
    //点赞
    String TOPIC_LIKE="like";
    //关注
    String TOPIC_FOLLOW="follow";
    int SYSTEM_USER_ID=1;
}
