package io.metersphere.track.issue.domain.Jira;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class JiraSearchReq {
    private String jql;
    private int startAt;
    private int maxResults;
    private boolean validateQuery;
    private List<String> fields;
    private String expand;
}
