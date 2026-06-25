package com.example.aihelper.ai.service.impl;

import com.example.aihelper.ai.entity.po.Conversation;
import com.example.aihelper.ai.entity.po.UserMessage;
import com.example.aihelper.ai.entity.query.ConversationQuery;
import com.example.aihelper.ai.entity.query.UserMessageQuery;
import com.example.aihelper.ai.mappers.ConversationMapper;
import com.example.aihelper.ai.mappers.UserMessageMapper;
import com.example.aihelper.ai.service.ConversationService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
public class ConversationServiceImpl implements ConversationService {
    @Resource
    ConversationMapper<Conversation, ConversationQuery> conversationMapper;
    @Resource
    UserMessageMapper<UserMessage, UserMessageQuery> userMessageMapper;

    //获取会话列表
    @Override
    public List<Conversation> getConversationsByUserId(String userId) {
        ConversationQuery conversationQuery = new ConversationQuery();
        conversationQuery.setUserId(userId);
        return conversationMapper.selectList(conversationQuery);
    }

    //保存对话
    @Override
    public void saveConversation(Conversation conversation) {
        ConversationQuery conversationQuery = new ConversationQuery();
        BeanUtils.copyProperties(conversation,conversationQuery);
        conversationMapper.insertOrUpdate(conversationQuery);
    }

    //删除会话
    @Transactional
    @Override
    public boolean deleteConversation(String userId, Long conversationId) {
        //先删除当前用户会话的所有消息
        int msgRows  = userMessageMapper.deleteByConversationId(userId, conversationId);
        //再删除当前会话
        int convRows  = conversationMapper.deleteConversation(userId, conversationId);
        return convRows >0;
    }
}
