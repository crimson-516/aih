package com.example.aihelper.ai.exception;

import com.example.aihelper.ai.entity.enums.ResultEnum;
import com.example.aihelper.ai.entity.po.Result;
import com.example.aihelper.ai.entity.vo.ResponseVO;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResultException.class)
    public ResponseVO<?> handleResultException(ResultException e) {
        ResponseVO<?> vo = new ResponseVO<>();
        vo.setStatus("error");
        vo.setCode(e.getCode() != null ? e.getCode() : ResultEnum.SERVER_ERROR.getCode());
        vo.setInfo(e.getMessage());
        vo.setData(null);
        return vo;
    }

    @ExceptionHandler(Exception.class)
    public ResponseVO<?> handleException(Exception e) {
        ResponseVO<?> vo = new ResponseVO<>();
        vo.setStatus("error");
        vo.setCode(ResultEnum.SERVER_ERROR.getCode());
        vo.setInfo(ResultEnum.SERVER_ERROR.getMessage());
        vo.setData(null);
        return vo;
    }
}