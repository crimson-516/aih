package com.example.aihelper.ai.entity.factory;

import com.example.aihelper.ai.service.AiCodeHelperService;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Component
public class AiCodeHelperFactory {

    @Resource
    private ChatModel qwenModel;
    @Resource
    private ContentRetriever contentRetriever;
    @Resource
    private McpToolProvider mcpToolProvider;

    // 每个用户独立记忆
    private final ConcurrentHashMap<String, AiCodeHelperService> userAiMap = new ConcurrentHashMap<>();

    // 工厂方法：给每个 userId 创建独立 AI 实例
    public AiCodeHelperService getForUser(String userId) {
        return userAiMap.computeIfAbsent(userId, this::createNewAiInstance);
    }

    // 每个用户创建新的 AI + 独立记忆
    private AiCodeHelperService createNewAiInstance(String userId) {
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(20);

        return AiServices.builder(AiCodeHelperService.class)
                .chatModel(qwenModel)
                .chatMemory(chatMemory)  // 每个用户自己的记忆
                .contentRetriever(contentRetriever)
                .toolProvider(mcpToolProvider)
                .build();
    }
}
