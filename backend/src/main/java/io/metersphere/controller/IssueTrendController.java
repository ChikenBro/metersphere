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
    @GetMapping("/issue/total/projectquality/list")
    public ResultHolder issueTrendTotal(@RequestParam HashMap<String, String> hashMap) throws HttpProcessException, ExecutionException, InterruptedException, ParseException {
        List<Map<String,Object>> result = issueTrendStatisticsService.getIssueTrendTotal(hashMap);
                if (result.size()==0){
            return ResultHolder.selfInface(1,"fail","请检查环境或者个人令牌权限",issueTrendStatisticsService.getIssueTrendTotal(hashMap).size());
        }
        return ResultHolder.selfInface(0,"success",result,result.size());
    }
//    @RequestMapping(value = "/queryStmp", method = RequestMethod.GET)
    @GetMapping("/issue/total/getAllProject")
    public ResultHolder getAllProject(@RequestParam HashMap<String, String> hashMap) throws HttpProcessException {
//        Map<String,Object> testMap = new HashMap<>();
        ArrayList<String> modulName = new ArrayList<>();


        JSONObject json_test =  issueTrendStatisticsService.codingGetProjectAll(hashMap.get("yourToken"));
//        JSONObject json_test = JSONObject.parseObject(respResult);
        for (Object e:json_test.getJSONArray("data")) {
            JSONObject e1 = JSONObject.parseObject(e.toString());
            modulName.add(e1.get("display_name").toString());
        }
//        testMap.put("code",0);
//        testMap.put("msg","sueccess");
//        testMap.put("data",modulName);
//        return CommonResult.success(modulName);

        return ResultHolder.selfInfaceNew(0,"success",modulName);
    }

}
