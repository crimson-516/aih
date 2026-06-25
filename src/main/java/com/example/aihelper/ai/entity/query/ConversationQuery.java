package com.example.aihelper.ai.entity.query;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConversationQuery {
    private Long id;
    private String userId;
    private String title;
    private LocalDateTime createTime;
}
