package io.metersphere.track.issue.domain.Jira;

import lombok.Data;

@Data
public class XrayTestCase {
    private Integer id;
    private String key;
    private String summary;
    private Integer rank;
    private String priority;
    private String testType;
}
