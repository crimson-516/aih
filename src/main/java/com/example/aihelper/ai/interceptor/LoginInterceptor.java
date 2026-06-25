package com.example.aihelper.ai.interceptor;

import com.example.aihelper.ai.entity.enums.ResultEnum;
import com.example.aihelper.ai.entity.vo.UsersVO;
import com.example.aihelper.ai.exception.ResultException;
import com.example.aihelper.ai.utils.RedisUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.example.aihelper.ai.entity.constants.RedisConstants.LOGIN_USER_KEY;
import static com.example.aihelper.ai.entity.constants.RedisConstants.LOGIN_USER_TTL;

public class LoginInterceptor implements HandlerInterceptor {

    private final RedisUtil<UsersVO> redisUtil;

    public LoginInterceptor(RedisUtil<UsersVO> redisUtil) {
        this.redisUtil = redisUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        if (!StringUtils.hasText(token)) {
            throw new ResultException(ResultEnum.UNAUTHORIZED); // 提示：未登录，请先登录
        }
        String userKey = LOGIN_USER_KEY+token;
        UsersVO usersVO = redisUtil.get(userKey);
        if (usersVO==null){
            throw new ResultException(ResultEnum.UNAUTHORIZED); // 提示：登录已过期或无效 token
        }
        //续期用户有效时间
        redisUtil.expire(userKey, LOGIN_USER_TTL);
        return true;
    }

}
