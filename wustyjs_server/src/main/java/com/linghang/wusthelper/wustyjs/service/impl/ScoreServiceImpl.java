package com.linghang.wusthelper.wustyjs.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linghang.wusthelper.wustyjs.dto.ScoreDto;
import com.linghang.wusthelper.wustyjs.entity.Score;
import com.linghang.wusthelper.wustyjs.mapper.ScoreMapper;
import com.linghang.wusthelper.wustyjs.service.IScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScoreServiceImpl extends ServiceImpl<ScoreMapper, Score> implements IScoreService {

    @Autowired
    private ScoreMapper scoreMapper;

    @Override
    public List<ScoreDto> getScores(String studentNum) {
        return scoreMapper.getScores(studentNum);
    }
}
