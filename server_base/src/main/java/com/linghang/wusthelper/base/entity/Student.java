package com.linghang.wusthelper.base.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("student")
@ApiModel(value="Student对象", description="返回学生的基本信息")
public class Student implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "学生表主键")
    private Long id;

    @ApiModelProperty(value = "学号")
    @TableField("student_num")
    private String studentNum;

    @ApiModelProperty(value = "密码")
    @TableField("password")
    @JsonIgnore
    private String password;

    @ApiModelProperty(value = "姓名")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "学位")
    @TableField("degree")
    private String degree;

    @ApiModelProperty(value = "导师姓名")
    @TableField("tutor_name")
    private String tutorName;

    @ApiModelProperty(value = "学院")
    @TableField("academy")
    private String academy;

    @ApiModelProperty(value = "专业")
    @TableField("specialty")
    private String specialty;

    @ApiModelProperty(value = "年级")
    @TableField("grade")
    private Integer grade;

    @ApiModelProperty(value = "头像")
    @TableField("avatar")
    private String avatar;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "修改时间/最后一次登录时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 用于注册
    public Student(String studentNum, String password){
        this.studentNum = studentNum;
        this.password = password;
    }

}
