package io.metersphere.iteration.response;

import lombok.Data;

@Data
public class Options {
    private Integer id;
    /**
     * 需求:父需求code
     */
    private Integer parentCode;
    private String name;
    private String statusName;
    private String statusType;
    /**
     * 需求:优先级
     */
    private String priority;
    private Double completedPercent;
    private Long startTime;
    private Long endTime;
    private Long startDate;
    private Long dueDate;
    private Double workingHours;
    private Long createdTime;
    private Long updatedTime;
    private Integer iterationId;
    private String creator;
    /**
     * 迭代:负责人，需求:指派人
     */
    private String leader;
    /**
     * 迭代目标，需求描述
     */
    private String goal;
}
