package io.metersphere.track.issue;

import io.metersphere.commons.exception.CodingException;
import io.metersphere.notice.util.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.metersphere.base.domain.IssuesDao;
import io.metersphere.base.domain.Project;
import io.metersphere.commons.constants.IssuesManagePlatform;
import io.metersphere.commons.exception.MSException;
import io.metersphere.commons.utils.LogUtil;
import io.metersphere.dto.UserDTO;
import io.metersphere.track.issue.client.*;
import io.metersphere.track.dto.DemandDTO;
import io.metersphere.track.issue.client.ZentaoClient;
import io.metersphere.track.issue.domain.PlatformUser;
import io.metersphere.track.request.testcase.IssuesRequest;
import io.metersphere.track.request.testcase.IssuesUpdateRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;

import java.util.List;

public class CodingPaltform extends AbstractIssuePlatform {

    /**
     * coding account
     */
    private String account;
    /**
     * coding password
     */
    private String password;
    /**
     * coding url eg:http://x.x.x.x/zentao
     */
    private String url;

//    private final CodingClient CodingClient;

    protected String key = IssuesManagePlatform.Zentao.toString();

    public CodingPaltform(IssuesRequest issuesRequest) {
        super(issuesRequest);
//        String config = getPlatformConfig(IssuesManagePlatform.Zentao.toString());
//        // todo
//        if (StringUtils.isBlank(config)) {
//            MSException.throwException("未集成禅道平台!");
//        }
//        JSONObject object = JSON.parseObject(config);
//        this.account = object.getString("account");
//        this.password = object.getString("password");
//        this.url = object.getString("url");
//        String type = object.getString("request");
//        this.orgId = issuesRequest.getOrganizationId();
//        this.zentaoClient = ZentaoFactory.getInstance(this.url, type);
    }

    @Override
    String getProjectId(String projectId) {
        return null;
    }

    @Override
    public List<IssuesDao> getIssue(IssuesRequest request) {
        request.setPlatform(IssuesManagePlatform.Coding.toString());
        List<IssuesDao> issues;
        if (StringUtils.isNotBlank(request.getProjectId())) {
            issues = extIssuesMapper.getIssuesByProjectId(request);
        } else {
            issues = extIssuesMapper.getIssuesByCaseId(request);
        }
        return issues;
    }

    @Override
    public List<DemandDTO> getDemandList(String projectId) {
        System.out.println();
        return null;
    }

    @Override
    public String addIssue(IssuesUpdateRequest issuesRequest) {
        this.url = "http://ms-coding.dev.mudu.tv/issues/add";
        LogUtil.info("add issue: " + issuesRequest);
        return CodingException.checkCodingException(this.url, issuesRequest);

    }

    @Override
    public String updateIssue(IssuesUpdateRequest request) {
        this.url = "http://ms-coding.dev.mudu.tv/issues/update";
        LogUtil.info("update issue: " + request);
        return CodingException.checkCodingException(this.url, request);
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
