package io.metersphere.controller;


import com.alibaba.fastjson.JSONObject;
import com.arronlong.httpclientutil.exception.HttpProcessException;
import io.metersphere.base.domain.IssueTrend;

import io.metersphere.performance.base.TrendChartsData;
import io.metersphere.service.IssueTrendStatisticsService;
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
    public ResultHolder issueTrendTotal(@RequestParam HashMap<String, String> hashMap) throws HttpProcessException, ExecutionException, InterruptedException, ParseException {
        System.out.println(hashMap.get("token"));
        JSONObject resultToken = issueTrendStatisticsService.checkToken(hashMap.get("token"));
        System.out.println(resultToken);
        if(resultToken.get("team") == null ){
            return ResultHolder.selfInface(1,"fail","请检查环境或者个人令牌权限",0);
        }
        List<Map<String,Object>> result = issueTrendStatisticsService.getIssueTrendTotal(hashMap);
        if (result.size()==0){
            return ResultHolder.selfInface(1,"fail","请检查环境或者个人令牌权限",0);
        }
        return ResultHolder.selfInface(0,"success",result,result.size());
    }
//    @RequestMapping(value = "/queryStmp", method = RequestMethod.GET)
    @GetMapping("/issue/total/getAllProject")
    public ResultHolder getAllProject(@RequestParam HashMap<String, String> hashMap) throws HttpProcessException {
        JSONObject resultToken = issueTrendStatisticsService.checkToken(hashMap.get("token"));
//        System.out.println(String.format("code1:%s,", hashMap.get("token")));
//        System.out.println(resultToken);
        if(resultToken.get("team") == null ){
            return ResultHolder.selfInface(1,"fail","请检查环境或者个人令牌权限",0);
        }
        Map<String,Object> testMap = new HashMap<>();



        JSONObject json_test =  issueTrendStatisticsService.codingGetProjectAll(hashMap.get("token"));
        System.out.println(json_test);
//        JSONObject json_test1 = JSONObject.parseObject(json_test.get("Response"));
        for (Object e:json_test.getJSONObject("Response").getJSONObject("Data").getJSONArray("ProjectList")) {
            Map<String,Object> modulName = new HashMap<>();
            JSONObject e1 = JSONObject.parseObject(e.toString());
            System.out.println(e1);
            modulName.put("display_name",  e1.get("DisplayName"));
            modulName.put("id", e1.get("Id"));
            testMap.put( e1.get("Name").toString(),modulName);
        }
//        testMap.put("code",0);
//        testMap.put("msg","sueccess");
//        testMap.put("data",modulName);
//        return CommonResult.success(modulName);

        return ResultHolder.selfInfaceNew(0,"success",testMap,testMap.size());
    }
    @GetMapping("/issue/total/projectquality/unresolved/list")
    public ResultHolder fromProjectUnresolved(@RequestParam HashMap<String, String> hashMap) throws HttpProcessException {
        JSONObject resultToken = issueTrendStatisticsService.checkToken(hashMap.get("token"));
        if(resultToken.get("team") == null ){
            return ResultHolder.selfInface(1,"fail","请检查环境或者个人令牌权限",0);
        }
        Map<String,Object> result = issueTrendStatisticsService.fromProjectUnresolved(hashMap.get("token"),hashMap.get("projectName"));
        if (result.size()==0){
            return ResultHolder.selfInface(1,"fail","请检查环境或者个人令牌权限",0);
        }
        return ResultHolder.selfInface(0,"success",result,0);
    }
    @GetMapping("/issue/total/projectquality/duedate/null/list")
    public ResultHolder fromProjectBugCheckNull(@RequestParam HashMap<String, String> hashMap) throws HttpProcessException {
        JSONObject resultToken = issueTrendStatisticsService.checkToken(hashMap.get("token"));
        if(resultToken.get("team") == null ){
            return ResultHolder.selfInface(1,"fail","请检查环境或者个人令牌权限",0);
        }
        ArrayList<Object> result = issueTrendStatisticsService.fromProjectBugCheckNull(hashMap.get("token"),hashMap.get("projectName"));
        if (result.size()==0){
            return ResultHolder.selfInface(1,"fail","请检查环境或者个人令牌权限",0);
        }
        return ResultHolder.selfInface(0,"success",result,result.size());
    }
    @GetMapping("/issue/total/projectquality/lastday/list")
    public ResultHolder fromProjectBugCheck(@RequestParam HashMap<String, String> hashMap)  {
        JSONObject resultToken = issueTrendStatisticsService.checkToken(hashMap.get("token"));
        if(resultToken.get("team") == null ){
            return ResultHolder.selfInface(1,"fail","请检查环境或者个人令牌权限",0);
        }
        ArrayList<Object> result = issueTrendStatisticsService.fromProjectBugCheck(hashMap.get("token"),hashMap.get("projectName"),hashMap.get("duation"));
        if (result.size()==0){
            return ResultHolder.selfInface(1,"fail","请检查环境或者个人令牌权限",0);
        }
        return ResultHolder.selfInface(0,"success",result,result.size());
    }
    @GetMapping("/issue/total/projectquality/duedate/overtime/list")
    public ResultHolder fromProjectBugCheckOvertime(@RequestParam HashMap<String, String> hashMap)  {
        JSONObject resultToken = issueTrendStatisticsService.checkToken(hashMap.get("token"));
        if(resultToken.get("team") == null ){
            return ResultHolder.selfInface(1,"fail","请检查环境或者个人令牌权限",0);
        }
        ArrayList<Object> result = issueTrendStatisticsService.fromProjectBugCheckOvertime(hashMap.get("token"),hashMap.get("projectName"),hashMap.get("duation"));
        if (result.size()==0){
            return ResultHolder.selfInface(1,"fail","请检查环境或者个人令牌权限",0);
        }
        return ResultHolder.selfInface(0,"success",result,result.size());
    }
}
