package io.metersphere.iteration;

import lombok.Data;

@Data
public class RequirementDao {
    private String id;
    private Integer num;
    private String projectId;
    private Integer code;
    private String name;
    private String priority;
    private String status;
    private String assignee;
    private String creator;
    private String watcher;
    private String description;
    private String dueDate;
    private String startDate;
    private String createTime;
    private String updateTime;
    private Long syncStartTime;
    private Long syncUpdateTime;
    private Double workingHours;
    private Integer parentId;
    private Integer iterationCode;
    private Integer serviceId;
    private Integer deleted;
}
