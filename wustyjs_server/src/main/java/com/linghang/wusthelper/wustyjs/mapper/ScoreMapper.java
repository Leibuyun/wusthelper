package com.linghang.wusthelper.wustyjs.mapper;

import com.linghang.wusthelper.wustyjs.dto.ScoreDto;
import com.linghang.wusthelper.wustyjs.entity.Score;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lby
 * @since 2022-01-26
 */
public interface ScoreMapper extends BaseMapper<Score> {

    List<ScoreDto> getScores(@Param("studentNum") String studentNum);

}
