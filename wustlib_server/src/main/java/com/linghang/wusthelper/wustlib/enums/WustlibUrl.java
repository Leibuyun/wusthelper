package com.linghang.wusthelper.wustlib.enums;

public interface WustlibUrl {

    // 图书馆服务的URL
    String LIB_SERVICE_URL = "https://libsys.wust.edu.cn:443/meta-local/opac/cas/rosetta";

    // 认证tickets
    String TICKETS_URL = "https://auth.wust.edu.cn/lyuapServer/v1/tickets";

    // 获取当前借阅的所有书, 默认page=1&pageSize=100
    String CURRENT_BORROW_URL = "https://libsys.wust.edu.cn/meta-local/opac/users/loans?page=1&pageSize=100";

    // 获取历史借阅信息 默认page=1&pageSize=1000
    String HISTORY_URL = "https://libsys.wust.edu.cn/meta-local/opac/users/loan_hists?page=1&pageSize=1000";

    // 获取学生init信息的url, 可以用于验证cookie是否还生效
    String VALID_COOKIE_URL = "https://libsys.wust.edu.cn/meta-local/opac/sys/initInfo";

}
