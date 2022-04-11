package io.metersphere.track.request.testcase;

import lombok.Data;

@Data
public class Descriptions {
    /**
     * 前置条件
     */
    private String preconditions;
    /**
     * 测试步骤
     */
    private String operatingSteps;
    /**
     * 预期结果
     */
    private String expectedResult;
    /**
     * 实际结果结果
     */
    private String actualResult;
}
