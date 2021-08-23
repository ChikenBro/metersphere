package io.metersphere.base.domain;

import java.io.Serializable;
import lombok.Data;

@Data
public class TestPlanMembers implements Serializable {
    private Integer id;

    private String testPlanId;

    private String projectId;

    private String tester;

    private String creator;

    private Integer testCaseTotal;

    private Integer executeTestCase;

    private Integer testCasePass;

    private Integer testCaseFail;

    private Integer testCaseBlock;

    private Long createTime;

    private Long updateTime;

    private String extContent;

    private static final long serialVersionUID = 1L;
}