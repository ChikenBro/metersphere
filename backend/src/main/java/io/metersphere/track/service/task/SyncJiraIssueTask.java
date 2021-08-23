package io.metersphere.track.service.task;

import io.metersphere.base.domain.IssuesDao;
import io.metersphere.base.domain.Project;
import io.metersphere.base.domain.Workspace;
import io.metersphere.base.mapper.WorkspaceMapper;
import io.metersphere.commons.utils.LogUtil;
import io.metersphere.service.ProjectService;
import io.metersphere.track.issue.MDJiraPlatform;
import io.metersphere.track.request.testcase.IssuesRequest;
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

    private Lock lock = new ReentrantLock();
    private Map<String, String> syncIds = new HashMap<>();

    @Autowired
    private ProjectService projectService;
    @Autowired
    private WorkspaceMapper workspaceMapper;


    @Async
    public void getIssueList(String projectId, List<IssuesDao> issues) {
        // 加锁
        LogUtil.info(projectId + " 开始同步jira issue!");
        try {
            lock.tryLock(5, TimeUnit.SECONDS);
            String syncId = syncIds.get(projectId);
            if (null != syncId) {
                LogUtil.info(projectId + " 同步jira issue任务进行中!");
                lock.unlock();
                return;
            }
            // 缓存projectId
            syncIds.put(projectId, projectId);
            lock.unlock();
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

}
