package io.metersphere.base.mapper;

import io.metersphere.base.domain.ProjectCaseConfig;
import io.metersphere.base.domain.ProjectCaseConfigExample;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface ProjectCaseConfigMapper {
    long countByExample(ProjectCaseConfigExample example);

    int deleteByExample(ProjectCaseConfigExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ProjectCaseConfig record);

    int insertSelective(ProjectCaseConfig record);

    List<ProjectCaseConfig> selectByExample(ProjectCaseConfigExample example);

    ProjectCaseConfig selectByPrimaryKey(Integer id);

    ProjectCaseConfig selectByProjectId(String projectId);

    int updateByExampleSelective(@Param("record") ProjectCaseConfig record, @Param("example") ProjectCaseConfigExample example);

    int updateByExample(@Param("record") ProjectCaseConfig record, @Param("example") ProjectCaseConfigExample example);

    int updateByPrimaryKeySelective(ProjectCaseConfig record);

    int updateByPrimaryKey(ProjectCaseConfig record);
}