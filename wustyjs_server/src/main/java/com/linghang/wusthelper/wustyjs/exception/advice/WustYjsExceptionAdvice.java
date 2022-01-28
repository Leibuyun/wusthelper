package com.linghang.wusthelper.wustyjs.exception.advice;

import com.linghang.wusthelper.wustyjs.exception.WustYjsException;
import com.linghang.wusthelper.wustyjs.response.ResponseCode;
import com.linghang.wusthelper.wustyjs.response.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * 全局异常处理
 */
@Slf4j
@RestControllerAdvice
public class WustYjsExceptionAdvice {

    /**
     * 服务端未处理的异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseVO handleException(Exception e) {
        log.error(e.getMessage());
        // todo: 输出到日志
        return ResponseVO.custom().error().build();
    }

    /**
     * 处理非法请求, 例如需要Post, 但是发送Get请求
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseVO handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("非法请求: " + e.getMessage());
        return ResponseVO.custom().code(ResponseCode.METHOD_ERROR).message(e.getMessage()).build();
    }

    /**
     * 处理请求参数为空
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseVO handHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("非法请求: " + e.getMessage());
        return ResponseVO.custom().code(ResponseCode.ARGS_NULL).message("数据为null").build();
    }

    /**
     * 处理数据校验异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseVO handleValidException(MethodArgumentNotValidException e) {
        log.error("数据不合法: " + e.getMessage());
        return ResponseVO.custom()
                .code(ResponseCode.VALIDATION_ERROR)
                .message("数据不合法")
                .data(e.getFieldErrors())
                .build();
    }

    /**
     * 处理自定义的异常, 例如网络请求失败, 验证码解析失败
     */
    @ExceptionHandler(WustYjsException.class)
    public ResponseVO handleWustYjwException(WustYjsException e) {
        log.error(e.getMessage());
        return ResponseVO.custom().code(e.getCode()).message(e.getMessage()).build();
    }

}
