package com.linghang.wusthelper.wustlib.controller;

import com.linghang.wusthelper.base.response.ResponseVO;
import com.linghang.wusthelper.wustlib.entity.Book;
import com.linghang.wusthelper.wustlib.service.IBookBorrowService;
import com.linghang.wusthelper.wustlib.service.IBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test")
@Api(tags = "本地测试")
public class TestController {

    @Autowired
    private IBookService bookService;

    @Autowired
    private IBookBorrowService borrowService;

    @GetMapping("health")
    @ApiOperation(value = "健康检测", notes = "测试服务是否还在正常运行")
    public ResponseVO libHealth() {
        return ResponseVO.custom().success().build();
    }

    @GetMapping("curBooks/{studentNum}")
    @ApiOperation(value = "获取学生的当前借阅信息")
    public List<Book> getScore(@PathVariable("studentNum") String studentNum) {
        return bookService.getCurBooksByUsername(studentNum);
    }

}
