package com.longjiang.Entity;

import lombok.Data;

import java.util.Date;

@Data
public class User {
    private int id;
    private String username;
    private String password;
    private String email;
    private int type;
    private int status;
    private String activationCode;
    private String header_url;
    private Date creatTime;
}
