package com.linghang.wusthelper.wustyjs.enums;

/**
 * URL常量, 每年肯能官方换服务器, 检查ip是否和官网一致 https://ysxy.wust.edu.cn/
 */
public interface WustyjsUrl {

    // 获取验证码
    String CREATE_YZM_URL = "http://59.68.177.189/pyxx/PageTemplate/NsoftPage/yzm/createyzm.aspx";

    // 登录
    String LOGIN_URL = "http://59.68.177.189/pyxx/login.aspx";

    // 验证页面
    String DEFAULT_URL = "http://59.68.177.189/pyxx/Default.aspx";

    // 学生信息页面
    String BASIC_INFO_URL = "http://59.68.177.189/pyxx/loging.aspx";

    // 学院和专业
    String MAJOR_URL = "http://59.68.177.189/pyxx/topmenu.aspx";

    // 成绩, 页面上虽然提示需要评教, 但是实际上的html返回值有成绩
    String SCORES_URL = "http://59.68.177.189/pyxx/grgl/xskccjcx.aspx";

    // 今日课表 kcdate=2022-01-28&xh=202103703082
    String TODAY_COURSE_URL = "http://59.68.177.189/pyxx/App_Ajax/GetkcHandler.ashx?";

    // 修改密码
    String UPDATE_PASSWORD_RUL = "http://59.68.177.189/pyxx/grgl/xsmmxg.aspx";

    // 学历基本数据, 姓名, 性别, 出身日期,政治面貌,身份证号等等, 信息比较敏感
//    String MORE_INFO = " http://59.68.177.189/pyxx/grgl/xwsbhd_xlss.aspx";


}
