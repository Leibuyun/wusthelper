package com.linghang.wusthelper.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.linghang.wusthelper.base.entity.Student;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author lby
 * @since 2022-01-25
 */
@Mapper
public interface StudentMapper extends BaseMapper<Student> {

}
