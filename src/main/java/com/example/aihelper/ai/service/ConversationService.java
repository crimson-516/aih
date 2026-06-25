package com.example.aihelper.ai.service;

import com.example.aihelper.ai.entity.po.Conversation;

import java.util.List;

public interface ConversationService {
    //获取用户的所有对话
    List<Conversation> getConversationsByUserId(String userId);
    //新建对话
    void saveConversation(Conversation conversation);
    //删除会话
    boolean deleteConversation(String userId, Long conversationId);
}
