package com.linghang.wusthelper.wustyjs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linghang.wusthelper.wustyjs.entity.Student;
import com.linghang.wusthelper.wustyjs.mapper.StudentMapper;
import com.linghang.wusthelper.wustyjs.service.IStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements IStudentService {

    @Autowired
    private StudentMapper studentMapper;

    // 根据学号查询学生
    @Override
    public Student getStudent(String username) {
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_num", username);
        return studentMapper.selectOne(queryWrapper);
    }
}
