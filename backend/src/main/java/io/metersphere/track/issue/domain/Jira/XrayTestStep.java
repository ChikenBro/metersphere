package io.metersphere.track.issue.domain.Jira;

import lombok.Data;

@Data
public class XrayTestStep {
    private Integer id;
    private String step;
    private String result;
}
