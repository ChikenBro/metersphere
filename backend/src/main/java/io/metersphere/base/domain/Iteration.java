package io.metersphere.base.domain;

import lombok.Data;

@Data
public class Iteration {
    private String id;
    private String workspaceId;
    private String projectId;
    private Integer code;
    private String name;
    private String leader;
    private String creator;
    private String status;
    /**
     * 迭代目标
     */
    private String description;
    /**
     * 进度
     */
    private Integer schedule;
    private String startTime;
    private String endTime;
    private Long syncStartTime;
    private Long syncUpdateTime;
    private Integer numberOfExecutions;
    private Integer lastPassRate;
    private Integer deleted;
    private String reportId;

}
