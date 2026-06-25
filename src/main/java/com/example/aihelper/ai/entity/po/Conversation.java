package com.example.aihelper.ai.entity.po;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Conversation {
    private Long id;
    private String userId;
    private String title;
    private LocalDateTime createTime;
}
