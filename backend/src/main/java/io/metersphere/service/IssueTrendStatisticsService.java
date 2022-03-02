package io.metersphere.service;

import com.alibaba.fastjson.JSONObject;
import com.arronlong.httpclientutil.exception.HttpProcessException;
import io.metersphere.base.domain.IssueTrend;
import io.metersphere.base.domain.IssueTrendExample;
import io.metersphere.base.mapper.IssueTrendMapper;
import io.metersphere.commons.constants.MDTrend;
import io.metersphere.performance.base.TrendChartsData;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class IssueTrendStatisticsService extends Thread{

    private String jsonString ;
    private String projectId ;
    private String mapKey ;
    private Map<String,String> testMap ;
    @Autowired
    public IssueTrendMapper issueTrendMapper;
//    public IssueTrendStatisticsService(String jsonString,String projectId,String mapKey,Map<String,String> testMap){
//        this.jsonString = jsonString ;      // 通过构造方法配置name属性
//        this.projectId = projectId ;      // 通过构造方法配置name属性
//        this.mapKey = mapKey ;      // 通过构造方法配置name属性
//        this.testMap = testMap ;      // 通过构造方法配置name属性
//    }

    //    IssueTrendStatisticsService
    public void addProjectIssueTrend(IssueTrend issueTrend) {
        if (null == issueTrend.getIssueTotal()) {
            issueTrend.setIssueTotal(0);
        }
        if (null == issueTrend.getResolutionIssueTotal()) {
            issueTrend.setResolutionIssueTotal(0);
        }
        if (null == issueTrend.getResolutionWeekIssue()) {
            issueTrend.setResolutionWeekIssue(0);
        }
        if (null == issueTrend.getResolutionHistoryIssue()) {
            issueTrend.setResolutionHistoryIssue(0);
        }
        issueTrendMapper.insert(issueTrend);
    }

    public boolean updateIssueTrend(IssueTrend issueTrend) {
        if (null == issueTrend.getId()) {
            return false;
        }
        if (issueTrend.getId() == 0) {
            return false;
        }
        issueTrend.setUpdateTime(new Date().getTime());
        int update = issueTrendMapper.updateByPrimaryKey(issueTrend);
        return update > 0;
    }
    public static Date getThisWeekMonday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // 获得当前日期是一个星期的第几天
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        // 获得当前日期是一个星期的第几天
        int day = cal.get(Calendar.DAY_OF_WEEK);
        // 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
        return cal.getTime();
    }

    public List<IssueTrend> getIssueTrendByCurrentWeek() {
        LocalDate now = LocalDate.now();
        WeekFields weekFields = WeekFields.ISO;
        // 当前第几周
        int weekNumber = now.get(weekFields.weekOfWeekBasedYear());
        IssueTrendExample issueTrendExample = new IssueTrendExample();
        issueTrendExample.createCriteria().andIssueWeekEqualTo(weekNumber);
        // 取同一年数据
        List<IssueTrend> allIssueTrend = issueTrendMapper.selectByExample(issueTrendExample);
        return allIssueTrend.stream().filter(v -> {
            LocalDate start = LocalDate.parse(v.getStartWeekTime());
            LocalDate end = LocalDate.parse(v.getEndWeekTime());
            if (now.isAfter(start) && now.isBefore(end)) {
                return true;
            }
            return now.isEqual(start) || now.isEqual(end);
        }).collect(Collectors.toList());
    }

    public List<IssueTrend> getIssueTrendList() {
        return getIssueTrends();
    }

    public TrendChartsData getIssueTrendCharts() {
        List<IssueTrend> allIssueTrend = getIssueTrends();
        TrendChartsData chartsData = new TrendChartsData();
        // 目睹云-新增bug, WeLink-新增bug, 直播-新增bug, 企播-新增bug, 老企播-新增bug
        List<String> legend = Arrays.stream(MDTrend.values())
                .sorted(Comparator.comparing(MDTrend::getIndex))
                .map(MDTrend::getValue).collect(Collectors.toList());
        chartsData.setLegend(legend);
        // x轴
        List<String> xAxis = allIssueTrend.stream().map(v -> String.format("%s-%s周", v.getIssueYear(), v.getIssueWeek())).distinct().collect(Collectors.toList());
        chartsData.setXAxis(xAxis);
        //
        List<IssueTrend> MDR = allIssueTrend.stream().filter(v -> v.getJiraKey().equals(MDTrend.MDR.getName())).collect(Collectors.toList());
        List<IssueTrend> MDW = allIssueTrend.stream().filter(v -> v.getJiraKey().equals(MDTrend.MDW.getName())).collect(Collectors.toList());
        List<IssueTrend> MDL = allIssueTrend.stream().filter(v -> v.getJiraKey().equals(MDTrend.MDL.getName())).collect(Collectors.toList());
        List<IssueTrend> MDE = allIssueTrend.stream().filter(v -> v.getJiraKey().equals(MDTrend.MDE.getName())).collect(Collectors.toList());
        List<IssueTrend> MDN = allIssueTrend.stream().filter(v -> v.getJiraKey().equals(MDTrend.MDN.getName())).collect(Collectors.toList());
        // addBug
        List<Map<String, List<Integer>>> addBugs = new ArrayList<>();
        addBugSeries(MDTrend.MDR.getValue(), MDR, addBugs);
        addBugSeries(MDTrend.MDW.getValue(), MDW, addBugs);
        addBugSeries(MDTrend.MDL.getValue(), MDL, addBugs);
        addBugSeries(MDTrend.MDE.getValue(), MDE, addBugs);
        addBugSeries(MDTrend.MDN.getValue(), MDN, addBugs);
        chartsData.setSeriesAddBug(addBugs);
        // fixBug
        List<Map<String, List<Integer>>> fixBugs = new ArrayList<>();
        fixBugSeries(MDTrend.MDR.getValue(), MDR, fixBugs);
        fixBugSeries(MDTrend.MDW.getValue(), MDW, fixBugs);
        fixBugSeries(MDTrend.MDL.getValue(), MDL, fixBugs);
        fixBugSeries(MDTrend.MDE.getValue(), MDE, fixBugs);
        fixBugSeries(MDTrend.MDN.getValue(), MDN, fixBugs);
        chartsData.setSeriesFixBug(fixBugs);
        return chartsData;
    }

    public List<IssueTrend> getIssueTrends() {
        LocalDate now = LocalDate.now();
        WeekFields weekFields = WeekFields.ISO;
        int weekNumber = now.get(weekFields.weekOfWeekBasedYear());
        int yearNumber = now.getYear();
        List<Integer> monthList = new ArrayList<>();
        monthList.add(weekNumber);
        if (weekNumber < 4) {
            // 处理跨年
            IssueTrendExample issueTrendExample = new IssueTrendExample();
            if (weekNumber - 1 == 0) {
                issueTrendExample.createCriteria().andIssueYearEqualTo(yearNumber).andIssueWeekIn(monthList);
                return issueTrendMapper.selectByExample(issueTrendExample);
            }
            if (weekNumber - 1 > 0) {
                for (int i = 1; i < 3; i++) {
                    if (weekNumber - i != 0) {
                        monthList.add(weekNumber - i);
                    }
                }
                issueTrendExample.createCriteria().andIssueYearEqualTo(yearNumber).andIssueWeekIn(monthList);
                issueTrendExample.setOrderByClause("issue_week ASC");
                return issueTrendMapper.selectByExample(issueTrendExample);
            }
        }
        monthList.add(weekNumber - 1);
        monthList.add(weekNumber - 2);
        monthList.add(weekNumber - 3);
        IssueTrendExample issueTrendExample = new IssueTrendExample();
        issueTrendExample.createCriteria().andIssueYearEqualTo(yearNumber).andIssueWeekIn(monthList);
        issueTrendExample.setOrderByClause("issue_week ASC");
        return issueTrendMapper.selectByExample(issueTrendExample);
    }
    public Map<String, Object> fromProjectUnresolved(String youToken,String projectName){
        Map<String, Object> modulName = new HashMap<>();
        String jsonString1 = String.format("{\"Action\": \"DescribeIssueListWithPage\", \t\"ProjectName\": \"%s\", \t\"IssueType\": \"DEFECT\", \t\"PageNumber\": 1, \t\"PageSize\": 500  }", projectName);

        JSONObject respResult_AddBug = this.codingGetProjectIssueList(jsonString1,youToken );
//        JSONObject respResult_AddBug = this.codingGetProjectIssueList(jsonString1,e1.get("id").toString(),hashMap.get("token") );
        if (respResult_AddBug == null){
//            modulName.add(testMap);
            return modulName;
        }
//        //System.out.println(end);
//        //System.out.println(start);
        Map<String,Object> testMap2 = new HashMap<>();
        Integer a4 = 0;
        //System.out.println(respResult_AddBug.getJSONObject("Response").getJSONObject("Data"));
        modulName.put("all_issue",respResult_AddBug.getJSONObject("Response").getJSONObject("Data").get("TotalCount"));
        for (Object e2 : respResult_AddBug.getJSONObject("Response").getJSONObject("Data").getJSONArray("List")) {
            JSONObject e3 = JSONObject.parseObject(e2.toString());
            if (e3.get("IssueStatusId").equals(43257745) || e3.get("IssueStatusId").equals(43257752)|| e3.get("IssueStatusId").equals(43257749)){
                a4 = a4 +1;

            }
            modulName.put("all_unresolved_issue",a4);


        }
        return modulName;



    }
    public  ArrayList<Object> fromProjectBugCheckNull(String youToken,String projectName){
        Map<Object, Object> member = this.getAllprojectMember(  youToken);
//        Map<String, Object> modulName = new HashMap<>();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd ");
        ArrayList<Object> testName = new ArrayList<>();
        String jsonString1 = String.format("{\"Action\": \"DescribeIssueListWithPage\", 	\"ProjectName\": \"%s\", 	\"IssueType\": \"DEFECT\", 	\"PageNumber\": 1, 	\"PageSize\": 500  }",  projectName);

        JSONObject respResult_AddBug = this.codingGetProjectIssueList(jsonString1,youToken );

        if (respResult_AddBug == null){
//            modulName.add(testMap);
            return testName;
        }
//        //System.out.println(end);
//        //System.out.println(start);
//        Map<String,Object> testMap2 = new HashMap<>();
        Integer a4 = 0;
//        modulName.put("all_issue",respResult_AddBug.getJSONObject("data").get("totalRow"));


        for (Object e2 : respResult_AddBug.getJSONObject("Response").getJSONObject("Data").getJSONArray("List")) {
            Map<String, Object> modulName1 = new HashMap<>();
            JSONObject e3 = JSONObject.parseObject(e2.toString());
//            JSONObject e4 = JSONObject.parseObject(e3.get("assignee") .toString());
//            JSONObject e5 = JSONObject.parseObject(e3.get("issueStatus") .toString());
            if ((Integer)e3.get("IssueStatusId")== 43257745 ||(Integer)e3.get("IssueStatusId")== 43257752 ||(Integer)e3.get("IssueStatusId")== 43257749||(Integer)e3.get("IssueStatusId")== 43257750){
                if (e3.get("StartDate").equals(0) || e3.get("DueDate").equals(0)) {
                    a4 = a4 + 1;
                    modulName1.put("name", e3.get("Name"));
                    modulName1.put("startDate", df.format(e3.get("StartDate")));
                    modulName1.put("dueDate", e3.get("DueDate"));
                    modulName1.put("assignee", member.get(e3.get("AssigneeId")));
                    modulName1.put("issueStatus", e3.get("IssueStatusName"));
                    testName.add(modulName1);
                }
//
//            }
//           }
//                modulName.put("dueDateNullList",testName);
//                modulName.put("dueDateNull", a4.toString());


            }

        }
        return testName;


    }
    public ArrayList<Object> fromProjectBugCheck(String youToken,String projectName,String duation){
        Map<Object, Object> member = this.getAllprojectMember(youToken);
//        Map<String, Object> modulName = new HashMap<>();
        ArrayList<Object> testName = new ArrayList<>();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd ");
        String jsonString1 = String.format("{\"Action\": \"DescribeIssueListWithPage\", 	\"ProjectName\": \"%s\", 	\"IssueType\": \"DEFECT\", 	\"PageNumber\": 1, 	\"PageSize\": 500  }",  projectName);

        JSONObject respResult_AddBug = this.codingGetProjectIssueList(jsonString1,youToken );

        if (respResult_AddBug == null){
//            modulName.add(testMap);
            return testName;
        }
//        //System.out.println(end);
//        //System.out.println(start);
//        Map<String,Object> testMap2 = new HashMap<>();
        Integer a4 = 0;
//        modulName.put("all_issue",respResult_AddBug.getJSONObject("data").get("totalRow"));
        //System.out.println(respResult_AddBug);


        for (Object e2 : respResult_AddBug.getJSONObject("Response").getJSONObject("Data").getJSONArray("List")) {
            Map<String, Object> modulName1 = new HashMap<>();
            JSONObject e3 = JSONObject.parseObject(e2.toString());
//            JSONObject e4 = JSONObject.parseObject(e3.get("assignee") .toString());
//            JSONObject e5 = JSONObject.parseObject(e3.get("issueStatus") .toString());
            if ( !e3.get("DueDate").equals(0) ) {
                //System.out.println(e3.get("DueDate") );
                if ( (long)e3.get("CompletedAt") -(long)e3.get("DueDate")  > Integer.parseInt(duation)*24*3600*1000){
                    a4 = a4 +1;
                    modulName1.put("name",e3.get("Name"));
                    modulName1.put("startDate",df.format(e3.get("StartDate")));
                    modulName1.put("dueDate",df.format(e3.get("DueDate")));
                    modulName1.put("assignee",member.get(e3.get("AssigneeId")));
                    modulName1.put("CompletedAt",df.format(e3.get("CompletedAt")));
                    modulName1.put("issueStatus",e3.get("IssueStatusName"));
                    testName.add(modulName1);
                }
//
//            }
//                modulName.put("dueDateList",testName);
//                modulName.put("count", a4.toString());


            }

        }
        return testName;


    }
    public ArrayList<Object> fromProjectBugCheckOvertime(String youToken,String projectName,String duation){
        Map<Object, Object> member = this.getAllprojectMember(  youToken);
//        Map<String, Object> modulName = new HashMap<>();
        ArrayList<Object> testName = new ArrayList<>();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd ");
        String jsonString1 = String.format("{\"Action\": \"DescribeIssueListWithPage\", 	\"ProjectName\": \"%s\", 	\"IssueType\": \"DEFECT\", 	\"PageNumber\": 1, 	\"PageSize\": 500  }",  projectName);

//        String jsonString1 = String.format("{\"page\":1,\"pageSize\":10000,\"content\":{\"sort\":{\"key\":\"PRIORITY\",\"value\":\"DESC\"},\"conditions\":[{\"key\":\"STATUS_TYPE\",\"customFieldId\":null,\"value\":[],\"fixed\":true,\"constValue\":[],\"userMap\":{\"COMPLETED\":{\"value\":\"COMPLETED\"},\"PROCESSING\":{\"value\":\"PROCESSING\"},\"TODO\":{\"value\":\"TODO\"}},\"validInfo\":[]},{\"key\":\"KEYWORD\",\"customFieldId\":null,\"value\":null,\"fixed\":true},{\"key\":\"STATUS\",\"customFieldId\":null,\"value\":[43257750],\"fixed\":false,\"userMap\":{\"43257750\":{\"value\":43257750}},\"validInfo\":[]}]}}",  "");

        JSONObject respResult_AddBug = this.codingGetProjectIssueList(jsonString1,youToken );

        if (respResult_AddBug == null){
//            modulName.add(testMap);
            return testName;
        }
//        //System.out.println(end);
//        //System.out.println(start);
//        Map<String,Object> testMap2 = new HashMap<>();
        Integer a4 = 0;
//        modulName.put("all_issue",respResult_AddBug.getJSONObject("data").get("totalRow"));

        //System.out.println(respResult_AddBug);

        for (Object e2 : respResult_AddBug.getJSONObject("Response").getJSONObject("Data").getJSONArray("List")) {
            Map<String, Object> modulName1 = new HashMap<>();
            JSONObject e3 = JSONObject.parseObject(e2.toString());
            //System.out.println(e3);

//            JSONObject e4 = JSONObject.parseObject(e3.get("assignee") .toString());
//            JSONObject e5 = JSONObject.parseObject(e3.get("issueStatus") .toString());
//            JSONObject e6 = JSONObject.parseObject(e3.get("creator") .toString());
//            if ( e3.get("dueDate") != null) {
            if ((Integer)e3.get("IssueStatusId") == 43257750) {
                if (System.currentTimeMillis() - (long) e3.get("UpdatedAt") > (long) Integer.parseInt(duation) * 3600 * 24*1000) {
                    a4 = a4 + 1;
                    modulName1.put("name", e3.get("Name"));
                    modulName1.put("creator", member.get(e3.get("CreatorId")));

//                    modulName1.put("assignee", e4.get("name"));
                    modulName1.put("assignee",member.get(e3.get("AssigneeId")));
                    modulName1.put("UpdatedAt", df.format(e3.get("UpdatedAt")));
                    modulName1.put("issueStatus", e3.get("IssueStatusName"));
                    testName.add(modulName1);
                }
            }
//
//            }
//                modulName.put("dueDateList",testName);
//                modulName.put("bug_check_overtime", a4.toString());


        }

//        }
        return testName;


    }

    public JSONObject codingGetProjectAll(String youToken)  {
        JSONObject json_test = null;
        String url = "https://mudu1.coding.net/open-api";
        String  jsonString = "{\"Action\": \"DescribeCodingProjects\",   \"PageNumber\": 1,   \"PageSize\": 100 }";
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
//            for (Object e2 : json_test.getJSONObject("Response").getJSONObject("Data").getJSONArray("TeamMembers")) {
//                JSONObject e3 = JSONObject.parseObject(e2.toString());
//                member.put(e3.get("Id"),e3.get("Name"));
//
//
//            }

            return json_test;

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return json_test;
    }
    public JSONObject checkToken( String youToken) {
        String url = String.format("http://mudu1.coding.net/api/me", projectId);
        JSONObject json_test = null;

        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        String result = "";
        try {
            // 通过址默认配置创建一个httpClient实例
            httpClient = HttpClients.createDefault();
            // 创建httpGet远程连接实例
            HttpGet httpGet = new HttpGet(url);
            // 设置请求头信息，鉴权
            httpGet.setHeader("Authorization", "token "+youToken);
            // 设置配置请求参数
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000)// 连接主机服务超时时间
                    .setConnectionRequestTimeout(35000)// 请求超时时间
                    .setSocketTimeout(60000)// 数据读取超时时间
                    .build();
            // 为httpGet实例设置配置
            httpGet.setConfig(requestConfig);
            // 执行get请求得到返回对象
            response = httpClient.execute(httpGet);
            // 通过返回对象获取返回数据
            HttpEntity entity = response.getEntity();
            // 通过EntityUtils中的toString方法将结果转换为字符串
            result = EntityUtils.toString(entity);
            json_test = JSONObject.parseObject(result);
//            #//System.out.println(json_test);
//            for (Object e : json_test.getJSONArray("data")) {
//                JSONObject e1 = JSONObject.parseObject(e.toString());
//                #//System.out.println(e1.get("display_name").toString());
//            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json_test;
    }
    private HashMap<Object, Object> getAllprojectMember( String youToken) {
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
    @Async
    public Future<Map<String,Object>> AsytGetIssueTotal(String token,  Object e,long start ,long end){

//        long start = System.currentTimeMillis( );
        Future<Map<String,Object>> returnmsg;

        Integer a1 = 0;
        Integer a2 = 0;
        Integer a3 = 0;
        Integer a4 = 0;
        Integer a5 = 0;
        Map<String,Object> testMap = new HashMap<>();
        Map<String,String> testMap2 = new HashMap<>();
        JSONObject e1 = JSONObject.parseObject(e.toString());
//        testMap.put("project",e1.get("display_name").toString());
        String jsonString1 = String.format("{\"Action\": \"DescribeIssueListWithPage\", 	\"ProjectName\": \"%s\", 	\"IssueType\": \"DEFECT\", 	\"PageNumber\": 1, 	\"PageSize\": 500  }",  e1.get("Name"));

//        System.out.println(jsonString1);
        JSONObject respResult_AddBug = this.codingGetProjectIssueList(jsonString1,token);
        if (respResult_AddBug.getJSONObject("Response").getJSONObject("Data") == null){
            //                JSONObject json_AddBug = JSONObject.parseObject(respResult_AddBug.getResult());
            testMap2.put("new_create_issue",a1.toString());

            testMap2.put("week_resolved_week_issue",a2.toString());
//
//                JSONObject respResult_RepairHistoryBug = this.codingGetProjectIssueList(jsonString3,e1.get("id").toString());
////                JSONObject json_RepairHistoryBug = JSONObject.parseObject(respResult_RepairHistoryBug.getResult());
            testMap2.put("week_resolved_history_issue", a3 .toString());
//
//                JSONObject respResult_noRepairBug = this.codingGetProjectIssueList(jsonString4,e1.get("id").toString());
////                JSONObject json_noRepairBug = JSONObject.parseObject(respResult_noRepairBug.getResult());
            testMap2.put("all_unresolved_issue",a4.toString());



//                Integer RepairBug;
//                RepairBug = a3;
            testMap2.put("week_resolved_issue",a5.toString());
            testMap.put(e1.get("Name").toString(),testMap2);
            returnmsg=new AsyncResult(testMap);
            return returnmsg;

//            testMap.put("error","token异常");
//            modulName.add(testMap);

        }
        else {
            for (Object e2 : respResult_AddBug.getJSONObject("Response").getJSONObject("Data").getJSONArray("List")) {
                JSONObject e3 = JSONObject.parseObject(e2.toString());
                if (((long)e3.get("CreatedAt") < end+24*3600*1000-600) && ((long)e3.get("CreatedAt") > start)){
                    a1 = a1 +1 ;
//                        //System.out.println(e3.get("code"));
                    if ( e3.get("IssueStatusId").equals(43257756)){

                        a2 = a2 +1;
                    }
                }
                else {

                    if (e3.get("IssueStatusId").equals(43257756) ){
                        if(((long)e3.get("CompletedAt") < end+24*3600*1000-600) && ((long)e3.get("CompletedAt") > start-+24*3600*1000)){

                            a3 = a3 +1;
                        }
                    }

                }
                if ((long)e3.get("CreatedAt") < end+24*3600*1000-600){

                    if (e3.get("IssueStatusId").equals(43257756) ){
                        if(((long)e3.get("CompletedAt") < end+24*3600*1000-600) && ((long)e3.get("CompletedAt") > start-24*3600*1000)){

                            a5 = a5 +1;
                        }
                    }

                    if (e3.get("IssueStatusId").equals(43257745) || e3.get("IssueStatusId").equals(43257752)|| e3.get("IssueStatusId").equals(43257749)){
                        a4 = a4 +1;


                    }
                }
            }
//                JSONObject json_AddBug = JSONObject.parseObject(respResult_AddBug.getResult());
            testMap2.put("new_create_issue",a1.toString());

            testMap2.put("week_resolved_week_issue",a2.toString());
//
//                JSONObject respResult_RepairHistoryBug = this.codingGetProjectIssueList(jsonString3,e1.get("id").toString());
////                JSONObject json_RepairHistoryBug = JSONObject.parseObject(respResult_RepairHistoryBug.getResult());
            testMap2.put("week_resolved_history_issue", a3 .toString());
//
//                JSONObject respResult_noRepairBug = this.codingGetProjectIssueList(jsonString4,e1.get("id").toString());
////                JSONObject json_noRepairBug = JSONObject.parseObject(respResult_noRepairBug.getResult());
            testMap2.put("all_unresolved_issue",a4.toString());



//                Integer RepairBug;
//                RepairBug = a3;
            testMap2.put("week_resolved_issue",a5.toString());
            testMap.put(e1.get("Name").toString(),testMap2);
        }
        returnmsg=new AsyncResult(testMap);
        return returnmsg;
    }

    public List<Map<String,Object>> getIssueTrendTotal(HashMap<String, String> hashMap) throws HttpProcessException, ExecutionException, InterruptedException, ParseException {
        String currentTimeNow = null;
        String currentTime = null;
        long start = 0 ;
        long end = 0;
        Date date ;
        ArrayList<Map<String, Object>> modulName = new ArrayList<>();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        if (hashMap.get("startDate") != null){
            currentTime = hashMap.get("startDate");
            date = df.parse(currentTime);
            start = date.getTime();
            if (hashMap.get("endDate") != null){
                currentTimeNow = hashMap.get("endDate");
//                Calendar nowTimeNow = Calendar.getInstance();

//                currentTimeNow = df.format(nowTimeNow.getTime());
                date = df.parse(currentTimeNow);
                end = date.getTime();
            }
            else {
                Calendar nowTime = Calendar.getInstance();

                currentTimeNow = df.format(nowTime.getTime());
                end = System.currentTimeMillis( );
            }

        }
        else {
            Calendar nowTimeNow = Calendar.getInstance();

            currentTimeNow = df.format(nowTimeNow.getTime());
            date = df.parse(currentTimeNow);
            start = df.parse(df.format(getThisWeekMonday(date))).getTime();

            if (hashMap.get("endDate") != null){
                currentTimeNow = hashMap.get("endDate");
//                Calendar nowTimeNow = Calendar.getInstance();

//                currentTimeNow = df.format(nowTimeNow.getTime());
                date = df.parse(currentTimeNow);
                end = date.getTime();
//                currentTimeNow = hashMap.get("endDate");
//                date = df.parse(currentTimeNow);
//                start = df.parse(df.format(getThisWeekMonday(date))).getTime();
            }
            else {
                Calendar nowTime = Calendar.getInstance();

                currentTimeNow = df.format(nowTime.getTime());
                end = System.currentTimeMillis( );
            }

        }


//        JSONObject respResult = this.codingGetProjectAll(hashMap.get("token"));

//        respResult.getResult();
        //本周新增BUG
        //        //本周解决本周BUG
//        String jsonString2 = String.format("{\"page\":1,\"pageSize\":100,\"content\":{\"sort\":{\"key\":\"PRIORITY\",\"value\":\"DESC\"},\"conditions\":[{\"key\":\"CREATED_AT\",\"customFieldId\":null,\"value\":{\"startDate\":\"%s\",\"endDate\":\"%s\"},\"fixed\":false},{\"key\":\"STATUS\",\"customFieldId\":null,\"value\":[43257750,43257751,43257756],\"fixed\":false,\"userMap\":{\"43257750\":{\"value\":43257750},\"43257751\":{\"value\":43257751},\"43257756\":{\"value\":43257756}},\"validInfo\":[]}]}}", currentTime, currentTimeNow);
//        //本周解决历史BUG
//        String jsonString3 = String.format("{\"page\":1,\"pageSize\":100,\"content\":{\"sort\":{\"key\":\"PRIORITY\",\"value\":\"DESC\"},\"conditions\":[{\"key\":\"CREATED_AT\",\"customFieldId\":null,\"value\":{\"startDate\":\"%s\",\"endDate\":\"%s\"},\"fixed\":false},{\"key\":\"STATUS\",\"customFieldId\":null,\"value\":[43257750,43257751,43257756],\"fixed\":false,\"userMap\":{\"43257750\":{\"value\":43257750},\"43257751\":{\"value\":43257751},\"43257756\":{\"value\":43257756}},\"validInfo\":[]}]}}", currentTimelastYear, currentTime);
//        //所有未解决BUG
//        String jsonString4 = String.format("{\"page\":1,\"pageSize\":100,\"content\":{\"sort\":{\"key\":\"PRIORITY\",\"value\":\"DESC\"},\"conditions\":[{\"key\":\"CREATED_AT\",\"customFieldId\":null,\"value\":{\"startDate\":\"%s\",\"endDate\":\"%s\"},\"fixed\":false},{\"key\":\"STATUS\",\"customFieldId\":null,\"value\":[43257745,43257752,43257749],\"fixed\":false,\"userMap\":{\"43257749\":{\"value\":43257749}},\"validInfo\":[]}]}}", currentTimelastYear, currentTime);



//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

//        //System.out.println(start);
//        //System.out.println(start-7*24*3600*1000-10*3600*1000);
//        JSONObject json_test = JSONObject.parseObject(respResult.getResult());
//        for (Object e:respResult.getJSONArray("Data")) {
        Integer a1 = 0;
        Integer a2 = 0;
        Integer a3 = 0;
        Integer a4 = 0;
        Integer a5 = 0;
        Map<String,Object> testMap = new HashMap<>();
//            JSONObject e1 = JSONObject.parseObject(e.toString());

        if ((hashMap.get("projectName") != null) ){
//                testMap.put("project",e1.get("display_name").toString());
            String jsonString1 = String.format("{\"Action\": \"DescribeIssueListWithPage\", 	\"ProjectName\": \"%s\", 	\"IssueType\": \"DEFECT\", 	\"PageNumber\": 1, 	\"PageSize\": 500  }",  hashMap.get("projectName"));


            JSONObject respResult_AddBug = this.codingGetProjectIssueList(jsonString1 ,hashMap.get("token") );
            if (respResult_AddBug == null){
                modulName.add(testMap);
                return modulName;
            }
            //System.out.println(end);
            //System.out.println(start);
            Map<String,Object> testMap2 = new HashMap<>();
            for (Object e2 : respResult_AddBug.getJSONObject("Response").getJSONObject("Data").getJSONArray("List")) {
                JSONObject e3 = JSONObject.parseObject(e2.toString());
                if (((long)e3.get("CreatedAt") < end+24*3600*1000-600) && ((long)e3.get("CreatedAt") > start)){
                    a1 = a1 +1 ;
//                        //System.out.println(e3.get("code"));
                    if ( e3.get("IssueStatusId").equals(43257756)){

                        a2 = a2 +1;
                    }
                }
                else {

                    if (e3.get("IssueStatusId").equals(43257756) ){
                        if(((long)e3.get("CompletedAt") < end+24*3600*1000-600) && ((long)e3.get("CompletedAt") > start-+24*3600*1000)){

                        a3 = a3 +1;
                    }
                    }

                }
                if ((long)e3.get("CreatedAt") < end+24*3600*1000-600){

                    if (e3.get("IssueStatusId").equals(43257756) ){
                        if(((long)e3.get("CompletedAt") < end+24*3600*1000-600) && ((long)e3.get("CompletedAt") > start-24*3600*1000)){
                            System.out.println(e3);

                        a5 = a5 +1;
                    }
                    }

                    if (e3.get("IssueStatusId").equals(43257745) || e3.get("IssueStatusId").equals(43257752)|| e3.get("IssueStatusId").equals(43257749)){
                        a4 = a4 +1;


                    }
                }
            }
//                JSONObject json_AddBug = JSONObject.parseObject(respResult_AddBug.getResult());
            testMap2.put("new_create_issue",a1.toString());

            testMap2.put("week_resolved_week_issue",a2.toString());
//
//                JSONObject respResult_RepairHistoryBug = this.codingGetProjectIssueList(jsonString3,e1.get("id").toString());
////                JSONObject json_RepairHistoryBug = JSONObject.parseObject(respResult_RepairHistoryBug.getResult());
            testMap2.put("week_resolved_history_issue", a3 .toString());
//
//                JSONObject respResult_noRepairBug = this.codingGetProjectIssueList(jsonString4,e1.get("id").toString());
////                JSONObject json_noRepairBug = JSONObject.parseObject(respResult_noRepairBug.getResult());
            testMap2.put("all_unresolved_issue",a4.toString());



//                Integer RepairBug;
//                RepairBug = a3;
            testMap2.put("week_resolved_issue",a5.toString());
            testMap.put(hashMap.get("projectName"),testMap2);
            modulName.add(testMap);
            return modulName;

        }
        else if(hashMap.get("projectName") == null ){
            Future<Map<String,Object>> future = null;
//                long start1 = System.currentTimeMillis( );
            JSONObject respResult = this.codingGetProjectAll(hashMap.get("token"));
            for (Object e:respResult.getJSONObject("Response").getJSONObject("Data").getJSONArray("ProjectList")) {
//
                future=this.AsytGetIssueTotal(hashMap.get("token"),e,start,end);
                modulName.add(future.get());
            }
//
////                //System.out.println(future.get());
////                Map<String,String> hashMapNew = JSON.parseObject(future.get().replace("=",":"), HashMap.class);
////                Map<String, String> hashMapNew = this.getStringToMap(future.get());

//                long end1 = System.currentTimeMillis( );
//                //System.out.println(end1-start1);
//
//
        }



//        }
        return modulName;

    }


    private void addBugSeries(String name, List<IssueTrend> trendList, List<Map<String, List<Integer>>> addBugs) {
        if (trendList.size() == 0) {
            return;
        }
        handleSeries(name, addBugs, trendList.stream().map(IssueTrend::getIssueTotal));
    }

    private void fixBugSeries(String name, List<IssueTrend> trendList, List<Map<String, List<Integer>>> fixBugs) {
        if (trendList.size() == 0) {
            return;
        }
        handleSeries(name, fixBugs, trendList.stream().map(IssueTrend::getResolutionIssueTotal));
    }

    private void handleSeries(String name, List<Map<String, List<Integer>>> addBugs, Stream<Integer> integerStream) {
        List<Integer> fixBug = integerStream.collect(Collectors.toList());
        Map<String, List<Integer>> mapList = new HashMap<>();
        mapList.put(name, fixBug);
        addBugs.add(mapList);
    }

}
