package io.metersphere.base.mapper;

import io.metersphere.base.domain.TestPlanMembers;
import io.metersphere.base.domain.TestPlanMembersExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TestPlanMembersMapper {
    long countByExample(TestPlanMembersExample example);

    int deleteByExample(TestPlanMembersExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(TestPlanMembers record);

    int insertSelective(TestPlanMembers record);

    List<TestPlanMembers> selectByExampleWithBLOBs(TestPlanMembersExample example);

    List<TestPlanMembers> selectByExample(TestPlanMembersExample example);

    TestPlanMembers selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") TestPlanMembers record, @Param("example") TestPlanMembersExample example);

    int updateByExampleWithBLOBs(@Param("record") TestPlanMembers record, @Param("example") TestPlanMembersExample example);

    int updateByExample(@Param("record") TestPlanMembers record, @Param("example") TestPlanMembersExample example);

    int updateByPrimaryKeySelective(TestPlanMembers record);

    int updateByPrimaryKeyWithBLOBs(TestPlanMembers record);

    int updateByPrimaryKey(TestPlanMembers record);
}