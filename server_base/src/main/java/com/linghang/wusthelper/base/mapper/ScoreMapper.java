package com.linghang.wusthelper.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.linghang.wusthelper.base.dto.ScoreDto;
import com.linghang.wusthelper.base.entity.Score;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author lby
 * @since 2022-01-26
 */
@Mapper
public interface ScoreMapper extends BaseMapper<Score> {

    List<ScoreDto> getScores(@Param("studentNum") String studentNum);

}
