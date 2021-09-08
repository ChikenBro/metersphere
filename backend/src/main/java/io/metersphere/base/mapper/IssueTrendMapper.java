package io.metersphere.base.mapper;

import io.metersphere.base.domain.IssueTrend;
import io.metersphere.base.domain.IssueTrendExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface IssueTrendMapper {
    long countByExample(IssueTrendExample example);

    int deleteByExample(IssueTrendExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(IssueTrend record);

    int insertSelective(IssueTrend record);

    List<IssueTrend> selectByExample(IssueTrendExample example);

    IssueTrend selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") IssueTrend record, @Param("example") IssueTrendExample example);

    int updateByExample(@Param("record") IssueTrend record, @Param("example") IssueTrendExample example);

    int updateByPrimaryKeySelective(IssueTrend record);

    int updateByPrimaryKey(IssueTrend record);
}