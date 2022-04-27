package io.metersphere.base.mapper;

import io.metersphere.base.domain.Iteration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;


@Mapper
@Component
public interface IterationMapper {

    /**
     * 获取 Iteration对象 根据projectId
     *
     * @param iterationCode 迭代code
     * @return 返回影响行数
     */
    List<Iteration> getIterationByIterationCode(@Param("iterationCode") Integer iterationCode);

    /**
     * 获取 Iteration对象 根据p
     *
     * @param iterationIds 迭代code
     * @return 返回影响行数
     */
    List<Iteration> getIterationsByIterationId(@Param("iterationIds") List<String> iterationIds);
}
