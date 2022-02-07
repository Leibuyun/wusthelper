package com.linghang.wusthelper.wustyjs.controller;


import com.linghang.wusthelper.base.dto.ScoreDto;
import com.linghang.wusthelper.base.entity.Student;
import com.linghang.wusthelper.base.response.ResponseVO;
import com.linghang.wusthelper.base.service.IScoreService;
import com.linghang.wusthelper.base.service.IStudentService;
import com.linghang.wusthelper.wustyjs.service.WustyjsSpiderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test")
@Api(tags = {"本地测试"})
public class TestController {

    @Autowired
    private WustyjsSpiderService wustyjsSpiderService;

    @Autowired
    private IScoreService scoreService;

    @Autowired
    private IStudentService studentService;

    @GetMapping("health")
    @ApiOperation(value = "测试程序是否还在正常运行", notes = "GET请求, 无参数")
    public ResponseVO testHealth() {
        return ResponseVO.custom().success().build();
    }

    @PostMapping("download")
    @ApiOperation(value = "测试下载图片", notes = "本地测试需要修改底层downloadYzm和图像处理的方法, 改为不要删除图片, 注意图片名是否重复")
    @ApiImplicitParam(name = "size", value = "下载的验证码的个数, 默认为1", required = false, paramType = "query", dataType = "int", example = "1")
    public ResponseVO downloadYzm(@RequestParam(required = false) Integer size) {
        size = (size == null ? 1 : size);
        List<Map<String, String>> mapList = new ArrayList<>();
        for (int i = 0; i < size; ++i) {
            final Map<String, String> map = wustyjsSpiderService.downloadYzm();
            // 裁剪并且二值化, 如果想对比原图和二值化的图片, 去修改处理后的文件名
            wustyjsSpiderService.clippingAndBinary(map.get("fileName"));
            mapList.add(map);
        }
        return ResponseVO.custom().success().data(mapList).build();
    }

    @GetMapping("student/{studentNum}")
    @ApiOperation(value = "获取学生信息的data")
    public Student getStudent(@PathVariable("studentNum") String studentNum) {
        return studentService.getStudent(studentNum);
    }

    @GetMapping("score/{studentNum}")
    @ApiOperation(value = "获取成绩信息的data")
    public List<ScoreDto> getScore(@PathVariable("studentNum") String studentNum) {
        return scoreService.getScores(studentNum);
    }


}
