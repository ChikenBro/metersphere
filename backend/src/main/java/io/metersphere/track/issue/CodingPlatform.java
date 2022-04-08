package io.metersphere.track.issue;

import io.metersphere.base.domain.IssuesDao;
import io.metersphere.base.domain.Project;
import io.metersphere.dto.UserDTO;
import io.metersphere.track.dto.DemandDTO;
import io.metersphere.track.issue.domain.PlatformUser;
import io.metersphere.track.request.testcase.IssuesRequest;
import io.metersphere.track.request.testcase.IssuesUpdateRequest;

import java.util.List;

public class CodingPlatform extends AbstractIssuePlatform {
    public CodingPlatform(IssuesRequest issuesRequest) {
        super(issuesRequest);
    }

    @Override
    String getProjectId(String projectId) {
        return null;
    }

    @Override
    public List<IssuesDao> getIssue(IssuesRequest request) {
        return null;
    }

    @Override
    public List<DemandDTO> getDemandList(String projectId) {
        return null;
    }

    @Override
    public String addIssue(IssuesUpdateRequest issuesRequest) {
        return null;
    }

    @Override
    public String updateIssue(IssuesUpdateRequest request) {
        return null;
    }

    @Override
    public void deleteIssue(String id) {

    }

    @Override
    public void testAuth() {

    }

    @Override
    public void userAuth(UserDTO.PlatformInfo userInfo) {

    }

    @Override
    public List<PlatformUser> getPlatformUser() {
        return null;
    }

    @Override
    public void syncIssues(Project project, List<IssuesDao> tapdIssues) {

    }
}
