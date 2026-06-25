package com.example.aihelper.ai.controller;

import com.example.aihelper.ai.entity.constants.RedisConstants;
import com.example.aihelper.ai.entity.enums.ResultEnum;
import com.example.aihelper.ai.entity.factory.AiCodeHelperFactory;
import com.example.aihelper.ai.entity.po.Conversation;
import com.example.aihelper.ai.entity.po.UserMessage;
import com.example.aihelper.ai.entity.po.Users;
import com.example.aihelper.ai.entity.vo.ResponseVO;
import com.example.aihelper.ai.entity.vo.UsersVO;
import com.example.aihelper.ai.exception.ResultException;
import com.example.aihelper.ai.service.*;
import com.example.aihelper.ai.utils.RedisUtil;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.service.SystemMessage;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static com.example.aihelper.ai.entity.enums.ResultEnum.SERVER_ERROR;

@RestController
@RequestMapping("/ai")
public class AIController extends BaseController {

    public static final String LOGIN_USER_KEY = RedisConstants.LOGIN_USER_KEY;

    @Resource
    UserService userService;

    @Resource
    RedisUtil<UsersVO> redisUtil;

    @Resource
    private AiCodeHelperFactory aiCodeHelperFactory;

    @Resource
    private StreamingChatModel qwenStreamingChatModel;

    @Resource
    private UserMessageService userMessageService;

    @Resource
    private ConversationService conversationService;

    //登录
    @PostMapping("/login")
    public ResponseVO login(@RequestBody Users users, HttpServletRequest request) {
        String ip = getClientIp(request);
        UsersVO userVO = userService.login(users, ip);
        return success(userVO);
    }

    //注册
    @PostMapping("/register")
    public ResponseVO register(@RequestBody Users users) {
        userService.register(users);
        return success(null);
    }

    // 消息聊天
    @PostMapping("/chat")
    public ResponseVO chat(HttpServletRequest request,
                           @RequestParam("content") String content,
                           @RequestParam("messageType") String messageType,
                           @RequestParam("conversationId") Long conversationId) {

        // 统一从工具方法获取用户，代码更干净、安全
        UsersVO usersVO = getUserIdFromRequest(request);
        if (usersVO == null) {
            throw new ResultException(ResultEnum.UNAUTHORIZED);
        }
        String userId = usersVO.getUserId();

        // 封装用户消息
        UserMessage userMessage = new UserMessage();
        userMessage.setUserId(userId);
        userMessage.setConversationId(conversationId);
        userMessage.setContent(content);
        userMessage.setIsAi(0);
        userMessage.setMessageType(messageType);
        userMessageService.saveMessageWithText(userMessage);

        // 封装AI消息（统一创建，避免重复代码）
        UserMessage aiMessage = new UserMessage();
        aiMessage.setUserId(userId);
        aiMessage.setConversationId(conversationId);
        aiMessage.setIsAi(1);
        aiMessage.setCreateTime(LocalDateTime.now());


        AiCodeHelperService userAiService = aiCodeHelperFactory.getForUser(userId);
        String aiResponse;
        switch (messageType) {
            case "text":
                aiResponse = userAiService.chat(content);
                aiMessage.setMessageType("text");
                aiMessage.setContent(aiResponse);
                break;
            case "image":
                aiResponse = userAiService.chat(content);
                aiMessage.setMessageType("image");
                aiMessage.setContent(aiResponse);
                break;
            case "file":
                aiResponse = userAiService.chat(content);
                aiMessage.setMessageType("file");
                aiMessage.setContent(aiResponse);
                break;
            case "video":
                aiResponse = userAiService.chat(content);
                aiMessage.setMessageType("video");
                aiMessage.setContent(aiResponse);
                break;
            default:
                return error(SERVER_ERROR);
        }

        // 统一保存AI消息
        userMessageService.saveMessageWithText(aiMessage);
        return success(aiMessage);
    }

