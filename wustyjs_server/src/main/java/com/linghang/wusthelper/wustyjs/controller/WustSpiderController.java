package com.linghang.wusthelper.wustyjs.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.linghang.wusthelper.wustyjs.dto.LoginDto;
import com.linghang.wusthelper.wustyjs.response.ResponseVO;
import com.linghang.wusthelper.wustyjs.service.SpiderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Pattern;


// eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJwYXNzd29yZCI6InBqdzIwMjEwMzcwMzA4MiIsImNvb2tpZSI6IkFTUC5ORVRfU2Vzc2lvbklkPWoya3dnaHliMGkxZXdpbGFtemZtdW8ycTsiLCJleHAiOjE2NDMxMzY4MjIsInVzZXJuYW1lIjoiMjAyMTAzNzAzMDgyIn0.Oh5NOq637LpAdMTXwxfl3tGBfNzwujt6Zp22_zLzOcA

@RestController
@RequestMapping("/wustyjs")
@Api(tags = {"爬虫服务"})
public class WustSpiderController {

    @Autowired
    private SpiderService spiderService;

    @PostMapping("login")
    @ApiOperation(value = "登录请求", notes = "获取wustyjsToken")
    @ApiResponses({
            @ApiResponse(code = 70000, message = "正常返回, 默认成功"),
            @ApiResponse(code = 70002, message = "账号密码不匹配"),
            @ApiResponse(code = 70003, message = "网络异常. "),
            @ApiResponse(code = 70005, message = "后端服务有未捕获的异常. 70006以上均为非法请求或者参数校验不合法"),
    })
    public ResponseVO login(@Validated @RequestBody LoginDto user) {
        return spiderService.login(user.getUsername(), user.getPassword());
    }

    @GetMapping("student")
    @ApiOperation(value = "获取学生信息", notes = "需要携带请求头wustyjsToken, data为对象")
    @ApiResponses({
            @ApiResponse(code = 70000, message = "正常返回, 默认成功"),
            @ApiResponse(code = 70001, message = "wustyjsToken过期"),
            @ApiResponse(code = 70005, message = "后端服务有未捕获的异常. 70006以上均为非法请求或者参数校验不合法"),
    })
    public ResponseVO getStudentInfo(@RequestHeader(value = "wustyjsToken") String wustyjsToken) {
        return spiderService.getStudent(wustyjsToken);
    }

    @GetMapping("scores")
    @ApiOperation(value = "获取成绩", notes = "需要携带请求头wustyjsToken, data为列表[]")
    @ApiResponses({
            @ApiResponse(code = 70000, message = "正常返回, 默认成功"),
            @ApiResponse(code = 70001, message = "wustyjsToken过期"),
            @ApiResponse(code = 70005, message = "后端服务有未捕获的异常. 70006以上均为非法请求或者参数校验不合法"),
    })
    public ResponseVO getScores(@RequestHeader(value = "wustyjsToken") String wustyjsToken) {
        return spiderService.getScores(wustyjsToken);
    }

    // 修改密码, 新密码要求同时包含字母和数字, 8~15位
    @PostMapping("updatePassword")
    @ApiOperation(value = "修改密码", notes = "需要携带请求头wustyjsToken, 新的密码要求必须同时且仅包含字母和数字8~15位, data为布尔值, true:修改成功, false:修改失败")
    @ApiResponses({
            @ApiResponse(code = 70000, message = "正常返回, 默认成功"),
            @ApiResponse(code = 70001, message = "wustyjsToken过期"),
            @ApiResponse(code = 70005, message = "后端服务有未捕获的异常. 70006以上均为非法请求或者参数校验不合法"),
    })
    public ResponseVO updatePassword(@RequestHeader(value = "wustyjsToken") String wustyjsToken,
                                     @RequestParam("newPassword") String newPassword) {
        Pattern patternnewPassword = Pattern.compile("^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9]{8,15}$");
        if (!patternnewPassword.matcher(newPassword).matches())
            return ResponseVO.custom().success().message("新密码不合法").data(false).build();
        return spiderService.updatePassword(wustyjsToken, newPassword);
    }
}