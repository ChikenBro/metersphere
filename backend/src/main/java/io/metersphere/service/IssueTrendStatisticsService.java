package io.metersphere.service;

import io.metersphere.base.domain.IssueTrend;
import io.metersphere.base.domain.IssueTrendExample;
import io.metersphere.base.mapper.IssueTrendMapper;
import io.metersphere.commons.constants.MDTrend;
import io.metersphere.performance.base.TrendChartsData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class IssueTrendStatisticsService {

    @Autowired
    private IssueTrendMapper issueTrendMapper;


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
