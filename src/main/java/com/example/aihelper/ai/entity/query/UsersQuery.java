package com.example.aihelper.ai.entity.query;

import lombok.Data;

@Data
public class UsersQuery {
    // 主键id
    private String id;
    // 用户名
    private String userName;
    // 密码
    private String passWord;

    // 模糊查询字段
    private String userNameFuzzy;
}
