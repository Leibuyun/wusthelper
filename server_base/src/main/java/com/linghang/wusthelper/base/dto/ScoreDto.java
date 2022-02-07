package com.linghang.wusthelper.base.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ApiModel(value="Score对象", description="学生的单门成绩信息")
public class ScoreDto {

    @ApiModelProperty(value = "id, 成绩表主键")
    private Long id;

    @ApiModelProperty(value = "课程名")
    private String name;

    @ApiModelProperty(value = "课程学分")
    private Double credit;

    @ApiModelProperty(value = "选修学期")
    private Integer term;

    @ApiModelProperty(value = "得分")
    private String point;

}
