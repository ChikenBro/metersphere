package io.metersphere.track.issue.domain.Jira;

import lombok.Data;

@Data
public class XrayTestCaseStep {
    private Integer id;
    private Integer index;
    private XrayTestCaseStepAction step;
    private XrayTestCaseStepAction data;
    private XrayTestCaseStepAction result;
}