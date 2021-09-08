package io.metersphere.base.domain;

import java.io.Serializable;
import lombok.Data;

@Data
public class IssueTrend implements Serializable {
    private Integer id;

    private String projectId;

    private String jiraKey;

    private Integer issueWeek;

    private String startWeekTime;

    private String endWeekTime;

    private Integer issueTotal;

    private Integer resolutionIssueTotal;

    private Integer resolutionWeekIssue;

    private Integer resolutionHistoryIssue;

    private Integer testEnvIssue;

    private Integer uatEnvIssue;

    private Integer prodEnvIssue;

    private Long createTime;

    private Long updateTime;

    private Integer issueYear;

    private static final long serialVersionUID = 1L;
}