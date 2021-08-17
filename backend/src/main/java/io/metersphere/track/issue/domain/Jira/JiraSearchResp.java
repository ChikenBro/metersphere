package io.metersphere.track.issue.domain.Jira;

import lombok.Data;

import java.util.List;

@Data
public class JiraSearchResp {
    private int startAt;
    private int maxResults;
    private int total;
    private List<JiraIssue> issues;
    private String expand;
}
