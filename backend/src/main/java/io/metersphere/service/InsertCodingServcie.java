package io.metersphere.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.common.util.UuidUtils;
import com.google.common.collect.Lists;
import io.metersphere.base.domain.IssuesDao;
import io.metersphere.base.domain.IssuesWithBLOBs;
import io.metersphere.base.domain.Project;
import io.metersphere.base.mapper.IssuesMapper;
import io.metersphere.commons.utils.LogUtil;
import io.metersphere.dto.CustomFieldItemDTO;
import io.metersphere.dto.IssueTemplateDao;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InsertCodingServcie {
    protected IssuesMapper issuesMapper;

    private JSONObject codingGetProjectIssueList( String jsonString,String youToken) {
        String url = "https://mudu1.coding.net/open-api";
        JSONObject json_test = null;


        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        String result = "";
        try {
            // 通过址默认配置创建一个httpClient实例
            httpClient = HttpClients.createDefault();
            // 创建httpGet远程连接实例
            HttpPost httpPost = new HttpPost(url);
            // 设置请求头信息，鉴权
            httpPost.setHeader("Authorization", "token "+youToken);
            StringEntity se = new StringEntity(jsonString, "UTF-8");
            se.setContentType("application/json");
            httpPost.setEntity(se);
            // 设置配置请求参数
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000)// 连接主机服务超时时间
                    .setConnectionRequestTimeout(35000)// 请求超时时间
                    .setSocketTimeout(60000)// 数据读取超时时间
                    .build();
            // 为httpGet实例设置配置
            httpPost.setConfig(requestConfig);
            // 执行get请求得到返回对象
            response = httpClient.execute(httpPost);
            // 通过返回对象获取返回数据
            HttpEntity entity = response.getEntity();
            // 通过EntityUtils中的toString方法将结果转换为字符串
            result = EntityUtils.toString(entity);

            json_test = JSONObject.parseObject(result);

            return json_test;

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json_test;
    }
    private HashMap<Object, Object> getAllprojectMember(String youToken) {
        HashMap<Object, Object> member = new HashMap<>();
        String jsonString = "{\"Action\": \"DescribeTeamMembers\",   \"PageNumber\": 1,   \"PageSize\": 500 }";
        String url = "https://mudu1.coding.net/open-api";
        JSONObject json_test = null;


        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        String result = "";
        try {
            // 通过址默认配置创建一个httpClient实例
            httpClient = HttpClients.createDefault();
            // 创建httpGet远程连接实例
            HttpPost httpPost = new HttpPost(url);
            // 设置请求头信息，鉴权
            httpPost.setHeader("Authorization", "token "+youToken);
            StringEntity se = new StringEntity(jsonString, "UTF-8");
            se.setContentType("application/json");
            httpPost.setEntity(se);
            // 设置配置请求参数
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000)// 连接主机服务超时时间
                    .setConnectionRequestTimeout(35000)// 请求超时时间
                    .setSocketTimeout(60000)// 数据读取超时时间
                    .build();
            // 为httpGet实例设置配置
            httpPost.setConfig(requestConfig);
            // 执行get请求得到返回对象
            response = httpClient.execute(httpPost);
            // 通过返回对象获取返回数据
            HttpEntity entity = response.getEntity();
            // 通过EntityUtils中的toString方法将结果转换为字符串
            result = EntityUtils.toString(entity);

            json_test = JSONObject.parseObject(result);
            for (Object e2 : json_test.getJSONObject("Response").getJSONObject("Data").getJSONArray("TeamMembers")) {
                JSONObject e3 = JSONObject.parseObject(e2.toString());
                member.put(e3.get("Id"),e3.get("Name"));


            }

            return member;

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return member;
    }
    public void syncIssues(String projectName,String youToken,String projectId) {
        Integer PageNumber = 1;
        Map<Object, Object> member = this.getAllprojectMember(  youToken);
        while(true){
        String jsonString1 = String.format("{\"Action\": \"DescribeIssueListWithPage\", 	\"ProjectName\": \"%s\", 	\"IssueType\": \"DEFECT\", 	\"PageNumber\": \"%d\", 	\"PageSize\": 20  }",  projectName,PageNumber);

        JSONObject respResult_AddBug = this.codingGetProjectIssueList(jsonString1,youToken );
            if (respResult_AddBug == null){
//           modulName.add(testMap);
           return ;
            }
//            IssuesWithBLOBs issuesWithBLOBs = new IssuesWithBLOBs();
//            // 自定义字段
//            List<CustomFieldItemDTO> customFields = Lists.newArrayList();
//            IssueTemplateDao issueTemplateDao = issueTemplateService.getTemplate(project.getId());
//            issueTemplateDao.getCustomFields().forEach(customField -> {
//                CustomFieldItemDTO customFieldItemDTO = new CustomFieldItemDTO();
//                customFieldItemDTO.setId(UuidUtils.generateUuid());
//                customFieldItemDTO.setName(customField.getName());
//                customFieldItemDTO.setType(customField.getType());
//                customFieldItemDTO.setCustomData(customField.getCustomData());
//                if ("fixVersions".equals(customField.getCustomData())) {
//                    JSONArray fixVersions = item.getFields().getJSONArray(customField.getCustomData());
//                    List<String> vls = Lists.newArrayList();
//                    fixVersions.forEach(fixVersion -> {
//                        String version = ((JSONObject) fixVersion).getString("name");
//                        vls.add(version);
//                    });
//                    customFieldItemDTO.setValue(JSON.toJSONString(vls));
//                    customFields.add(customFieldItemDTO);
//                    return;
//                }
//                if ("customfield_10101".equals(customField.getCustomData())) {
//                    String fieldString = item.getFields().getString(customField.getCustomData());
//                    customFieldItemDTO.setValue(fieldString);
//                    customFields.add(customFieldItemDTO);
//                    return;
//                }
//                if ("components".equals(customField.getCustomData())) {
//                    JSONArray jsonArray = item.getFields().getJSONArray(customField.getCustomData());
//                    if (jsonArray.size() > 0) {
//                        JSONObject jsonObject = (JSONObject) jsonArray.get(0);
//                        customFieldItemDTO.setValue(jsonObject.get("id").toString());
//                    }
//                    customFields.add(customFieldItemDTO);
                    return;
                }
//                if ("assignee".equals(customField.getCustomData())) {
//                    JSONObject fieldObject = item.getFields().getJSONObject(customField.getCustomData());
//                    String assigneeUserId = "";
//                    try {
//                        assigneeUserId = getUserId(fieldObject.getString("name"), fieldObject.getString("emailAddress"));
//                    } catch (Exception e) {
//                        LogUtil.error(e);
//                    }
//                    if (StringUtils.isEmpty(assigneeUserId)) {
//                        customFieldItemDTO.setValue(fieldObject.getString("name"));
//                    } else {
//                        customFieldItemDTO.setValue(assigneeUserId);
//                    }
//                    customFields.add(customFieldItemDTO);
//                    return;
//                }
//                JSONObject fieldObject = item.getFields().getJSONObject(customField.getCustomData());
//                if (null == fieldObject) {
//                    customFieldItemDTO.setValue("");
//                } else {
//                    customFieldItemDTO.setValue(fieldObject.getString("id"));
//                }
//                customFields.add(customFieldItemDTO);
//            });
//            issuesWithBLOBs.setCustomFields(JSON.toJSONString(customFields));
//            issuesMapper.insert(issuesWithBLOBs);
//            for (Object e2 : respResult_AddBug.getJSONObject("Response").getJSONObject("Data").getJSONArray("List")) {
//                JSONObject e3 = JSONObject.parseObject(e2.toString());
//                JSONObject e4 = JSONObject.parseObject(e3.get("IssueTypeDetail").toString());
//                issuesWithBLOBs.setId((String) e3.get("Code"));
//                issuesWithBLOBs.setNum((Integer) e4.get("Id"));
//                issuesWithBLOBs.setProjectId(projectId);
//                issuesWithBLOBs.setDescription((String) e3.get("Description"));
//                issuesWithBLOBs.setTitle((String) e3.get("Name"));
//                issuesWithBLOBs.setStatus((String) e3.get("IssueStatusName"));
//                issuesWithBLOBs.setCreator((String) member.get(e3.get("CreatorId")));
//                issuesWithBLOBs.setLastmodify((String) member.get(e3.get("AssigneeId")));
//                issuesWithBLOBs.setCreateTime(e3.get("CreatedAt"));
//
//
//            }
//            if (PageNumber > (Integer) respResult_AddBug.getJSONObject("Response").getJSONObject("Data").get("TotalPage")){
//                return;
//            }
//            else {
//                PageNumber = PageNumber +1;
//            }
//
//        }



    }


//    JSONObject respResult_AddBug = this.codingGetProjectIssueList(jsonString1 ,hashMap.get("token") );
}
