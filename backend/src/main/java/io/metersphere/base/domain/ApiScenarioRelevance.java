package io.metersphere.base.domain;

import lombok.Data;

@Data
public class ApiScenarioRelevance {

    private String id;

    private String scenarioId;

    private String projectId;

    private String method;

    private String path;

    private int status;

    private Long createTime;

    private Long updateTime;

    private Long deleteTime;

    private String deleteUserId;

    private static final long serialVersionUID = 1L;
}
