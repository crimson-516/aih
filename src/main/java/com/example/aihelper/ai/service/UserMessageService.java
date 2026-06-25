package com.example.aihelper.ai.service;

import com.example.aihelper.ai.entity.po.UserMessage;

import java.util.List;

public interface UserMessageService {
    void saveMessageWithText(UserMessage userMessage);
    List<UserMessage> getHistoryByUserId(Long conversationId);

}
