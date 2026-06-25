package com.example.aihelper.ai.entity.po;

import com.example.aihelper.ai.entity.enums.ResultEnum;
import lombok.Data;

import lombok.Data;

@Data
public class Result<T> {

    private Integer code;
    private String message;
    private T data;

    // 1. 无参数成功（最常用）
    public static <T> Result<T> success() {
        return success(null);
    }

    // 2. 带数据成功
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(ResultEnum.SUCCESS.getCode());
        result.setMessage(ResultEnum.SUCCESS.getMessage());
        result.setData(data);
        return result;
    }

    // 3. 通过枚举失败（推荐 99% 场景使用）
    public static <T> Result<T> error(ResultEnum resultEnum) {
        Result<T> result = new Result<>();
        result.setCode(resultEnum.getCode());
        result.setMessage(resultEnum.getMessage());
        return result;
    }

    // 4. 手动传码和消息（保留备用，不推荐常用）
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}
