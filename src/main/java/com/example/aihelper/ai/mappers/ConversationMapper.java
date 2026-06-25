package com.example.aihelper.ai.mappers;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ConversationMapper<T,P> extends BaseMapper<T,P> {
    //删除当前用户会话
    int deleteConversation(String userId,Long conversationId);
}
