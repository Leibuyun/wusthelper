package com.linghang.wusthelper.wustlib.service;

import com.linghang.wusthelper.wustlib.entity.Book;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lby
 * @since 2022-02-06
 */
public interface IBookService extends IService<Book> {

    List<Book> getCurBooksByUsername(String username);

    List<Book> getHisBooksByUsername(String username);

    int deleteCurBookBorrowByUsername(String username);

    int deleteHisBookBorrowByUsername(String username);

}
