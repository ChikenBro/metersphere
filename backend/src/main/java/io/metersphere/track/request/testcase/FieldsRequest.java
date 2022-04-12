package io.metersphere.track.request.testcase;

import lombok.Data;

import java.util.List;


@Data

public class FieldsRequest {
    private Integer defectTypeId;
    private String defectTypeName;
    private String priority;
    private Double workingHours;
    private Double recordedHours;
    private Integer iterationCode;
    private String iterationName;
    private String assignee;
    private String assigneeName;
    private String watcherName;
    /**
     * 关联需求
     */
    private Integer requirementCode;
    private String requirementName;
    private String moduleId;
    private String startDate;
    private String dueDate;
    private List<Integer> labelIds;
    /**
     * 文件id列表
     */
    private List<Integer> fileIds;
    private long completedAt;
    private List<String> filePaths;

    /**
     * 复现环境
     */
    private String environment;
    /**
     * 复现频率
     */
    private String repetitionFrequency;
}
