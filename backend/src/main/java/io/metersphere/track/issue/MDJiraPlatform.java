package io.metersphere.track.issue;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.common.util.UuidUtils;
import com.google.common.collect.Lists;
import io.metersphere.base.domain.IssuesDao;
import io.metersphere.base.domain.IssuesWithBLOBs;
import io.metersphere.base.domain.Project;
import io.metersphere.commons.utils.CommonBeanFactory;
import io.metersphere.dto.CustomFieldItemDTO;
import io.metersphere.dto.IssueTemplateDao;
import io.metersphere.service.IssueTemplateService;
import io.metersphere.track.issue.client.JiraClientV2;
import io.metersphere.track.issue.domain.Jira.JiraIssue;
import io.metersphere.track.issue.domain.Jira.JiraSearchReq;
import io.metersphere.track.issue.domain.Jira.JiraSearchResp;
import io.metersphere.track.request.testcase.IssuesRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

public class MDJiraPlatform extends JiraPlatform {

    private IssueTemplateService issueTemplateService;

    public MDJiraPlatform(IssuesRequest issuesRequest) {
        super(issuesRequest);
        this.issueTemplateService = CommonBeanFactory.getBean(IssueTemplateService.class);
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
        });
    }

    public List<JiraIssue> getIssues() {
        Map<String, String> jiraInfo = getJiraInfo("issuetype");
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
}
