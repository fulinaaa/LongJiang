package com.longjiang.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.security.auth.Subject;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class SensitiveFilter {
    //替换符
    private static final String REAPLACEMENT="***";
    //根节点
    private TireNode rootNode=new TireNode();
    @PostConstruct
    public void init(){
        try(InputStream is = this.getClass().getClassLoader().getResourceAsStream("senstitive-words.txt");
            BufferedReader reader=new BufferedReader(new InputStreamReader(is));
        ){
            String keyword;
            while((keyword=reader.readLine())!=null){
                this.addKeyword(keyword);
            }
        }catch (IOException e){
            log.error("加载敏感词文件失败",e);
        }

    }
    //将一个敏感词添加到前缀树上去
    private void addKeyword(String keyword){
        TireNode temp=rootNode;
        for(int i=0;i<keyword.length();i++){
            char c=keyword.charAt(i);
            TireNode subNode = temp.getSubNode(c);
            if(subNode==null){
                //初始化子节点
                subNode=new TireNode();
                temp.addSubNode(c,subNode);
            }
            //让指针指向子节点，进入下一轮循环
            temp=subNode;
            if(i==keyword.length()-1){
                temp.setKeywordEnd(true);
            }
        }
    }
    //过滤敏感词
    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }
        //指针1
        TireNode temp=rootNode;
        //指针2
        int begin=0;
        //指针3
        int position=0;
        StringBuilder sb=new StringBuilder();
        while (position<text.length()){
            char c=text.charAt(position);
            if(isSymbol(c)){
                //若指针1处于根节点，将符号计入结果，让指针2向下一步
                if(temp==rootNode){
                    sb.append(c);
                    begin++;
                }
                //无论符号在开头或中间都向下走一步
                position++;
                continue;
            }
            //检查下一节点
            temp=temp.getSubNode(c);
            if(temp==null){
                //以begin开头的不是敏感词
                sb.append(text.charAt(begin));
                //进入下一个位置
                position=++begin;
                //重新指向根节点
                temp=rootNode;
            } else if (temp.isKeywordEnd()) {
                sb.append(REAPLACEMENT);
                begin=++position;
                temp=rootNode;
            }else {
                //检查下一个字符
                position++;
            }
        }
        //将最后一批字符计入结果
        sb.append(text.substring(begin));
        return sb.toString();
    }
    private boolean isSymbol(Character c){
        return !CharUtils.isAsciiAlphanumeric(c)&&(c<0x2E80||c>0x9FFF);
    }
    //前缀树
    private class TireNode{
        //关键词结束标识
        private boolean isKeywordEnd=false;

        //当前节点的子节点key是字符，value是下级节点
        private Map<Character,TireNode> map=new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }
        //添加子节点
        public void addSubNode(Character c,TireNode node){
            map.put(c,node);
        }
        //获取子节点
        public TireNode getSubNode(Character c){
            return map.get(c);
        }
    }
}
