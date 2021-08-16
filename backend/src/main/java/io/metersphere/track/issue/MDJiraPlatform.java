package io.metersphere.track.issue;

import io.metersphere.base.domain.IssuesDao;
import io.metersphere.base.domain.Project;
import io.metersphere.track.issue.client.JiraClientV2;
import io.metersphere.track.issue.domain.Jira.JiraIssue;
import io.metersphere.track.request.testcase.IssuesRequest;

import java.util.List;

public class MDJiraPlatform extends JiraPlatform {

    public MDJiraPlatform(IssuesRequest issuesRequest) {
        super(issuesRequest);
    }

    private JiraClientV2 jiraClientV2 = new JiraClientV2();

    @Override
    public void syncIssues(Project project, List<IssuesDao> issues) {
        //        super.syncIssues(project, issues);

        // 解析issue
        // 批量入库

    }

    public List<JiraIssue> getIssues() {
        // 比较待入库数据
//        jiraClientV2.

        return null;
    }
}
