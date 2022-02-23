package io.metersphere.controller;


import com.alibaba.fastjson.JSONObject;
import com.arronlong.httpclientutil.common.HttpResult;
import com.arronlong.httpclientutil.exception.HttpProcessException;
import io.metersphere.base.domain.IssueTrend;
import io.metersphere.performance.base.TrendChartsData;
import io.metersphere.service.IssueTrendStatisticsService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @GetMapping("/issue/total/allproject")
    public List<Map<String, String>> issueTrendTotal(@RequestParam HashMap<String, String> hashMap) throws HttpProcessException {

        return issueTrendStatisticsService.getIssueTrendTotal(hashMap);
    }
    @GetMapping("/issue/total/getAllProject")
    public List<String> getAllProject() throws HttpProcessException {
        ArrayList<String> modulName = new ArrayList<>();


        JSONObject json_test =  issueTrendStatisticsService.codingGetProjectAll();
//        JSONObject json_test = JSONObject.parseObject(respResult);
        for (Object e:json_test.getJSONArray("data")) {
            JSONObject e1 = JSONObject.parseObject(e.toString());
            modulName.add(e1.get("display_name").toString());
        }
        return modulName;
    }

}
