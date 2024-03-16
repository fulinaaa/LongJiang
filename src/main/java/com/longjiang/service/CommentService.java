package com.longjiang.service;

import com.longjiang.Entity.Comment;

import java.util.List;

public interface CommentService {
    public List<Comment> findCommentsByEntity(int entityType,int entityId,int offset,int limit );
    public int findCommentCount(int entityType,int entityId);
    public int addComment(Comment comment);
}
