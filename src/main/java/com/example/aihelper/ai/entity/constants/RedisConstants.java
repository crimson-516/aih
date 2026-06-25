package com.example.aihelper.ai.entity.constants;

public class RedisConstants {

    public static final String LOGIN_USER_KEY = "login:token:";   // 用户登录key
    public static final Long LOGIN_USER_TTL = 43200L;   // 登录用户 30天过期
    // 防止登录缓存穿透
    public static final String LOGIN_CACHE_NULL_FAIL_IP = "login:fail:ip:";
    public static final Long CACHE_NULL_TTL = 15L;
}
