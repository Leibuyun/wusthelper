package com.linghang.wusthelper.wustlib.service;

import com.linghang.wusthelper.base.response.ResponseVO;

public interface LibSpiderService {

    ResponseVO login(String username, String password);

    ResponseVO getCurBooks(String token);

    ResponseVO getHisBooks(String token);

    boolean validCookie(String cookie);

}
