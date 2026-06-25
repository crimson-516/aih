package com.example.aihelper;

import com.example.aihelper.ai.entity.factory.AiCodeHelperFactory;
import com.example.aihelper.ai.entity.po.Conversation;
import com.example.aihelper.ai.entity.po.Users;
import com.example.aihelper.ai.entity.query.UserMessageQuery;
import com.example.aihelper.ai.entity.query.UsersQuery;
import com.example.aihelper.ai.entity.vo.UsersVO;
import com.example.aihelper.ai.mappers.ConversationMapper;
import com.example.aihelper.ai.mappers.UserMessageMapper;
import com.example.aihelper.ai.service.UserService;
import com.example.aihelper.ai.service.impl.AiCodeHelper;
//import dev.langchain4j.data.message.UserMessage;
import com.example.aihelper.ai.entity.po.UserMessage;
import com.example.aihelper.ai.service.AiCodeHelperService;
import com.example.aihelper.ai.service.ConversationService;
import com.example.aihelper.ai.service.UserMessageService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
class AiHelperApplicationTests {

    @Resource
    UserService userService;

    @Resource
    private AiCodeHelperFactory aiCodeHelperFactory;

    @Resource
    UserMessageService userMessageService;

    @Resource
    UserMessageMapper<UserMessage, UserMessageQuery> userMessageMapper;

    @Resource
    ConversationService conversationService;

    //基本测试
    @Test
    void contextLoads() {
        AiCodeHelperService aiService = aiCodeHelperFactory.getForUser("test_user_001");
        String chat = aiService.chat("你好，你是谁？能干什么？");
        System.out.println("【基本测试返回】" + chat);
    }

    //测试ai会话记忆
    @Test
    void chatWithMemory(){
        // 💡 修复点 3：替换掉彻底不存在的旧变量名 aiCodeHelperServiceFactory
        AiCodeHelperService aiService = aiCodeHelperFactory.getForUser("test_user_001");

        String chat = aiService.chat("你好，我叫张三");
        System.out.println("第一次对话：" + chat);

        String chat2 = aiService.chat("我刚刚说我叫什么？");
        System.out.println("带记忆对话（预期应该知道是张三）：" + chat2);
    }
    //测试提示词，角色设定
    @Test
    void chatWithPropmt(){
        AiCodeHelperService aiService = aiCodeHelperFactory.getForUser("test_user_001");
        String chat = aiService.chat("你好你的主人是谁？介绍一下");
        System.out.println("【角色设定测试】" + chat);
    }
    //测试联网查询
    @Test
    void chatByWeb(){
        AiCodeHelperService aiService = aiCodeHelperFactory.getForUser("test_user_001");

        String chat = aiService.chat("今天是几几年几月几号？");
        System.out.println("【联网时间测试】" + chat);

        String chat1 = aiService.chat("今日西安天气");
        System.out.println("【联网天气测试】" + chat1);
    }
    //============== 增删改查====================

    @Test
    void testRegister(){
        Users users = new Users();
        users.setUserName("yy");
        users.setPassWord("123456");
        userService.register(users);
    }
    @Test
    void testLogin(){
        Users users = new Users();
        users.setUserName("yy");
        users.setPassWord("123456");
        UsersVO usersVO = userService.login(users,"192.168.0.1");
        System.out.println("userId:"+usersVO.getUserId());
        System.out.println("userName:"+usersVO.getUserName());
    }
    @Test
    void testInsertMessageByMapper() {
        UserMessageQuery userMessageQuery = new UserMessageQuery();
        userMessageQuery.setUserId("84ca042debb54fd3a99ca244b0555c41");
        userMessageQuery.setMessageType("text");
        userMessageQuery.setContent("你是谁？");
        userMessageQuery.setIsAi(0);
        userMessageQuery.setConversationId(5L);
        userMessageQuery.setCreateTime(LocalDateTime.now());
        userMessageMapper.insertOrUpdate(userMessageQuery);
    }

    @Test
    void testInsertMessageByService() {
        UserMessage userMessage = new UserMessage();
        userMessage.setUserId("9051e5955306423f91467604c06ddce8");
        userMessage.setMessageType("text");
        userMessage.setContent("啦啦啦啦？");
        userMessage.setIsAi(0);
        userMessage.setConversationId(5L);
        userMessage.setCreateTime(LocalDateTime.now());
        userMessageService.saveMessageWithText(userMessage);
    }

    @Test
    void testSelectChatMessageListByMapper() {
        UserMessageQuery userMessageQuery = new UserMessageQuery();
        userMessageQuery.setUserId("9051e5955306423f91467604c06ddce8");
        List<UserMessage> userMessages = userMessageMapper.selectList(userMessageQuery);
        for (UserMessage userMessage : userMessages) {
            System.out.println(userMessage);
        }
    }

    @Test
    void testSelectChatMessageListByService() {
        List<UserMessage> userMessages = userMessageService.getHistoryByUserId(1L);
        for (UserMessage userMessage : userMessages) {
            System.out.println("userMessage:" + userMessage);
        }
    }

    @Test
    void testInsertConversationByService() {
        Conversation conversation = new Conversation();
        conversation.setUserId("9051e5955306423f91467604c06ddce8");
        conversation.setTitle(null);
        conversation.setCreateTime(LocalDateTime.now());
        conversationService.saveConversation(conversation);
    }

    @Test
    void testSelectConversationByService() {

        List<Conversation> conversations = conversationService.getConversationsByUserId("9051e5955306423f91467604c06ddce8");
        for (Conversation conversation : conversations) {
            System.out.println("conversation:" + conversation);
        }
    }
}