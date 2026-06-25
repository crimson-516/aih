package com.example.aihelper.ai.config;

import com.example.aihelper.ai.entity.vo.UsersVO;
import com.example.aihelper.ai.interceptor.LoginInterceptor;
import com.example.aihelper.ai.utils.RedisUtil;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebMvcConfig implements WebMvcConfigurer {
    private RedisUtil<UsersVO> redisUtil;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor(redisUtil))
                .addPathPatterns("/**") //默认拦截所有请求
                .excludePathPatterns(
                        "/ai/login",
                        "/ai/register",
                        "error");
    }
}
