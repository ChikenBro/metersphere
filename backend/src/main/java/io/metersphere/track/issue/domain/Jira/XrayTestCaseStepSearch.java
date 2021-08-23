package io.metersphere.track.issue.domain.Jira;

import lombok.Data;

import java.util.List;

@Data
public class XrayTestCaseStepSearch {
    private String key;
    private Integer id;
    private String self;
    private String reporter;
    private List<Object> precondition;
    private String type;
    private String status;
    private String definition;
}
