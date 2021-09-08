package io.metersphere.job.sechedule;

import com.alibaba.fastjson.JSONObject;
import io.metersphere.base.domain.IssueTrend;
import io.metersphere.base.domain.Project;
import io.metersphere.base.domain.Workspace;
import io.metersphere.commons.constants.ScheduleGroup;
import io.metersphere.commons.utils.CommonBeanFactory;
import io.metersphere.service.IssueTrendStatisticsService;
import io.metersphere.service.ProjectService;
import io.metersphere.service.WorkspaceService;
import io.metersphere.track.issue.MDJiraPlatform;
import io.metersphere.track.issue.domain.Jira.JiraIssue;
import io.metersphere.track.request.testcase.IssuesRequest;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.TriggerKey;

import java.util.List;

public class JiraIssueStatisticsJob extends MsScheduleJob {

    private IssueTrendStatisticsService statisticsService;
    private ProjectService projectService;
    private WorkspaceService workspaceService;

    private final String CREATE_JQL = "project = %s AND issuetype = Bug AND created >= %s AND created <= %s";
    private final String UPDATE_JQL = "project = %s AND issuetype = Bug AND resolutiondate >= %s AND resolution in (Done,完成) AND updated >= %s AND updated <= %s";
    private final String ISSUE_ENV = "customfield_11000";

    public JiraIssueStatisticsJob() {
        this.statisticsService = CommonBeanFactory.getBean(IssueTrendStatisticsService.class);
        this.projectService = CommonBeanFactory.getBean(ProjectService.class);
        this.workspaceService = CommonBeanFactory.getBean(WorkspaceService.class);
    }

    @Override
    void businessExecute(JobExecutionContext context) {
        // 获取所有jira项目
        List<IssueTrend> issueTrends = statisticsService.getIssueTrendByCurrentWeek();
        if (null != issueTrends && issueTrends.size() > 0) {
            issueTrends.forEach(v -> {
                Project projectById = projectService.getProjectById(v.getProjectId());
                Workspace workspace = workspaceService.getWorkspaceById(projectById.getWorkspaceId());
                // jira issue 数据
                IssuesRequest issuesRequest = new IssuesRequest();
                issuesRequest.setProjectId(v.getProjectId());
                issuesRequest.setOrganizationId(workspace.getOrganizationId());
                MDJiraPlatform jiraPlatform = new MDJiraPlatform(issuesRequest);
                // 本周新增issue
                String weekCreatedJQL = String.format(CREATE_JQL, v.getJiraKey(), v.getStartWeekTime(), v.getEndWeekTime());
                List<JiraIssue> current = jiraPlatform.getIssues(weekCreatedJQL);
                // 本周解决新增issue
                long newWeekIssueCount = current.stream().filter(issue -> {
                    JSONObject object = issue.getFields().getJSONObject("resolution");
                    return null != object;
                }).count();
                int weekIssueCount = new Long(newWeekIssueCount).intValue();
                int testEnvIssue = countEnvIssue(current, "测试环境");
                int uatEnvIssue = countEnvIssue(current, "预发环境");
                int prodEnvIssue = countEnvIssue(current, "生产环境");
                // 本周解决issue
                String weekHistoryJQL = String.format(UPDATE_JQL, v.getJiraKey(), v.getStartWeekTime(), v.getStartWeekTime(), v.getEndWeekTime());
                List<JiraIssue> issuesTotal = jiraPlatform.getIssues(weekHistoryJQL);
                // 更新数据库
                v.setIssueTotal(current.size());
                v.setResolutionIssueTotal(issuesTotal.size());
                v.setResolutionWeekIssue(weekIssueCount);
                v.setResolutionHistoryIssue(issuesTotal.size() - weekIssueCount);
                v.setTestEnvIssue(testEnvIssue);
                v.setUatEnvIssue(uatEnvIssue);
                v.setProdEnvIssue(prodEnvIssue);
                statisticsService.updateIssueTrend(v);
            });
        }
    }

    private int countEnvIssue(List<JiraIssue> current, String env) {
        long envIssue = current.stream().filter(issue -> {
            JSONObject object = issue.getFields().getJSONObject(ISSUE_ENV);
            return object.getString("value").equals(env);
        }).count();
        return new Long(envIssue).intValue();
    }

    public static JobKey getJobKey(String projectId) {
        return new JobKey(projectId, ScheduleGroup.ISSUE_STATISTICS.name());
    }

    public static TriggerKey getTriggerKey(String projectId) {
        return new TriggerKey(projectId, ScheduleGroup.ISSUE_STATISTICS.name());
    }
}
