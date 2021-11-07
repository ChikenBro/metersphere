package io.metersphere.base.mapper;

import io.metersphere.base.domain.ApiScenarioModule;
import io.metersphere.base.domain.ApiScenarioModuleExample;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface ApiScenarioModuleMapper {
    long countByExample(ApiScenarioModuleExample example);

    int deleteByExample(ApiScenarioModuleExample example);

    int deleteByPrimaryKey(String id);

    int insert(ApiScenarioModule record);

    int insertSelective(ApiScenarioModule record);

    List<ApiScenarioModule> selectByExample(ApiScenarioModuleExample example);

    ApiScenarioModule selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") ApiScenarioModule record, @Param("example") ApiScenarioModuleExample example);

    int updateByExample(@Param("record") ApiScenarioModule record, @Param("example") ApiScenarioModuleExample example);

    int updateByPrimaryKeySelective(ApiScenarioModule record);

    int updateByPrimaryKey(ApiScenarioModule record);

    List<ApiScenarioModule> selectScenarioModuleId(@Param("projectIds") List<String> projectIds, @Param("serverName") String serverName);

}