package com.example.aihelper.ai.entity.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserMessage{
    private Long id;
    private String userId;
    private Long conversationId;
    private String messageType;
    private String content;
    private Integer isAi;
    private LocalDateTime createTime;
}
