package com.longjiang.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Map;

public class JWTUtil {
    static public String createJWT(String type, Map<String,Object> map){
        String jwt= Jwts.builder().signWith(SignatureAlgorithm.HS256,type)
                .addClaims(map)
                .setExpiration(new Date(System.currentTimeMillis()+3600*1000))
                .compact();
        return jwt;
    }
    static public Claims parseJwt(String type,String jwt){
        Claims body = Jwts.parser()
                .setSigningKey(type).parseClaimsJws(jwt).getBody();
        return body;
    }
}
