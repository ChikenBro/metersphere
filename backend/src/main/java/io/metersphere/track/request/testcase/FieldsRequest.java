package io.metersphere.track.request.testcase;

import lombok.Data;

import java.util.List;


@Data

public class FieldsRequest {
    private String defectTypeId;
    private String priority;
    private Double workingHours;
    private Integer iterationCode;
    private String assigneeName;
    /**
     * 关联需求
     */
    private Integer requirementCode;
    private String moduleId;
    private String startDate;
    private String dueDate;
    private List<Integer> labelIds;
    /**
     * 文件id列表
     */
    private List<Integer> fileIds;
}
