package com.linghang.wusthelper.wustyjs.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "统一的返回结果")
public class ResponseVO {

    @ApiModelProperty(value = "返回的code, 与status不同")
    private Integer code;

    @ApiModelProperty(value = "提示信息")
    private String message;

    @ApiModelProperty(value = "返回的数据, 根据请求的接口自适应, 可能为null")
    private Object data;

    private ResponseVO(Integer code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    public static ResponseVO.Builder custom() {
        return new ResponseVO.Builder();
    }

    public static class Builder {
        private Integer code;
        private String message;
        private Object data;

        private Builder() {
            this.code = ResponseCode.DEFAULT;
        }

        public ResponseVO.Builder code(Integer code) {
            this.code = code;
            return this;
        }

        public ResponseVO.Builder message(String message) {
            this.message = message;
            return this;
        }

        public ResponseVO.Builder data(Object data) {
            this.data = data;
            return this;
        }

        public ResponseVO.Builder success() {
            this.code = ResponseCode.SUCCESS;
            this.message = "success";
            return this;
        }

        public ResponseVO.Builder error() {
            this.code = ResponseCode.ERROR;
            this.message = "error";
            return this;
        }

        public ResponseVO build() {
            return new ResponseVO(code, message, data);
        }
    }

}
