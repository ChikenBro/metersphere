package io.metersphere.track.issue.domain.Jira;

import lombok.Data;

import java.util.List;

@Data
public class XrayTestCases {
    private List<XrayTestCase> tests;
    private Integer total;
}