    @GetMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatStream(@RequestParam("token") String token,
                                 @RequestParam("content") String content,
                                 @RequestParam("messageType") String messageType,
                                 @RequestParam("conversationId") Long conversationId) {
        SseEmitter emitter = new SseEmitter(0L);

        CompletableFuture.runAsync(() -> {
            UsersVO usersVO = getUserByToken(token);
            if (usersVO == null) {
                sendEvent(emitter, "error", "未登录或登录已过期");
                emitter.complete();
                return;
            }

            String userId = usersVO.getUserId();
            UserMessage userMessage = new UserMessage();
            userMessage.setUserId(userId);
            userMessage.setConversationId(conversationId);
            userMessage.setContent(content);
            userMessage.setIsAi(0);
            userMessage.setMessageType(messageType);
            userMessageService.saveMessageWithText(userMessage);

            StringBuilder fullText = new StringBuilder();
            sendEvent(emitter, "start", "");

            qwenStreamingChatModel.chat(content, new StreamingChatResponseHandler() {
                @Override
                public void onPartialResponse(String partialResponse) {
                    fullText.append(partialResponse);
                    sendEvent(emitter, "delta", partialResponse);
                }

                @Override
                public void onCompleteResponse(ChatResponse completeResponse) {
                    UserMessage aiMessage = new UserMessage();
                    aiMessage.setUserId(userId);
                    aiMessage.setConversationId(conversationId);
                    aiMessage.setIsAi(1);
                    aiMessage.setCreateTime(LocalDateTime.now());
                    aiMessage.setMessageType("text");
                    aiMessage.setContent(fullText.toString());
                    userMessageService.saveMessageWithText(aiMessage);

                    sendEvent(emitter, "done", fullText.toString());
                    emitter.complete();
                }

                @Override
                public void onError(Throwable error) {
                    error.printStackTrace();
                    sendEvent(emitter, "error", "生成失败，请稍后重试");
                    emitter.completeWithError(error);
                }
            });
        });

        return emitter;
    }

    // 获取【某个对话的消息列表】
    @GetMapping("/messages")
    public ResponseVO history(@RequestParam Long conversationId) {
        return success(userMessageService.getHistoryByUserId(conversationId));
    }

    // 获取【当前用户的所有对话列表】
    @GetMapping("/conversations")
    public ResponseVO conversations(HttpServletRequest request) {
        UsersVO usersVO = getUserIdFromRequest(request);
        if (usersVO == null) {
            return success(null);
        }
        return success(conversationService.getConversationsByUserId(usersVO.getUserId()));
    }

    // 新建对话
    @PostMapping("/conversation")
    public ResponseVO createConversation(HttpServletRequest request) {
        try {
            // 1. 获取登录用户
            UsersVO usersVO = getUserIdFromRequest(request);
            if (usersVO == null) {
                return error(401, "未登录");
            }

            // 2. 构建会话
            Conversation conversation = new Conversation();
            conversation.setUserId(usersVO.getUserId());
            conversation.setTitle("新对话");

            // 3. 保存（你现有的逻辑）
            conversationService.saveConversation(conversation);

            // 4.插入后重新查询，拿到带 ID 的会话
            List<Conversation> conversations = conversationService.getConversationsByUserId(usersVO.getUserId());
            if (conversations.isEmpty()) {
                return error(SERVER_ERROR);
            }

            // 5. 返回最新的一条（一定有 ID）
            return success(conversations.get(0));

        } catch (Exception e) {
            // 打印错误，方便排查
            e.printStackTrace();
            return error(SERVER_ERROR);
        }
    }

    // 删除会话
    @PostMapping("/delete")
    public ResponseVO deleteConversation(HttpServletRequest request,
                                         @RequestParam Long conversationId) {
        UsersVO usersVO = getUserIdFromRequest(request);
        if (usersVO == null) {
            throw new ResultException(ResultEnum.UNAUTHORIZED);
        }

        boolean flag = conversationService.deleteConversation(usersVO.getUserId(), conversationId);
        return success(flag);
    }

    //统一获取登录用户（直接返回 UsersVO）
    private UsersVO getUserIdFromRequest(HttpServletRequest request) {
        // 放行 OPTIONS 预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return null;
        }

        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            throw new ResultException(ResultEnum.UNAUTHORIZED);
        }

        String userKey = LOGIN_USER_KEY + token;
        return getUserByToken(token);
    }

    private UsersVO getUserByToken(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        String userKey = LOGIN_USER_KEY + token;
        return redisUtil.get(userKey);
    }

    private void sendEvent(SseEmitter emitter, String eventName, String data) {
        try {
            emitter.send(SseEmitter.event().name(eventName).data(data == null ? "" : data));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
    }

    //获取客户端真实IP
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }
        return ip;
    }
}
