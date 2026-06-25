package com.example.aihelper.ai.mappers;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMessageMapper<T,P> extends BaseMapper<T,P> {

    List<T> getMessagesByConversationId(Long conversationId);
    //先删除当前用户会话的所有消息
    int deleteByConversationId(String userId,Long conversationId);
}
