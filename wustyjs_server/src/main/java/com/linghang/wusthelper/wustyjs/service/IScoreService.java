package com.linghang.wusthelper.wustyjs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linghang.wusthelper.wustyjs.dto.ScoreDto;
import com.linghang.wusthelper.wustyjs.entity.Score;

import java.util.List;


public interface IScoreService extends IService<Score> {

    List<ScoreDto> getScores(String studentNum);
}
