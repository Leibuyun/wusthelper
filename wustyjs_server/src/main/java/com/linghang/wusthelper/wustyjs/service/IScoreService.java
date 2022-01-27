package com.linghang.wusthelper.wustyjs.service;

import com.linghang.wusthelper.wustyjs.dto.ScoreDto;
import com.linghang.wusthelper.wustyjs.entity.Score;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lby
 * @since 2022-01-26
 */
public interface IScoreService extends IService<Score> {

    List<ScoreDto> getScores(String studentNum);
}
