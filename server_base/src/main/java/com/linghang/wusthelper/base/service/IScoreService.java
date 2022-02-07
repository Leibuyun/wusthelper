package com.linghang.wusthelper.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linghang.wusthelper.base.dto.ScoreDto;
import com.linghang.wusthelper.base.entity.Score;


import java.util.List;


public interface IScoreService extends IService<Score> {

    List<ScoreDto> getScores(String studentNum);
}
