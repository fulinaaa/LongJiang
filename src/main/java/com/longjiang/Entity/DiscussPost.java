package com.longjiang.Entity;

import lombok.Data;

import java.util.Date;
@Data
public class DiscussPost {
    private int id;
    private int userId;
    private String title;
    private String content;
    private int type;
    private Date creatTime;
    private int commentCount;
    private double score;
}
