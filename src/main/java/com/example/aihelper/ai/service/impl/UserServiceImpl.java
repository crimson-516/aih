package com.example.aihelper.ai.service.impl;

import com.example.aihelper.ai.entity.enums.ResultEnum;
import com.example.aihelper.ai.entity.po.Users;
import com.example.aihelper.ai.entity.query.UsersQuery;
import com.example.aihelper.ai.entity.vo.UsersVO;
import com.example.aihelper.ai.exception.ResultException;
import com.example.aihelper.ai.mappers.UsersMapper;
import com.example.aihelper.ai.service.UserService;
import com.example.aihelper.ai.utils.RedisUtil;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import java.util.UUID;

import static com.example.aihelper.ai.entity.constants.RedisConstants.*;


@Service
public class UserServiceImpl implements UserService {

    @Resource
    UsersMapper<Users, UsersQuery> usersMapper;

    @Resource
    RedisUtil<UsersVO> redisUtil;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Override
    public UsersVO login(Users users,String ip) {
        //防止缓存穿透
        String failKey = LOGIN_CACHE_NULL_FAIL_IP+ ip;
        String failCountStr = stringRedisTemplate.opsForValue().get(failKey);
        Integer failCount = failCountStr == null ? 0 : Integer.parseInt(failCountStr);
        if (failCount >= 5) {
            throw new ResultException(ResultEnum.TOO_MANY_ERRORS); // 提示：操作过于频繁，请稍后再试
        }
        // 1. 根据用户名查用户
        Users dbUser = usersMapper.selectByName(users.getUserName());
        if (dbUser == null) {
            increaseFailCount(failKey);
            throw new ResultException(ResultEnum.USER_NOT_EXIST); // 账号不存在
        }
        // 获取前端传过来的密码
        String inputPassword = users.getPassWord();
        String encryptedPassword = DigestUtils.md5DigestAsHex(inputPassword.getBytes());
        // 2. 校验密码
        if (!dbUser.getPassWord().equals(encryptedPassword)) {
            increaseFailCount(failKey); //密码错误，也必须失败计数加 1
            throw new ResultException(ResultEnum.LOGIN_ERROR); // 密码错误
        }
        // 4. 拼装返回 VO
        String token = UUID.randomUUID().toString().replace("-", "");
        UsersVO usersVO = new UsersVO();
        usersVO.setUserId(dbUser.getId());
        usersVO.setUserName(dbUser.getUserName());
        usersVO.setToken(token);
        // 3. 存入Redis
        String userKey = LOGIN_USER_KEY+token;
        redisUtil.setex(userKey, usersVO, LOGIN_USER_TTL);
        //登录成功，把该 IP 的错误计数删掉（清零解锁）
        stringRedisTemplate.delete(failKey);
        return usersVO;
    }

    @Override
    public void register(Users users) {
        Users existUser = usersMapper.selectByName(users.getUserName());
        System.out.println("existUser:" + existUser);
        if (existUser != null) {
            throw new ResultException(ResultEnum.USERNAME_EXIST);
        }
        UsersQuery usersQuery = new UsersQuery();
        //生成32位无横线随机字符串作为用户 ID
        String uuid = UUID.randomUUID().toString().replace("-", "");
        usersQuery.setId(uuid);
        usersQuery.setUserName(users.getUserName());
        String rawPassword = users.getPassWord();
        usersQuery.setPassWord(DigestUtils.md5DigestAsHex(rawPassword.getBytes()));
        this.usersMapper.insert(usersQuery);
    }

     // 辅助方法：让失败计数自增并设置 15 分钟过期
    private void increaseFailCount(String failKey) {
        // increment 是原子操作：如果 key 不存在，会自动创建并设为 1；如果存在则自增 1
        stringRedisTemplate.opsForValue().increment(failKey);
        // 设置 15 分钟过期时间。注意：每次输错都会刷新这 15 分钟的锁定/计数周期
        stringRedisTemplate.expire(failKey, CACHE_NULL_TTL, java.util.concurrent.TimeUnit.MINUTES);
    }
}
