package com.linghang.wusthelper.base.response;

public interface ResponseCode {

    Integer DEFAULT = 70000;

    Integer SUCCESS = 70000;

    Integer TokenExpired = 70001;   // token过期

    Integer PASSWORD_NOT_MATCH = 70002; // 账号密码不匹配

    Integer LOGIN_ERROR = 70003;    // 图片下载失败, 验证码解析失败, 网络异常

    Integer ERROR = 70005;          // Exception 其它未处理的异常

    Integer VALIDATION_ERROR = 70006;// 数据校验不合法

    Integer METHOD_ERROR = 70007;   // 请求方式非法

    Integer ARGS_NULL = 70008;      // 参数为空

    Integer TOKEN_ERROR = 70009;    // token非法

}

