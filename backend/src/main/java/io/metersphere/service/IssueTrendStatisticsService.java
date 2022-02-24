package io.metersphere.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.arronlong.httpclientutil.HttpClientUtil;
import com.arronlong.httpclientutil.builder.HCB;
import com.arronlong.httpclientutil.common.HttpConfig;
import com.arronlong.httpclientutil.common.HttpHeader;
import com.arronlong.httpclientutil.common.HttpResult;
import com.arronlong.httpclientutil.common.SSLs;
import com.arronlong.httpclientutil.exception.HttpProcessException;
import io.metersphere.base.domain.IssueTrend;
import io.metersphere.base.domain.IssueTrendExample;
import io.metersphere.base.mapper.IssueTrendMapper;
import io.metersphere.commons.constants.MDTrend;
import io.metersphere.performance.base.TrendChartsData;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
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

    public JSONObject codingGetProjectAll(String youToken)  {
        JSONObject json_test = null;
        String url = "http://mudu1.coding.net/api/project_recent_views/query?pmType=PROJECT";
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
//            #System.out.println(json_test);
//            for (Object e : json_test.getJSONArray("data")) {
//                JSONObject e1 = JSONObject.parseObject(e.toString());
//                #System.out.println(e1.get("display_name").toString());
//            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json_test;
//        String url = "http://mudu1.coding.net/api/project_recent_views/query?pmType=PROJECT";
//        HCB hcb = HCB.custom()
//                .timeout(1000) //超时
//                .pool(100, 10) //启用连接池，每个路由最大创建10个链接，总连接数限制为100个
//                .sslpv(SSLs.SSLProtocolVersion.TLSv1_2) 	//设置ssl版本号，默认SSLv3，也可以调用sslpv("TLSv1.2")
//                .ssl()  	  	//https，支持自定义ssl证书路径和密码，ssl(String keyStorePath, String keyStorepass)
//                .retry(5)		//重试5次
//                ;
//
//        HttpClient client = hcb.build();
//
////        Map<String, Object> map = new HashMap<String, Object>();
////        map.put("Action", "DescribeCodingProjects");
////        map.put("PageNumber", 1);
////        map.put("PageSize", 100);
//        Header[] headers = HttpHeader.custom()
//                .authorization("token 877e0180347fa8d24500d7f4e8acd2683ac958da")
//                .build();
//
//
//
//        //插件式配置请求参数（网址、请求参数、编码、client）
//        HttpConfig config = HttpConfig.custom()
//                .headers(headers)
//                .url(url)	          //设置请求的url
////                .map(map)	          //设置请求参数，没有则无需设置
//                .encoding("utf-8") //设置请求和返回编码，默认就是Charset.defaultCharset()
//                .client(client)    //如果只是简单使用，无需设置，会自动获取默认的一个client对象
//                ;
//
//        HttpClientUtil.get(config);    //post请求
//        HttpResult respResult = HttpClientUtil.sendAndGetResp(config);
//        return respResult;
    }

    private JSONObject codingGetProjectIssueList( String jsonString,String projectId,String youToken) {
        String url = String.format("https://mudu1.coding.net/api/project/%s/issues/DEFECT/list", projectId);
//        #System.out.println(url);
//        #System.out.println(jsonString);
        JSONObject json_test = null;
//        String url = "http://mudu1.coding.net/api/project_recent_views/query?pmType=PROJECT";
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
    public Future<Map<String,String>> AsytGetIssueTotal(String token,  Object e,String currentTimeNow){

        long start = System.currentTimeMillis( );
        Future<Map<String,String>> returnmsg;
        String jsonString1 = String.format("{\"page\":1,\"pageSize\":10000,\"content\":{\"sort\":{\"key\":\"PRIORITY\",\"value\":\"DESC\"},\"conditions\":[{\"key\":\"CREATED_AT\",\"customFieldId\":null,\"value\":{\"startDate\":\"\",\"endDate\":\"%s\"},\"fixed\":false},{\"key\":\"STATUS\",\"customFieldId\":null,\"value\":[],\"fixed\":false,\"userMap\":{},\"validInfo\":[]}]}}",  currentTimeNow);

        Integer a1 = 0;
        Integer a2 = 0;
        Integer a3 = 0;
        Integer a4 = 0;
        Map<String,String> testMap = new HashMap<>();
        JSONObject e1 = JSONObject.parseObject(e.toString());
        testMap.put("projectName",e1.get("display_name").toString());

        JSONObject respResult_AddBug = this.codingGetProjectIssueList(jsonString1,e1.get("id").toString(),token);
        if (respResult_AddBug == null){
            testMap.put("error","token异常");
//            modulName.add(testMap);

        }
        else {
        for (Object e2 : respResult_AddBug.getJSONObject("data").getJSONArray("list")) {
            JSONObject e3 = JSONObject.parseObject(e2.toString());
            if (((Long)e3.get("createdAt") < start) && ((Long)e3.get("createdAt") > start - 3600 * 24 * 7 * 1000-10*3600*1000)){

                a1 = a1 +1 ;
                if (e3.get("issueStatusId").equals(43257750) || e3.get("issueStatusId").equals(43257751)|| e3.get("issueStatusId").equals(43257756)){

                    a2 = a2 +1;
                }
            }
            else {
                if (e3.get("issueStatusId").equals(43257750) || e3.get("issueStatusId").equals(43257751)|| e3.get("issueStatusId").equals(43257756)){

                    a3 = a3 +1;
                }

            }

            if (e3.get("issueStatusId").equals(43257745) || e3.get("issueStatusId").equals(43257752)|| e3.get("issueStatusId").equals(43257749)){
                a4 = a4 +1;


            }
        }
//                JSONObject json_AddBug = JSONObject.parseObject(respResult_AddBug.getResult());
        testMap.put("AddBug",a1.toString());
//
//                JSONObject respResult_RepairNewBug = this.codingGetProjectIssueList(jsonString2,e1.get("id").toString());
////                JSONObject json_RepairNewBug = JSONObject.parseObject(respResult_RepairNewBug.getResult());
        testMap.put("RepairNewBug",a2.toString());
//
//                JSONObject respResult_RepairHistoryBug = this.codingGetProjectIssueList(jsonString3,e1.get("id").toString());
////                JSONObject json_RepairHistoryBug = JSONObject.parseObject(respResult_RepairHistoryBug.getResult());
        testMap.put("RepairHistoryBug",String.valueOf((a3 - a1)));
//
//                JSONObject respResult_noRepairBug = this.codingGetProjectIssueList(jsonString4,e1.get("id").toString());
////                JSONObject json_noRepairBug = JSONObject.parseObject(respResult_noRepairBug.getResult());
        testMap.put("noRepairBug",a4.toString());



        Integer RepairBug;
        RepairBug = a3;
        testMap.put("RepairBug",RepairBug.toString());
        }
        returnmsg=new AsyncResult(testMap);
        return returnmsg;
    }
    public static Map<String,String> getStringToMap(String str){
        //根据逗号截取字符串数组
        String[] str1 = str.split(",");
        //创建Map对象
        Map<String,String> map = new HashMap<>();
        //循环加入map集合
        for (int i = 0; i < str1.length; i++) {
            //根据":"截取字符串数组
            String[] str2 = str1[i].split("=");
            //str2[0]为KEY,str2[1]为值
            //map.put(str2[0],str2[1]);
            if (str2.length == 2){
                map.put(str2[0].trim(),str2[1]);
            }else{
                map.put(str2[0].trim(),"");
            }
        }
        return map;
    }
    public List<Map<String,String>> getIssueTrendTotal(HashMap<String, String> hashMap) throws HttpProcessException, ExecutionException, InterruptedException {
        String currentTimeNow = null;
        ArrayList<Map<String,String>> modulName = new ArrayList<>();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        if (hashMap.get("startTime") != null){
//            currentTime = hashMap.get("startTime");
            if (hashMap.get("endTime") != null){
                currentTimeNow = hashMap.get("endTime");
            }
            else {
                Calendar nowTime = Calendar.getInstance();

                currentTimeNow = df.format(nowTime.getTime());
            }

        }
        else {
            Calendar nowTime2 = Calendar.getInstance();
            nowTime2.add(Calendar.DAY_OF_WEEK, -7);//30分钟前的时间
//            Calendar nowTime = Calendar.getInstance();
            if (hashMap.get("endTime") != null){
                currentTimeNow = hashMap.get("endTime");
            }
            else {
                Calendar nowTime = Calendar.getInstance();

                currentTimeNow = df.format(nowTime.getTime());
            }

        }


        JSONObject respResult = this.codingGetProjectAll(hashMap.get("yourToken"));

//        respResult.getResult();
        //本周新增BUG
        String jsonString1 = String.format("{\"page\":1,\"pageSize\":10000,\"content\":{\"sort\":{\"key\":\"PRIORITY\",\"value\":\"DESC\"},\"conditions\":[{\"key\":\"CREATED_AT\",\"customFieldId\":null,\"value\":{\"startDate\":\"\",\"endDate\":\"%s\"},\"fixed\":false},{\"key\":\"STATUS\",\"customFieldId\":null,\"value\":[],\"fixed\":false,\"userMap\":{},\"validInfo\":[]}]}}",  currentTimeNow);
//        //本周解决本周BUG
//        String jsonString2 = String.format("{\"page\":1,\"pageSize\":100,\"content\":{\"sort\":{\"key\":\"PRIORITY\",\"value\":\"DESC\"},\"conditions\":[{\"key\":\"CREATED_AT\",\"customFieldId\":null,\"value\":{\"startDate\":\"%s\",\"endDate\":\"%s\"},\"fixed\":false},{\"key\":\"STATUS\",\"customFieldId\":null,\"value\":[43257750,43257751,43257756],\"fixed\":false,\"userMap\":{\"43257750\":{\"value\":43257750},\"43257751\":{\"value\":43257751},\"43257756\":{\"value\":43257756}},\"validInfo\":[]}]}}", currentTime, currentTimeNow);
//        //本周解决历史BUG
//        String jsonString3 = String.format("{\"page\":1,\"pageSize\":100,\"content\":{\"sort\":{\"key\":\"PRIORITY\",\"value\":\"DESC\"},\"conditions\":[{\"key\":\"CREATED_AT\",\"customFieldId\":null,\"value\":{\"startDate\":\"%s\",\"endDate\":\"%s\"},\"fixed\":false},{\"key\":\"STATUS\",\"customFieldId\":null,\"value\":[43257750,43257751,43257756],\"fixed\":false,\"userMap\":{\"43257750\":{\"value\":43257750},\"43257751\":{\"value\":43257751},\"43257756\":{\"value\":43257756}},\"validInfo\":[]}]}}", currentTimelastYear, currentTime);
//        //所有未解决BUG
//        String jsonString4 = String.format("{\"page\":1,\"pageSize\":100,\"content\":{\"sort\":{\"key\":\"PRIORITY\",\"value\":\"DESC\"},\"conditions\":[{\"key\":\"CREATED_AT\",\"customFieldId\":null,\"value\":{\"startDate\":\"%s\",\"endDate\":\"%s\"},\"fixed\":false},{\"key\":\"STATUS\",\"customFieldId\":null,\"value\":[43257745,43257752,43257749],\"fixed\":false,\"userMap\":{\"43257749\":{\"value\":43257749}},\"validInfo\":[]}]}}", currentTimelastYear, currentTime);


        long start = System.currentTimeMillis( );
//        System.out.println(start-7*24*3600*1000-10*3600*1000);
//        JSONObject json_test = JSONObject.parseObject(respResult.getResult());
        for (Object e:respResult.getJSONArray("data")) {
            Integer a1 = 0;
            Integer a2 = 0;
            Integer a3 = 0;
            Integer a4 = 0;
            Map<String,String> testMap = new HashMap<>();
            JSONObject e1 = JSONObject.parseObject(e.toString());

            if ((hashMap.get("projectName") != null) && (hashMap.get("projectName").equals(e1.get("display_name").toString()))){
                testMap.put("projectName",e1.get("display_name").toString());

                JSONObject respResult_AddBug = this.codingGetProjectIssueList(jsonString1,e1.get("id").toString(),hashMap.get("yourToken") );
                if (respResult_AddBug == null){
                    testMap.put("error","token异常");
                    modulName.add(testMap);
                    return modulName;
                }
                for (Object e2 : respResult_AddBug.getJSONObject("data").getJSONArray("list")) {
                    JSONObject e3 = JSONObject.parseObject(e2.toString());
                    if (((Long)e3.get("createdAt") < start) && ((Long)e3.get("createdAt") > start-7*24*3600*1000-10*3600*1000)){
                        a1 = a1 +1 ;
//                        System.out.println(e3.get("code"));
                        if (e3.get("issueStatusId").equals(43257750) || e3.get("issueStatusId").equals(43257751)|| e3.get("issueStatusId").equals(43257756)){

                            a2 = a2 +1;
                        }
                    }
                    else {
                        if (e3.get("issueStatusId").equals(43257750) || e3.get("issueStatusId").equals(43257751)|| e3.get("issueStatusId").equals(43257756)){

                            a3 = a3 +1;
                        }

                    }

                    if (e3.get("issueStatusId").equals(43257745) || e3.get("issueStatusId").equals(43257752)|| e3.get("issueStatusId").equals(43257749)){
                        a4 = a4 +1;


                    }
                }
//                JSONObject json_AddBug = JSONObject.parseObject(respResult_AddBug.getResult());
                testMap.put("AddBug",a1.toString());
//
//                JSONObject respResult_RepairNewBug = this.codingGetProjectIssueList(jsonString2,e1.get("id").toString());
////                JSONObject json_RepairNewBug = JSONObject.parseObject(respResult_RepairNewBug.getResult());
                testMap.put("RepairNewBug",a2.toString());
//
//                JSONObject respResult_RepairHistoryBug = this.codingGetProjectIssueList(jsonString3,e1.get("id").toString());
////                JSONObject json_RepairHistoryBug = JSONObject.parseObject(respResult_RepairHistoryBug.getResult());
                testMap.put("RepairHistoryBug", String.valueOf((a3 - a1)));
//
//                JSONObject respResult_noRepairBug = this.codingGetProjectIssueList(jsonString4,e1.get("id").toString());
////                JSONObject json_noRepairBug = JSONObject.parseObject(respResult_noRepairBug.getResult());
                testMap.put("noRepairBug",a4.toString());



                Integer RepairBug;
                RepairBug = a3;
                testMap.put("RepairBug",RepairBug.toString());
                modulName.add(testMap);
                return modulName;

            }
            else if(hashMap.get("projectName") == null ){

                Future<Map<String,String>> future=this.AsytGetIssueTotal(hashMap.get("yourToken"),e,currentTimeNow);
                long end = System.currentTimeMillis( );
                System.out.println(future.get());
//                Map<String,String> hashMapNew = JSON.parseObject(future.get().replace("=",":"), HashMap.class);
//                Map<String, String> hashMapNew = this.getStringToMap(future.get());
                modulName.add(future.get());
                System.out.println(end-start);

//                testMap.put("projectName",e1.get("display_name").toString());
//                JSONObject respResult_AddBug = this.codingGetProjectIssueList(jsonString1,e1.get("id").toString(),hashMap.get("yourToken"));
//                if (respResult_AddBug == null){
//                    testMap.put("error","token异常");
//                    modulName.add(testMap);
//                    return modulName;
//                }
//                for (Object e2 : respResult_AddBug.getJSONObject("data").getJSONArray("list")) {
//                    JSONObject e3 = JSONObject.parseObject(e2.toString());
//                    if (((Long)e3.get("createdAt") < start) && ((Long)e3.get("createdAt") > start - 3600 * 24 * 7 * 1000-10*3600*1000)){
//
//                        a1 = a1 +1 ;
//                        if (e3.get("issueStatusId").equals(43257750) || e3.get("issueStatusId").equals(43257751)|| e3.get("issueStatusId").equals(43257756)){
//
//                            a2 = a2 +1;
//                        }
//                    }
//                    else {
//                        if (e3.get("issueStatusId").equals(43257750) || e3.get("issueStatusId").equals(43257751)|| e3.get("issueStatusId").equals(43257756)){
//
//                            a3 = a3 +1;
//                        }
//
//                    }
//
//                    if (e3.get("issueStatusId").equals(43257745) || e3.get("issueStatusId").equals(43257752)|| e3.get("issueStatusId").equals(43257749)){
//                        a4 = a4 +1;
//
//
//                    }
//                }
////                JSONObject json_AddBug = JSONObject.parseObject(respResult_AddBug.getResult());
//                testMap.put("AddBug",a1.toString());
////
////                JSONObject respResult_RepairNewBug = this.codingGetProjectIssueList(jsonString2,e1.get("id").toString());
//////                JSONObject json_RepairNewBug = JSONObject.parseObject(respResult_RepairNewBug.getResult());
//                testMap.put("RepairNewBug",a2.toString());
////
////                JSONObject respResult_RepairHistoryBug = this.codingGetProjectIssueList(jsonString3,e1.get("id").toString());
//////                JSONObject json_RepairHistoryBug = JSONObject.parseObject(respResult_RepairHistoryBug.getResult());
//                testMap.put("RepairHistoryBug",String.valueOf((a3 - a1)));
////
////                JSONObject respResult_noRepairBug = this.codingGetProjectIssueList(jsonString4,e1.get("id").toString());
//////                JSONObject json_noRepairBug = JSONObject.parseObject(respResult_noRepairBug.getResult());
//                testMap.put("noRepairBug",a4.toString());
//
//
//
//                Integer RepairBug;
//                RepairBug = a3;
//                testMap.put("RepairBug",RepairBug.toString());
//                modulName.add(testMap);

//                JSONObject respResult_AddBug = this.codingGetProjectIssueList(jsonString1,e1.get("id").toString());
////                JSONObject json_AddBug = JSONObject.parseObject(respResult_AddBug.getResult());
//                testMap.put("AddBug",respResult_AddBug.getJSONObject("data").getString("totalRow"));
//
//                JSONObject respResult_RepairNewBug = this.codingGetProjectIssueList(jsonString2,e1.get("id").toString());
////                JSONObject json_RepairNewBug = JSONObject.parseObject(respResult_RepairNewBug.getResult());
//                testMap.put("RepairNewBug",respResult_RepairNewBug.getJSONObject("data").getString("totalRow"));
//
//                JSONObject respResult_RepairHistoryBug = this.codingGetProjectIssueList(jsonString3,e1.get("id").toString());
////                JSONObject json_RepairHistoryBug = JSONObject.parseObject(respResult_RepairHistoryBug.getResult());
//                testMap.put("RepairHistoryBug",respResult_RepairHistoryBug.getJSONObject("data").getString("totalRow"));
//
//                JSONObject respResult_noRepairBug = this.codingGetProjectIssueList(jsonString4,e1.get("id").toString());
////                JSONObject json_noRepairBug = JSONObject.parseObject(respResult_noRepairBug.getResult());
//                testMap.put("noRepairBug",respResult_noRepairBug.getJSONObject("data").getString("totalRow"));
//
//
//
//                Integer RepairBug;
//                RepairBug = Integer.valueOf(respResult_RepairNewBug.getJSONObject("data").getString("totalRow"))
//                        + Integer.valueOf(respResult_RepairHistoryBug.getJSONObject("data").getString("totalRow"));
//                testMap.put("RepairBug",RepairBug.toString());
//                modulName.add(testMap);

            }



        }
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
