package com.linghang.wusthelper.wustyjs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linghang.wusthelper.wustyjs.entity.Student;


public interface IStudentService extends IService<Student> {

    Student getStudent(String username);// 根据学号查询学生

}
