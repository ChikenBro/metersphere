package io.metersphere.track.issue;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import io.metersphere.base.domain.*;
import io.metersphere.commons.constants.IssuesManagePlatform;
import io.metersphere.commons.constants.IssuesStatus;
import io.metersphere.commons.exception.MSException;
import io.metersphere.commons.utils.EncryptUtils;
import io.metersphere.commons.utils.LogUtil;
import io.metersphere.commons.utils.SessionUtils;
import io.metersphere.dto.CustomFieldItemDTO;
import io.metersphere.dto.UserDTO;
import io.metersphere.service.UserService;
import io.metersphere.track.dto.DemandDTO;
import io.metersphere.track.issue.client.JiraClientV2;
import io.metersphere.track.issue.domain.Jira.JiraAddIssueResponse;
import io.metersphere.track.issue.domain.Jira.JiraConfig;
import io.metersphere.track.issue.domain.Jira.JiraIssue;
import io.metersphere.track.issue.domain.PlatformUser;
import io.metersphere.track.request.testcase.IssuesRequest;
import io.metersphere.track.request.testcase.IssuesUpdateRequest;
import org.apache.commons.lang3.StringUtils;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JiraPlatform extends AbstractIssuePlatform {

    protected String key = IssuesManagePlatform.Jira.toString();

    private JiraClientV2 jiraClientV2 = new JiraClientV2();

    public JiraPlatform(IssuesRequest issuesRequest) {
        super(issuesRequest);
    }

    public JiraConfig getConfig() {
        String config = getPlatformConfig(IssuesManagePlatform.Jira.toString());
        JiraConfig jiraConfig = JSONObject.parseObject(config, JiraConfig.class);
        validateConfig(jiraConfig);
        return jiraConfig;
    }

    public JiraConfig getUserConfig() {
        JiraConfig jiraConfig = null;
        String config = getPlatformConfig(IssuesManagePlatform.Jira.toString());
        if (StringUtils.isNotBlank(config)) {
            jiraConfig = JSONObject.parseObject(config, JiraConfig.class);
            UserDTO.PlatformInfo userPlatInfo = getUserPlatInfo(this.orgId);
            if (userPlatInfo != null && StringUtils.isNotBlank(userPlatInfo.getJiraAccount())
                    && StringUtils.isNotBlank(userPlatInfo.getJiraPassword())) {
                jiraConfig.setAccount(userPlatInfo.getJiraAccount());
                jiraConfig.setPassword(userPlatInfo.getJiraPassword());
            }
        }
        validateConfig(jiraConfig);
        return jiraConfig;
    }

    @Override
    public List<IssuesDao> getIssue(IssuesRequest issuesRequest) {
        issuesRequest.setPlatform(IssuesManagePlatform.Jira.toString());
        List<IssuesDao> issues;
        if (StringUtils.isNotBlank(issuesRequest.getProjectId())) {
            issues = extIssuesMapper.getIssuesByProjectId(issuesRequest);
        } else {
            issues = extIssuesMapper.getIssuesByCaseId(issuesRequest);
        }
        return issues;
    }

    public void parseIssue(IssuesWithBLOBs item, JiraIssue jiraIssue) {
        String lastmodify = "";
        String status = "";
        JSONObject fields = jiraIssue.getFields();

        status = getStatus(fields);
        JSONObject assignee = (JSONObject) fields.get("assignee");
        String description = fields.getString("description");
        if (null == description) {
            description = "";
        }
        Parser parser = Parser.builder().build();
        Node document = parser.parse(description);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        description = renderer.render(document);

        if (assignee != null) {
            lastmodify = assignee.getString("displayName");
        }
        // 自定义assignee
        List<CustomFieldItemDTO> cfs = JSON.parseArray(item.getCustomFields(), CustomFieldItemDTO.class);
        if (null != cfs) {
            for (CustomFieldItemDTO cf : cfs) {
                if (cf.getCustomData().equals("assignee")) {
                    if (assignee != null) {
                        try {
                            String userId = getUserId(assignee.getString("name"), assignee.getString("emailAddress"));
                            cf.setValue(userId);
                        } catch (Exception e) {
                            LogUtil.error(e);
                            cf.setValue(assignee.getString("name"));
                        }
                    }
                }
            }
        }
        item.setCustomFields(JSON.toJSONString(cfs));
        item.setId(jiraIssue.getKey());
        item.setTitle(fields.getString("summary"));
        item.setCreateTime(fields.getLong("created"));
        item.setLastmodify(lastmodify);
        item.setDescription(description);
        item.setPlatformStatus(status);
        item.setPlatform(IssuesManagePlatform.Jira.toString());
    }

    private String getStatus(JSONObject fields) {
        JSONObject statusObj = (JSONObject) fields.get("status");
        if (statusObj != null) {
            JSONObject statusCategory = (JSONObject) statusObj.get("statusCategory");
            return statusCategory.getString("name");
        }
        return "";
    }

    @Override
    public List<DemandDTO> getDemandList(String projectId) {
        List<DemandDTO> list = new ArrayList<>();

        try {
            Map<String, String> jiraInfo = getJiraInfo("storytype");
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.add("Authorization", "Basic " + jiraInfo.get("auth"));
            requestHeaders.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            //HttpEntity
            HttpEntity<String> requestEntity = new HttpEntity<>(requestHeaders);
            RestTemplate restTemplate = new RestTemplate();
            //post
            ResponseEntity<String> responseEntity = null;
            responseEntity = restTemplate.exchange(jiraInfo.get("url") + "/rest/api/2/search?jql=project=" + jiraInfo.get("key") + "+AND+issuetype=" + jiraInfo.get("type") + "&fields=summary,issuetype",
                    HttpMethod.GET, requestEntity, String.class);
            String body = responseEntity.getBody();
            JSONObject jsonObject = JSONObject.parseObject(body);
            JSONArray jsonArray = jsonObject.getJSONArray("issues");
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject o = jsonArray.getJSONObject(i);
                String issueKey = o.getString("key");
                JSONObject fields = o.getJSONObject("fields");
                String summary = fields.getString("summary");
                DemandDTO demandDTO = new DemandDTO();
                demandDTO.setName(summary);
                demandDTO.setId(issueKey);
                demandDTO.setPlatform(IssuesManagePlatform.Jira.name());
                list.add(demandDTO);
            }
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
        }

        return list;
    }

    private void validateConfig(JiraConfig config) {
        jiraClientV2.setConfig(config);
        if (config == null) {
            MSException.throwException("jira config is null");
        }
        if (StringUtils.isBlank(config.getIssuetype())) {
            MSException.throwException("Jira 问题类型为空");
        }
    }

    private String validateJiraKey(String projectId) {
        String jiraKey = getProjectId(projectId);
        if (StringUtils.isBlank(jiraKey)) {
            MSException.throwException("未关联Jira 项目Key");
        }
        return jiraKey;
    }

    protected Map<String, String> getJiraInfo(String issueType) {
        Map<String, String> stringMap = Maps.newHashMap();
        String key = validateJiraKey(projectId);
        String config = getPlatformConfig(IssuesManagePlatform.Jira.toString());
        JSONObject object = JSON.parseObject(config);

        if (object == null) {
            MSException.throwException("jira config is null");
        }

        String account = object.getString("account");
        String password = object.getString("password");
        String url = object.getString("url");
        String type = object.getString(issueType);
        String auth = EncryptUtils.base64Encoding(account + ":" + password);
        stringMap.put("key", key);
        stringMap.put("url", url);
        stringMap.put("type", type);
        stringMap.put("auth", auth);
        return stringMap;
    }

    @Override
    public void addIssue(IssuesUpdateRequest issuesRequest) {
        issuesRequest.setPlatform(IssuesManagePlatform.Jira.toString());

        JiraConfig config = getUserConfig();
        jiraClientV2.setConfig(config);

        String jiraKey = validateJiraKey(issuesRequest.getProjectId());

        JSONObject fields = new JSONObject();
        JSONObject project = new JSONObject();

        String desc = issuesRequest.getDescription();
        List<File> imageFiles = getImageFiles(desc);
        desc = removeImage(desc);

        fields.put("project", project);
        project.put("key", jiraKey);

        JSONObject issuetype = new JSONObject();
        issuetype.put("name", config.getIssuetype());

        fields.put("summary", issuesRequest.getTitle());
//        fields.put("description", new JiraIssueDescription(desc));
        fields.put("description", desc);
        fields.put("issuetype", issuetype);

        JSONObject addJiraIssueParam = new JSONObject();
        addJiraIssueParam.put("fields", fields);

        List<CustomFieldItemDTO> customFields = getCustomFields(issuesRequest.getCustomFields());
        jiraClientV2.setConfig(config);

        customFields.forEach(item -> {
            if (StringUtils.isNotBlank(item.getCustomData())) {
                if (StringUtils.isNotBlank(item.getValue())) {
                    if (StringUtils.isNotBlank(item.getType()) &&
                            StringUtils.equalsAny(item.getType(), "select", "multipleSelect", "checkbox", "radio", "member", "multipleMember")) {
                        JSONObject param = new JSONObject();
                        param.put("id", item.getValue());
                        fields.put(item.getCustomData(), param);
                        // 处理自定义字段
                        handleMuDuCustomData(item, fields);
                    } else {
                        fields.put(item.getCustomData(), item.getValue());
                    }
                }
            }
        });
        // 报告者
        String userName = getUserName(issuesRequest.getCreator());
        JSONObject issueParams = (JSONObject) addJiraIssueParam.get("fields");
        JSONObject reporter = new JSONObject();
        reporter.put("name", userName);
        issueParams.put("reporter", reporter);
        JiraAddIssueResponse result = jiraClientV2.addIssue(JSONObject.toJSONString(addJiraIssueParam));
        JiraIssue issues = jiraClientV2.getIssues(result.getId());

        imageFiles.forEach(img -> {
            jiraClientV2.uploadAttachment(result.getKey(), img);
        });
        String status = getStatus(issues.getFields());
        issuesRequest.setPlatformStatus(status);

        issuesRequest.setId(result.getKey());
        // 用例与第三方缺陷平台中的缺陷关联
        handleTestCaseIssues(issuesRequest);

        // 插入缺陷表
        insertIssues(result.getKey(), issuesRequest);
    }

    private void handleMuDuCustomData(CustomFieldItemDTO item, JSONObject fields) {
        if ("assignee".equals(item.getCustomData())) {
            JSONObject object = new JSONObject();
            String userName = getUserName(item.getValue());
            if (StringUtils.isNotBlank(userName)) {
                item.setValue(userName);
            }
            object.put("name", item.getValue());
            fields.put(item.getCustomData(), object);
        }
        if ("fixVersions".equals(item.getCustomData())) {
            JSONArray array = JSON.parseArray(item.getValue());
            List<JSONObject> objects = new ArrayList<>();
            array.forEach(vl -> {
                JSONObject object = new JSONObject();
                object.put("name", vl);
                objects.add(object);
            });
            fields.put(item.getCustomData(), objects);
        }
        if ("components".equals(item.getCustomData())) {
            List<JSONObject> objects = new ArrayList<>();
            if (StringUtils.isNotEmpty(item.getValue())) {
                JSONObject component = new JSONObject();
                component.put("id", item.getValue());
                objects.add(component);
            }
            fields.put(item.getCustomData(), objects);
        }
    }

    @Override
    public void updateIssue(IssuesUpdateRequest request) {
        // todo 调用接口
        request.setDescription(null);
        handleIssueUpdate(request);
    }

    @Override
    public void deleteIssue(String id) {
    }

    @Override
    public void testAuth() {
        setConfig();
        jiraClientV2.auth();
    }

    @Override
    public void userAuth(UserDTO.PlatformInfo userInfo) {
        String config = getPlatformConfig(IssuesManagePlatform.Jira.toString());
        JiraConfig jiraConfig = JSONObject.parseObject(config, JiraConfig.class);
        jiraConfig.setAccount(userInfo.getJiraAccount());
        jiraConfig.setPassword(userInfo.getJiraPassword());
        validateConfig(jiraConfig);
        jiraClientV2.setConfig(jiraConfig);
        jiraClientV2.auth();
    }

    @Override
    public List<PlatformUser> getPlatformUser() {
        return null;
    }

    @Override
    public void syncIssues(Project project, List<IssuesDao> issues) {
        issues.forEach(item -> {
            setConfig();
            try {
                parseIssue(item, jiraClientV2.getIssues(item.getId()));
                item.setDescription(htmlDesc2MsDesc(item.getDescription()));
                issuesMapper.updateByPrimaryKeySelective(item);
            } catch (HttpClientErrorException e) {
                if (e.getRawStatusCode() == 404) {
                    // 标记成删除
                    item.setPlatformStatus(IssuesStatus.DELETE.toString());
                    issuesMapper.deleteByPrimaryKey(item.getId());
                }
            }
        });
    }

    @Override
    String getProjectId(String projectId) {
        if (StringUtils.isNotBlank(projectId)) {
            return projectService.getProjectById(projectId).getJiraKey();
        }
        TestCaseWithBLOBs testCase = testCaseService.getTestCase(testCaseId);
        Project project = projectService.getProjectById(testCase.getProjectId());
        return project.getJiraKey();
    }

    public JiraConfig setConfig() {
        JiraConfig config = getConfig();
        jiraClientV2.setConfig(config);
        return config;
    }
}
