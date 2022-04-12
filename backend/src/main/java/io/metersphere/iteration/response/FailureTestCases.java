package io.metersphere.iteration.response;

import lombok.Data;

@Data
public class FailureTestCases {
    private String id;
    private Integer num;
    private String name;
    private String testPlanName;
    private String module;
    private String priority;
    private String type;
    private String testMode;
    private String executor;
    private String executeResult;
    private String updateTime;
}
