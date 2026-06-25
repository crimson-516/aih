package com.example.aihelper.ai.entity.query;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserMessageQuery {
    private Long id;
    private String userId;
    private Long conversationId;
    private String messageType;
    private String content;
    private int isAi;
    private LocalDateTime createTime;
    // 模糊查询字段
    private String contentFuzzy;
}
