package com.linghang.wusthelper.wustyjs.service;

import com.linghang.wusthelper.wustyjs.entity.Student;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lby
 * @since 2022-01-25
 */
public interface IStudentService extends IService<Student> {

    Student getStudent(String username);// 根据学号查询学生

}
