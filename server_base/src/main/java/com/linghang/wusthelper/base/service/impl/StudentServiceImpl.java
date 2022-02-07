package com.linghang.wusthelper.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linghang.wusthelper.base.entity.Student;
import com.linghang.wusthelper.base.mapper.StudentMapper;
import com.linghang.wusthelper.base.service.IStudentService;
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
