package com.linghang.wusthelper.wustyjs.service.impl;

import com.linghang.wusthelper.wustyjs.dto.ScoreDto;
import com.linghang.wusthelper.wustyjs.entity.Score;
import com.linghang.wusthelper.wustyjs.mapper.ScoreMapper;
import com.linghang.wusthelper.wustyjs.service.IScoreService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lby
 * @since 2022-01-26
 */
@Service
public class ScoreServiceImpl extends ServiceImpl<ScoreMapper, Score> implements IScoreService {

    @Autowired
    private ScoreMapper scoreMapper;

    @Override
    public List<ScoreDto> getScores(String studentNum) {
        return scoreMapper.getScores(studentNum);
    }
}
