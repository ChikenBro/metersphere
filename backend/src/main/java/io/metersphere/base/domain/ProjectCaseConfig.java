package io.metersphere.base.domain;

import java.io.Serializable;
import lombok.Data;

@Data
public class ProjectCaseConfig implements Serializable {
    private Integer id;

    private String projectId;

    private String caseProjectKey;

    private Long createTime;

    private Long updateTime;

    private static final long serialVersionUID = 1L;
}