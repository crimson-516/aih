package com.example.aihelper.ai.exception;

import com.example.aihelper.ai.entity.enums.ResultEnum;

public class ResultException extends RuntimeException {
    private Integer code;

    public ResultException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public ResultException(ResultEnum resultEnum) {
        super(resultEnum.getMessage());
        this.code = resultEnum.getCode();
    }

    public Integer getCode() {
        return code;
    }
}