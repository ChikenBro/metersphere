package io.metersphere.iteration.response;

import lombok.Data;

import java.util.List;

@Data
public class CaseExecutiveCondition {
    private String testPlanId;
    private String testPlanName;
    private List<ExecutorTestList> executorTestList;
}
