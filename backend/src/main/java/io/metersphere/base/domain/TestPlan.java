package io.metersphere.base.domain;

import java.io.Serializable;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
public class TestPlan implements Serializable {
    private String id;

    private String workspaceId;

    private String reportId;

    private String name;

    private String description;

    private String status;

    private String stage;

    private String principal;

    private String testCaseMatchRule;

    private String executorMatchRule;

    private Long createTime;

    private Long updateTime;

    private Long plannedStartTime;

    private Long plannedEndTime;

    private Long actualStartTime;

    private Long actualEndTime;

    private String creator;

    @NotEmpty(message = "projectId不能为空", groups = {addTestPlan.class})
    private String projectId;

    private Integer executionTimes;

    private Boolean automaticStatusUpdate;

    private String tags;

    private String iterationId;

    private String environment;

    private static final long serialVersionUID = 1L;

    public interface addTestPlan {
    }
}