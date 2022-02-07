package com.linghang.wusthelper.wustlib.controller;

import com.linghang.wusthelper.base.dto.LoginDto;
import com.linghang.wusthelper.base.response.ResponseVO;
import com.linghang.wusthelper.wustlib.service.LibSpiderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@Api(tags = {"爬虫服务"})
public class LibSpiderController {

    @Autowired
    private LibSpiderService libSpiderService;

    @PostMapping("login")
    @ApiOperation(value = "登录请求", notes = "获取wustlibToken")
    @ApiResponses({
            @ApiResponse(code = 70000, message = "正常返回, 默认成功"),
            @ApiResponse(code = 70002, message = "账号密码不匹配"),
            @ApiResponse(code = 70003, message = "网络异常. "),
            @ApiResponse(code = 70005, message = "后端服务有未捕获的异常. 70006以上均为非法请求或者参数校验不合法"),
    })
    public ResponseVO login(@Validated @RequestBody LoginDto user) {
        return libSpiderService.login(user.getUsername(), user.getPassword());
    }

    @GetMapping("curBooks")
    @ApiOperation(value = "获取当前借阅的图书", notes = "需要携带请求头wustlibToken")
    @ApiResponses({
            @ApiResponse(code = 70000, message = "正常返回, 默认成功"),
            @ApiResponse(code = 70001, message = "wustlibToken过期"),
            @ApiResponse(code = 70005, message = "后端服务有未捕获的异常. 70006以上均为非法请求或者参数校验不合法"),
    })
    public ResponseVO getCurBooks(@RequestHeader(value = "wustlibToken") String wustlibToken) {
        return libSpiderService.getCurBooks(wustlibToken);
    }

    @GetMapping("hisBooks")
    @ApiOperation(value = "获取历史借阅的图书", notes = "需要携带请求头wustlibToken")
    @ApiResponses({
            @ApiResponse(code = 70000, message = "正常返回, 默认成功"),
            @ApiResponse(code = 70001, message = "wustlibToken过期"),
            @ApiResponse(code = 70005, message = "后端服务有未捕获的异常. 70006以上均为非法请求或者参数校验不合法"),
    })
    public ResponseVO getHisBooks(@RequestHeader(value = "wustlibToken") String wustlibToken){
        return libSpiderService.getHisBooks(wustlibToken);
    }
}
