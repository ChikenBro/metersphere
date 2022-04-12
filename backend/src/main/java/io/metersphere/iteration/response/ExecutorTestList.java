package io.metersphere.iteration.response;

import lombok.Data;

@Data
public class ExecutorTestList {
    private String executor;
    private String executorName;
    private Integer caseCount;
    private Integer passCount;
    private Integer failureCount;
    private Integer blockingCount;
    private Integer skipCount;
    private Integer underwayCount;
    private Integer prepareCount;
}
