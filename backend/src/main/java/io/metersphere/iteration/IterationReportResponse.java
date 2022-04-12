package io.metersphere.iteration;

import io.metersphere.iteration.response.CaseExecutiveCondition;
import io.metersphere.iteration.response.FailureTestCases;
import io.metersphere.iteration.response.Issues;
import io.metersphere.iteration.response.Items;
import lombok.Data;

import java.util.List;

@Data
public class IterationReportResponse {
    private String projectId;
    private String projectName;
    private String iterationName;
    private String leaderName;
    private List<String> executorNames;
    private List<String> executors;
    private String planStartTime;
    private String planEndTime;
    private String actualStartTime;
    private String actualEndTime;
    private List<Items.TestResult> testResult;
    private List<CaseExecutiveCondition> caseExecutiveCondition;
    private List<FailureTestCases> failureTestCases;
    private List<Issues> issues;
}
