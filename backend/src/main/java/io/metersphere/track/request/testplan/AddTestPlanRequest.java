package io.metersphere.track.request.testplan;

import io.metersphere.base.domain.TestPlan;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AddTestPlanRequest extends TestPlan {
    private List<String> projectIds;
    private Integer iterationCode;
    private String environment;
    /**
     * 计划继承，测试计划id
     */
    private String testPlanInherit;
    /**
     * 继承后是否保留缺陷状态，默认false
     */
    private boolean ifRetain;

}
