package com.linghang.wusthelper.wustyjs.service;

import com.linghang.wusthelper.wustyjs.response.ResponseVO;

import java.util.Map;

public interface SpiderService {

    ResponseVO login(String username, String password);

    Map<String, String> downloadYzm();

    String clippingAndBinary(String fileName);

    ResponseVO getStudent(String token);

    ResponseVO getScores(String token);

    boolean validCookie(String cookie);

    ResponseVO updatePassword(String token, String newPassword);


}
