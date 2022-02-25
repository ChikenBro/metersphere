package io.metersphere.base.mapper;

import io.metersphere.base.domain.ApiScenarioRelevance;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ApiScenarioRelevanceServiceMapper {

    int insertList(@Param("apiScenarioRelevance") List<ApiScenarioRelevance> apiScenarioRelevance);

    int updateList(@Param("projectId") String projectId);

    int insert(ApiScenarioRelevance record);

}
