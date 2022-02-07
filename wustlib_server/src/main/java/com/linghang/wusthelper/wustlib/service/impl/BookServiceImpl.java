package com.linghang.wusthelper.wustlib.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.linghang.wusthelper.wustlib.entity.Book;
import com.linghang.wusthelper.wustlib.entity.BookBorrow;
import com.linghang.wusthelper.wustlib.mapper.BookBorrowMapper;
import com.linghang.wusthelper.wustlib.mapper.BookMapper;
import com.linghang.wusthelper.wustlib.service.IBookBorrowService;
import com.linghang.wusthelper.wustlib.service.IBookService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lby
 * @since 2022-02-06
 */
@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements IBookService {

    @Autowired
    private BookBorrowMapper bookBorrowMapper;

    @Autowired
    private BookMapper bookMapper;

    @Override
    public List<Book> getCurBooksByUsername(String username) {
        QueryWrapper<BookBorrow> bookBorrowQueryWrapper = new QueryWrapper<>();
        bookBorrowQueryWrapper.eq("flag", false);
        bookBorrowQueryWrapper.eq("student_num", username);
        List<String> list = bookBorrowMapper.selectList(bookBorrowQueryWrapper).stream().map(BookBorrow::getBid).collect(Collectors.toList());
        if (list.size() != 0)
            return bookMapper.selectBatchIds(list);
        else
            return new ArrayList<Book>();
    }

    @Override
    public List<Book> getHisBooksByUsername(String username) {
        QueryWrapper<BookBorrow> bookBorrowQueryWrapper = new QueryWrapper<>();
        bookBorrowQueryWrapper.eq("flag", true);
        bookBorrowQueryWrapper.eq("student_num", username);
        List<String> list = bookBorrowMapper.selectList(bookBorrowQueryWrapper).stream().map(BookBorrow::getBid).collect(Collectors.toList());
        if (list.size() != 0)
            return bookMapper.selectBatchIds(list);
        else
            return new ArrayList<Book>();
    }

    @Override
    public int deleteCurBookBorrowByUsername(String username) {
        QueryWrapper<BookBorrow> bookBorrowQueryWrapper = new QueryWrapper<>();
        bookBorrowQueryWrapper.eq("flag", false);
        bookBorrowQueryWrapper.eq("student_num", username);
        return bookBorrowMapper.delete(bookBorrowQueryWrapper);
    }

    @Override
    public int deleteHisBookBorrowByUsername(String username) {
        QueryWrapper<BookBorrow> bookBorrowQueryWrapper = new QueryWrapper<>();
        bookBorrowQueryWrapper.eq("flag", true);
        bookBorrowQueryWrapper.eq("student_num", username);
        return bookBorrowMapper.delete(bookBorrowQueryWrapper);
    }
}
