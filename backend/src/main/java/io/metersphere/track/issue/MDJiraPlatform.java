package io.metersphere.track.issue;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.common.util.UuidUtils;
import com.google.common.collect.Lists;
import io.metersphere.base.domain.IssuesDao;
import io.metersphere.base.domain.IssuesWithBLOBs;
import io.metersphere.base.domain.Project;
import io.metersphere.commons.exception.MSException;
import io.metersphere.commons.utils.CommonBeanFactory;
import io.metersphere.commons.utils.LogUtil;
import io.metersphere.dto.CustomFieldItemDTO;
import io.metersphere.dto.IssueTemplateDao;
import io.metersphere.service.IssueTemplateService;
import io.metersphere.track.dto.TestCaseNodeDTO;
import io.metersphere.track.issue.client.JiraClientV2;
import io.metersphere.track.issue.domain.Jira.*;
import io.metersphere.track.request.testcase.IssuesRequest;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MDJiraPlatform extends JiraPlatform {

    private IssueTemplateService issueTemplateService;
    private Map<String, String> jiraInfo;

    public MDJiraPlatform(IssuesRequest issuesRequest) {
        super(issuesRequest);
        this.issueTemplateService = CommonBeanFactory.getBean(IssueTemplateService.class);
        this.jiraInfo = getJiraInfo("issuetype");
    }

    private JiraClientV2 jiraClientV2 = new JiraClientV2();

    @Override
    public void syncIssues(Project project, List<IssuesDao> issues) {
        List<JiraIssue> jiraIssues = getIssues();
        // 批量入库
        jiraIssues.forEach(item -> {
            // 是否已存在数据
            if (issues.size() > 0) {
                IssuesWithBLOBs issue = issuesMapper.selectByPrimaryKey(item.getKey());
                if (null != issue && StringUtils.isNotBlank(issue.getId())) {
                    return;
                }
            }
            try {
                // 解析issue
                IssuesWithBLOBs issuesWithBLOBs = new IssuesWithBLOBs();
                parseIssue(issuesWithBLOBs, item);
                issuesWithBLOBs.setId(item.getKey());
                issuesWithBLOBs.setNum(getNextNum(project.getId()));
                issuesWithBLOBs.setProjectId(project.getId());
                issuesWithBLOBs.setDescription(htmlDesc2MsDesc(issuesWithBLOBs.getDescription()));
                // creator
                JSONObject creator = item.getFields().getJSONObject("reporter");
                issuesWithBLOBs.setCreator(creator.getString("name"));
                // 自定义字段
                List<CustomFieldItemDTO> customFields = Lists.newArrayList();
                IssueTemplateDao issueTemplateDao = issueTemplateService.getTemplate(project.getId());
                issueTemplateDao.getCustomFields().forEach(customField -> {
                    CustomFieldItemDTO customFieldItemDTO = new CustomFieldItemDTO();
                    customFieldItemDTO.setId(UuidUtils.generateUuid());
                    customFieldItemDTO.setName(customField.getName());
                    customFieldItemDTO.setType(customField.getType());
                    customFieldItemDTO.setCustomData(customField.getCustomData());
                    if ("fixVersions".equals(customField.getCustomData())) {
                        JSONArray fixVersions = item.getFields().getJSONArray(customField.getCustomData());
                        List<String> vls = Lists.newArrayList();
                        fixVersions.forEach(fixVersion -> {
                            String version = ((JSONObject) fixVersion).getString("name");
                            vls.add(version);
                        });
                        customFieldItemDTO.setValue(JSON.toJSONString(vls));
                        customFields.add(customFieldItemDTO);
                        return;
                    }
                    if ("customfield_10101".equals(customField.getCustomData())) {
                        String fieldString = item.getFields().getString(customField.getCustomData());
                        customFieldItemDTO.setValue(fieldString);
                    } else {
                        JSONObject fieldObject = item.getFields().getJSONObject(customField.getCustomData());
                        if ("assignee".equals(customField.getCustomData())) {
                            customFieldItemDTO.setValue(fieldObject.getString("name"));
                            customFields.add(customFieldItemDTO);
                            return;
                        }
                        customFieldItemDTO.setValue(fieldObject.getString("id"));
                    }
                    customFields.add(customFieldItemDTO);
                });
                issuesWithBLOBs.setCustomFields(JSON.toJSONString(customFields));
                issuesMapper.insert(issuesWithBLOBs);
            } catch (Exception e) {
                LogUtil.error("同步出错了", e);
            }
        });
    }

    public List<JiraIssue> getIssues() {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Authorization", "Basic " + jiraInfo.get("auth"));
        requestHeaders.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        // body
        JiraSearchReq searchReq = new JiraSearchReq();
        String jql = String.format("project = %s AND issuetype = %s  AND  resolution = Unresolved ORDER BY priority DESC, updated DESC",
                jiraInfo.get("key"),
                jiraInfo.get("type"));
        searchReq.setJql(jql);
        searchReq.setStartAt(1);
        searchReq.setMaxResults(50);
        searchReq.setFields(Lists.newArrayList());
        //HttpEntity
        HttpEntity<JiraSearchReq> requestEntity = new HttpEntity<>(searchReq, requestHeaders);
        RestTemplate restTemplate = new RestTemplate();
        //post
        ResponseEntity<String> responseEntity = null;
        responseEntity = restTemplate.exchange(jiraInfo.get("url") + "/rest/api/2/search", HttpMethod.POST, requestEntity, String.class);
        String body = responseEntity.getBody();
        JiraSearchResp searchResponse = JSONObject.parseObject(body, JiraSearchResp.class);
        if (null == searchResponse) {
            return Lists.newArrayList();
        }
        return searchResponse.getIssues();
    }

    public XrayFolders getXrayFolders(String projectKey) {
        String reqUrl = String.format("%s/rest/raven/1.0/api/testrepository/%s/folders", jiraInfo.get("url"), projectKey);
        ResponseEntity<String> responseEntity = getStringResponseEntity(reqUrl);
        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("get jira xray repository error");
        }
        XrayFolders xrayFolders = JSON.parseObject(responseEntity.getBody(), XrayFolders.class);
        if (ObjectUtils.isNotEmpty(xrayFolders)) {
            return xrayFolders;
        }
        xrayFolders = new XrayFolders();
        xrayFolders.setFolders(Lists.newArrayList());
        return xrayFolders;
    }

    public XrayTestCases getXrayTests(String projectKey, Integer folderId) {
        String reqUrl = String.format("%s/rest/raven/1.0/api/testrepository/%s/folders/%s/tests",
                jiraInfo.get("url"),
                projectKey,
                folderId);
        ResponseEntity<String> responseEntity = getStringResponseEntity(reqUrl);
        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("get jira xray testcase error");
        }
        XrayTestCases xrayTestCases = JSON.parseObject(responseEntity.getBody(), XrayTestCases.class);
        if (ObjectUtils.isNotEmpty(xrayTestCases)) {
            return xrayTestCases;
        }
        return null;
    }

    public List<XrayTestStep> getXrayTestCaseSteps(String testKey, Integer caseId, String testType) {
        List<XrayTestStep> testSteps = new ArrayList<>();
        if ("Cucumber".equals(testType)) {
            String reqUrl = String.format("%s/rest/raven/1.0/api/test?jql=issue in(%d)", jiraInfo.get("url"), caseId);
            ResponseEntity<String> responseEntity = getStringResponseEntity(reqUrl);
            List<XrayTestCaseStepSearch> stepSearches = JSON.parseArray(responseEntity.getBody(), XrayTestCaseStepSearch.class);
            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("get jira xray testcase step error");
            }
            stepSearches.forEach(element -> {
                XrayTestStep step = new XrayTestStep();
                step.setId(element.getId());
                step.setStep(element.getDefinition());
                testSteps.add(step);
            });
            return testSteps;
        }
        String reqUrl = String.format("%s/rest/raven/1.0/api/test/%s/step", jiraInfo.get("url"), testKey);
        ResponseEntity<String> responseEntity = getStringResponseEntity(reqUrl);
        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("get jira xray testcase step error");
        }
        List<XrayTestCaseStep> xrayTestCaseSteps = JSON.parseArray(responseEntity.getBody(), XrayTestCaseStep.class);
        xrayTestCaseSteps.forEach(element -> {
            XrayTestStep step = new XrayTestStep();
            step.setId(element.getId());
            step.setStep(element.getStep().getRaw());
            step.setResult(element.getStep().getRaw());
            testSteps.add(step);
        });
        return testSteps;
    }

    @NotNull
    private ResponseEntity<String> getStringResponseEntity(String reqUrl) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Authorization", "Basic " + jiraInfo.get("auth"));
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<HttpHeaders> requestEntity = new HttpEntity<>(requestHeaders);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(reqUrl, HttpMethod.GET, requestEntity, String.class);
    }

}
