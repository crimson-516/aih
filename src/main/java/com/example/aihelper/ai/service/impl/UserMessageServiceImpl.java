package com.example.aihelper.ai.service.impl;

import com.example.aihelper.ai.entity.po.UserMessage;
import com.example.aihelper.ai.entity.query.UserMessageQuery;
import com.example.aihelper.ai.mappers.UserMessageMapper;
import com.example.aihelper.ai.service.UserMessageService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserMessageServiceImpl implements UserMessageService {
    @Resource
    private UserMessageMapper<UserMessage, UserMessageQuery> userMessageMapper;

    @Override
    public void saveMessageWithText(UserMessage userMessage) {
        // 强制设置时间，防止为空
        if (userMessage.getCreateTime() == null) {
            userMessage.setCreateTime(LocalDateTime.now());
        }

        UserMessageQuery userMessageQuery = new UserMessageQuery();
        BeanUtils.copyProperties(userMessage, userMessageQuery);

        System.out.println("用户ID：" + userMessageQuery.getUserId());
        System.out.println("内容：" + userMessageQuery.getContent());

        userMessageMapper.insertOrUpdate(userMessageQuery);
        System.out.println("========= 插入数据库成功 =========");
    }

    //查询这个对话里的所有聊天记录根据conversationId
    @Override
    public List<UserMessage> getHistoryByUserId(Long conversationId) {
        return userMessageMapper.getMessagesByConversationId(conversationId);
    }
}
