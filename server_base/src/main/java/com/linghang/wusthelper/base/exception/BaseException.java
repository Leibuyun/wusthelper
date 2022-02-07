package com.linghang.wusthelper.base.exception;


public class BaseException extends RuntimeException {

    private Integer code;

    public BaseException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
