package com.example.aihelper.ai.service;

import com.example.aihelper.ai.entity.po.Users;
import com.example.aihelper.ai.entity.vo.UsersVO;


public interface UserService {
    UsersVO login(Users users,String ip);
    void register(Users users);

}
