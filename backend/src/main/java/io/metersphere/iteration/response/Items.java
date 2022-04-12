package io.metersphere.iteration.response;

import lombok.Data;

import java.util.List;

@Data
public class Items {
    private Integer type;
    private String name;
    private List<Options> options;

    @Data
    public static class TestResult {
        private String testPlanId;
        private String testPlanName;
        private String moduleName;
        private String moduleId;
        private Integer caseCount;
        private Integer passCount;
        private Integer failureCount;
        private Integer blockingCount;
        private Integer skipCount;
        private Integer underwayCount;
        private Integer prepareCount;
        private Double passRate;
        private Integer issuesCount;

    }
}
