package com.example.aihelper.ai.controller;

import com.example.aihelper.ai.entity.enums.ResultEnum;
import com.example.aihelper.ai.entity.po.Result;
import com.example.aihelper.ai.entity.vo.ResponseVO;
import org.springframework.web.bind.annotation.RestController;

public class BaseController {

    protected static final String STATUS_SUCCESS = "success";
    protected static final String STATUS_ERROR = "error";

    // 成功返回
    public <T> ResponseVO<T> success(T data) {
        ResponseVO<T> vo = new ResponseVO<>();
        vo.setStatus(STATUS_SUCCESS);
        vo.setCode(ResultEnum.SUCCESS.getCode());
        vo.setInfo(ResultEnum.SUCCESS.getMessage());
        vo.setData(data);
        return vo;
    }

    // 成功无数据
    public <T> ResponseVO<T> success() {
        ResponseVO<T> vo = new ResponseVO<>();
        vo.setStatus(STATUS_SUCCESS);
        vo.setCode(ResultEnum.SUCCESS.getCode());
        vo.setInfo(ResultEnum.SUCCESS.getMessage());
        return vo;
    }

    // 失败返回
    public <T> ResponseVO<T> error(Integer code, String info) {
        ResponseVO<T> vo = new ResponseVO<>();
        vo.setStatus(STATUS_ERROR);
        vo.setCode(code);
        vo.setInfo(info);
        return vo;
    }

    // 失败枚举
    public <T> ResponseVO<T> error(ResultEnum resultEnum) {
        ResponseVO<T> vo = new ResponseVO<>();
        vo.setStatus(STATUS_ERROR);
        vo.setCode(resultEnum.getCode());
        vo.setInfo(resultEnum.getMessage());
        return vo;
    }

    // token = userId
    protected Integer getUserId(String token) {
        if (token == null || token.isEmpty()) return null;
        return Integer.valueOf(token);
    }
}