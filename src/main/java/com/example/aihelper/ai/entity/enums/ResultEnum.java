package com.example.aihelper.ai.entity.enums;

public enum ResultEnum {

    // 成功
    SUCCESS(200, "操作成功"),

    // 客户端错误
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录，请先登录"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),

    // 服务器错误
    SERVER_ERROR(500, "服务器内部错误"),

    // 自定义业务错误
    USERNAME_EXIST(501, "用户名已存在"),
    LOGIN_ERROR(502, "用户名或密码错误"),
    USER_NOT_EXIST(503, "用户不存在"),

    // 密码错误次数过多
    TOO_MANY_ERRORS(504, "密码错误次数过多，账号已锁定，请15分钟后再试"),

    // 后续拦截器可能要用的“登录过期”补上
    LOGIN_EXPIRED(505, "登录已过期，请重新登录");

    // 状态码
    private final Integer code;
    // 提示信息
    private final String message;

    ResultEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
