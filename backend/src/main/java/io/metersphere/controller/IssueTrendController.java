package io.metersphere.controller;


import com.alibaba.fastjson.JSONObject;
import com.arronlong.httpclientutil.exception.HttpProcessException;
import io.metersphere.base.domain.IssueTrend;

import io.metersphere.performance.base.TrendChartsData;
import io.metersphere.service.IssueTrendStatisticsService;
import io.metersphere.service.IssueTrendStatisticsServiceNew;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping(value = "/trend")
public class IssueTrendController {

    @Resource
    private IssueTrendStatisticsService issueTrendStatisticsService;
//    @Resource
//    private IssueTrendStatisticsServiceNew issueTrendStatisticsService;

    @GetMapping("/issue/charts")
    public TrendChartsData issueTrendChats() {

        return issueTrendStatisticsService.getIssueTrendCharts();
    }

    @GetMapping("/issue/list")
    public List<IssueTrend> issueTrendList() {

        return issueTrendStatisticsService.getIssueTrendList();
    }
    @GetMapping("/issue/total/projectquality/resolved/list")
    public ResultHolder issueTrendTotal(@RequestParam HashMap<String, String> hashMap)  {
        List<Map<String,Object>>  result = new ArrayList<>();
        try {

//            System.out.println(hashMap.get("token"));
            JSONObject resultToken = issueTrendStatisticsService.checkToken(hashMap.get("token"));
            System.out.println(resultToken);
            if(resultToken.get("team") == null ){
                return ResultHolder.selfInface(1,"fail","请检查环境或者个人令牌权限",0);
            }
            result = issueTrendStatisticsService.getIssueTrendTotal(hashMap);
//        if (result.size()==0){
//            return ResultHolder.selfInface(1,"fail","请检查环境或者个人令牌权限",0);
//        }
            return ResultHolder.selfInface(0,"success",result,result.size());
        }          catch (Exception e) {
            return ResultHolder.selfInface(1,"fail",result,0);
        }
    }
    //    @RequestMapping(value = "/queryStmp", method = RequestMethod.GET)
    @GetMapping("/issue/total/getAllProject")
    public ResultHolder getAllProject(@RequestParam HashMap<String, String> hashMap) throws HttpProcessException {
        ArrayList<Object>  testMap = new ArrayList<>();
        try {
            String token = "";
            if (hashMap.get("token") == null){
                token = "363fa5a5fdcc0dec164b9edfc0e1a2860728671c";
//            hashMap.get("token")
//            IssuesService issuesService = new IssuesService();

            }
            else {
                token = hashMap.get("token") ;
            }

            JSONObject resultToken = issueTrendStatisticsService.checkToken(token);
//        System.out.println(String.format("code1:%s,", hashMap.get("token")));
//        System.out.println(resultToken);
            if(resultToken.get("team") == null ){
                return ResultHolder.selfInface(1,"fail","请检查环境或者个人令牌权限",0);
            }



            JSONObject json_test =  issueTrendStatisticsService.codingGetProjectAll(token);
            System.out.println(json_test);
//        JSONObject json_test1 = JSONObject.parseObject(json_test.get("Response"));
            for (Object e:json_test.getJSONObject("Response").getJSONObject("Data").getJSONArray("ProjectList")) {
                Map<String,Object> modulName = new HashMap<>();
                JSONObject e1 = JSONObject.parseObject(e.toString());
                System.out.println(e1);
                modulName.put("displayName",  e1.get("DisplayName"));
                modulName.put("id", e1.get("Id"));
                modulName.put("projectName",e1.get("Name").toString());
                testMap.add( modulName);
            }
//        testMap.put("code",0);
//        testMap.put("msg","sueccess");
//        testMap.put("data",modulName);
//        return CommonResult.success(modulName);

            return ResultHolder.selfInface(0,"success",testMap,testMap.size());
        } catch (Exception e) {
            return ResultHolder.selfInface(1,"fail",testMap,0);
        }
    }
    @GetMapping("/issue/total/projectquality/unresolved/list")
    public ResultHolder fromProjectUnresolved(@RequestParam HashMap<String, String> hashMap) throws HttpProcessException {
        ArrayList<Object>  testMap = new ArrayList<>();
        try {


            JSONObject resultToken = issueTrendStatisticsService.checkToken(hashMap.get("token"));
            if(resultToken.get("team") == null ){
                return ResultHolder.selfInface(1,"fail","请检查环境或者个人令牌权限",0);
            }
            JSONObject json_test =  issueTrendStatisticsService.codingGetProjectAll(hashMap.get("token"));
            if(hashMap.get("projectName") == null){

                for (Object e2 : json_test.getJSONObject("Response").getJSONObject("Data").getJSONArray("ProjectList")) {
                    Map<String,Object> result = new HashMap<>();
                    HashMap<String, Object> newMap = new HashMap<>();
                    JSONObject e1 = JSONObject.parseObject(e2.toString());
                    try {
                        result = issueTrendStatisticsService.fromProjectUnresolved(hashMap.get("token"),e1.get("Name").toString());
                    } catch (Exception e) {
                        result.put("allIssue",0);
                        result.put("allUnresolvedIssue",0);
                        result.put("unresolvedIssuePercent",0);
                        e.printStackTrace();
                    }
                    newMap.put("data",result);
                    newMap.put("projectName",e1.get("Name").toString());
                    newMap.put("displayName",  e1.get("DisplayName"));
                    testMap.add(newMap);

                }
            }
            else {
                HashMap<String, Object> newMap = new HashMap<>();
                Map<String,Object> result = issueTrendStatisticsService.fromProjectUnresolved(hashMap.get("token"),hashMap.get("projectName").split(";")[0]);
                newMap.put("data",result);
                newMap.put("projectName",hashMap.get("projectName").toString().split(";")[0]);
                newMap.put("displayName",hashMap.get("projectName").toString().split(";")[1]);
                testMap.add(newMap);
            }
//        if (result.size()==0){
//            return ResultHolder.selfInface(1,"fail","请检查环境或者个人令牌权限",0);
//        }
            return ResultHolder.selfInface(0,"success",testMap,testMap.size());
        } catch (Exception e) {
            return ResultHolder.selfInface(1,"fail",testMap,0);
        }
    }
    @GetMapping("/issue/total/projectquality/duedate/null/list")
    public ResultHolder fromProjectBugCheckNull(@RequestParam HashMap<String, String> hashMap) throws HttpProcessException {
        ArrayList<Object>  testMap = new ArrayList<>();
        try {

            JSONObject resultToken = issueTrendStatisticsService.checkToken(hashMap.get("token"));
            if(resultToken.get("team") == null ){
                return ResultHolder.selfInface(1,"fail","请检查环境或者个人令牌权限",0);
            }
            if(hashMap.get("projectName") == null) {
                JSONObject json_test =  issueTrendStatisticsService.codingGetProjectAll(hashMap.get("token"));
                for (Object e2 : json_test.getJSONObject("Response").getJSONObject("Data").getJSONArray("ProjectList")) {
                    ArrayList<Object> result = new ArrayList<>();
                    HashMap<String, Object> newMap = new HashMap<>();
                    JSONObject e1 = JSONObject.parseObject(e2.toString());
                    try {
                        result = issueTrendStatisticsService.fromProjectBugCheckNull(hashMap.get("token"), (String) e1.get("Name"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    HashMap<String, Object> newMap = new HashMap<>();
//                    JSONObject e1 = JSONObject.parseObject(e2.toString());
//                    ArrayList<Object> result = issueTrendStatisticsService.fromProjectBugCheckNull(hashMap.get("token"), (String) e1.get("Name"));
                    newMap.put("count",result.size());
                    newMap.put("data",result);
                    newMap.put("projectName",e1.get("Name").toString());
                    newMap.put("displayName",  e1.get("DisplayName").toString());
                    testMap.add(newMap);
                }
            }else{
                HashMap<String, Object> newMap = new HashMap<>();
                ArrayList<Object> result = issueTrendStatisticsService.fromProjectBugCheckNull(hashMap.get("token"),hashMap.get("projectName").split(";")[0]);
                newMap.put("count",result.size());
                newMap.put("data",result);
                newMap.put("projectName",hashMap.get("projectName").toString().split(";")[0]);
                newMap.put("displayName",hashMap.get("projectName").toString().split(";")[1]);
                testMap.add(newMap);
            }

//        if (result.size()==0){
//            return ResultHolder.selfInface(1,"fail","请检查环境或者个人令牌权限",0);
//        }
            return ResultHolder.selfInface(0,"success",testMap,testMap.size());
        } catch (Exception e) {
            return ResultHolder.selfInface(1,"fail",testMap,0);
        }
    }
    @GetMapping("/issue/total/projectquality/lastday/list")
    public ResultHolder fromProjectBugCheck(@RequestParam HashMap<String, String> hashMap)  {
        ArrayList<Object>  testMap = new ArrayList<>();
        try {

            JSONObject resultToken = issueTrendStatisticsService.checkToken(hashMap.get("token"));
            if(resultToken.get("team") == null ){
                return ResultHolder.selfInface(1,"fail","请检查环境或者个人令牌权限",0);
            }
            if(hashMap.get("projectName") == null) {
                JSONObject json_test =  issueTrendStatisticsService.codingGetProjectAll(hashMap.get("token"));
                for (Object e2 : json_test.getJSONObject("Response").getJSONObject("Data").getJSONArray("ProjectList")) {
                    ArrayList<Object> result = new ArrayList<>();
                    HashMap<String, Object> newMap = new HashMap<>();
                    JSONObject e1 = JSONObject.parseObject(e2.toString());
                    try {
                        result = issueTrendStatisticsService.fromProjectBugCheck(hashMap.get("token"), (String) e1.get("Name"),hashMap.get("duation"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    ArrayList<Object>
                    newMap.put("count",result.size());
                    newMap.put("data",result);
                    newMap.put("projectName",(String) e1.get("Name"));
                    newMap.put("displayName",  e1.get("DisplayName"));
                    testMap.add(newMap);
                }
            }else{
                HashMap<String, Object> newMap = new HashMap<>();
                ArrayList<Object> result = issueTrendStatisticsService.fromProjectBugCheck(hashMap.get("token"),hashMap.get("projectName").split(";")[0],hashMap.get("duation"));
                newMap.put("count",result.size());
                newMap.put("data",result);
                newMap.put("projectName",hashMap.get("projectName").toString().split(";")[0]);
                newMap.put("displayName",hashMap.get("projectName").toString().split(";")[1]);
                testMap.add(newMap);
            }

//        if (result.size()==0){
//            return ResultHolder.selfInface(1,"fail","请检查环境或者个人令牌权限",0);
//        }
            return ResultHolder.selfInface(0,"success",testMap,testMap.size());
        } catch (Exception e) {
            return ResultHolder.selfInface(1,"fail",testMap,0);
        }
    }
    @GetMapping("/issue/total/projectquality/duedate/overtime/list")
    public ResultHolder fromProjectBugCheckOvertime(@RequestParam HashMap<String, String> hashMap)  {
        ArrayList<Object>  testMap = new ArrayList<>();
        try {

            JSONObject resultToken = issueTrendStatisticsService.checkToken(hashMap.get("token"));
            if(resultToken.get("team") == null ){
                return ResultHolder.selfInface(1,"fail","请检查环境或者个人令牌权限",0);
            }
            if(hashMap.get("projectName") == null) {
                JSONObject json_test =  issueTrendStatisticsService.codingGetProjectAll(hashMap.get("token"));
                System.out.println(json_test);
                for (Object e2 : json_test.getJSONObject("Response").getJSONObject("Data").getJSONArray("ProjectList")) {
                    ArrayList<Object> result = new ArrayList<>();
                    HashMap<String, Object> newMap = new HashMap<>();
                    JSONObject e1 = JSONObject.parseObject(e2.toString());
                    try {
                        result = issueTrendStatisticsService.fromProjectBugCheckOvertime(hashMap.get("token"), (String) e1.get("Name"),hashMap.get("duation"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    newMap.put("count",result.size());
                    newMap.put("data",result);
                    newMap.put("projectName",(String) e1.get("Name"));
                    newMap.put("displayName",  e1.get("DisplayName"));
                    testMap.add(newMap);
                }
            }else{
                HashMap<String, Object> newMap = new HashMap<>();
                ArrayList<Object> result = issueTrendStatisticsService.fromProjectBugCheckOvertime(hashMap.get("token"),hashMap.get("projectName").split(";")[0],hashMap.get("duation"));

                newMap.put("count",result.size());
                newMap.put("data",result);
                newMap.put("projectName",hashMap.get("projectName").toString().split(";")[0]);
                newMap.put("displayName",hashMap.get("projectName").toString().split(";")[1]);
                testMap.add(newMap);
            }

            //        if (result.size()==0){
//            return ResultHolder.selfInface(1,"fail",result,0);
//        }
            return ResultHolder.selfInface(0,"success",testMap,testMap.size());
        } catch (Exception e) {
            return ResultHolder.selfInface(1,"fail",testMap,0);
        }
    }
}
