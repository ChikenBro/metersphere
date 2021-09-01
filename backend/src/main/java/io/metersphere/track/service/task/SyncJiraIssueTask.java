package io.metersphere.track.service.task;

import io.metersphere.base.domain.IssuesDao;
import io.metersphere.base.domain.Project;
import io.metersphere.base.domain.Workspace;
import io.metersphere.base.mapper.WorkspaceMapper;
import io.metersphere.commons.utils.LogUtil;
import io.metersphere.service.ProjectService;
import io.metersphere.track.issue.MDJiraPlatform;
import io.metersphere.track.request.testcase.IssuesRequest;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class SyncJiraIssueTask {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private WorkspaceMapper workspaceMapper;

    private Map<String, String> syncIds = new HashMap<>();

    public void getIssueList(String projectId, List<IssuesDao> issues) {
        // 加锁
        LogUtil.info(projectId + " 开始同步jira issue!");
        try {
            boolean isSyncIssue = getSyncIssueProjectId(projectId);
            if (isSyncIssue) {
                return;
            }
            Project project = projectService.getProjectById(projectId);
            Workspace workspace = workspaceMapper.selectByPrimaryKey(project.getWorkspaceId());
            IssuesRequest issuesRequest = new IssuesRequest();
            issuesRequest.setProjectId(projectId);
            issuesRequest.setOrganizationId(workspace.getOrganizationId());
            MDJiraPlatform mdJiraPlatform = new MDJiraPlatform(issuesRequest);
            mdJiraPlatform.syncIssues(project, issues);
            // 删除缓存projectId
            syncIds.remove(projectId);
        } catch (Exception e) {
            LogUtil.error("获取锁出错了" + projectId, e);
            String syncId = syncIds.get(projectId);
            if (null != syncId) {
                syncIds.remove(projectId);
            }
        }
    }

    private synchronized boolean getSyncIssueProjectId(String projectId) {
        if (ObjectUtils.isNotEmpty(syncIds.get(projectId))) {
            return true;
        }
        syncIds.put(projectId, projectId);
        return false;
    }

}
