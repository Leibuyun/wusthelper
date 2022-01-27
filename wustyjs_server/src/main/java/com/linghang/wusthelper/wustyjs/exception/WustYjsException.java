package com.linghang.wusthelper.wustyjs.exception;


public class WustYjsException extends RuntimeException{

    private Integer code;

    public WustYjsException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
