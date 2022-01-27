package com.linghang.wusthelper.wustyjs.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value = "登录请求的参数")
public class LoginDto {

    @NotBlank(message = "学号不能为空")
    @ApiModelProperty(value = "学号, 对应Student表中的的studentNum", required = true, example = "202103703082")
    private String username;

    @NotBlank(message = "密码不能为空")
    @ApiModelProperty(value = "密码", required = true, example = "pjw202103703082")
    private String password;

}
