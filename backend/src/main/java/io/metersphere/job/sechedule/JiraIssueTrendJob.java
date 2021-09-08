package io.metersphere.job.sechedule;

import io.metersphere.base.domain.IssueTrend;
import io.metersphere.base.domain.Project;
import io.metersphere.commons.constants.ScheduleGroup;
import io.metersphere.commons.utils.CommonBeanFactory;
import io.metersphere.service.IssueTrendStatisticsService;
import io.metersphere.service.ProjectService;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.TriggerKey;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.List;

public class JiraIssueTrendJob extends MsScheduleJob {

    private ProjectService projectService;
    private IssueTrendStatisticsService statisticsService;

    public JiraIssueTrendJob() {
        this.projectService = CommonBeanFactory.getBean(ProjectService.class);
        this.statisticsService = CommonBeanFactory.getBean(IssueTrendStatisticsService.class);
    }

    @Override
    void businessExecute(JobExecutionContext context) {
        LocalDate now = LocalDate.now();
        if (DayOfWeek.MONDAY.compareTo(now.getDayOfWeek()) != 0) {
            return;
        }
        // 格式化当前时间
        String mondayFormat = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        // 格式化礼拜天时间
        String sundayFormat = now.plusDays(6).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        WeekFields weekFields = WeekFields.ISO;
        // 当前第几周
        int weekNumber = now.get(weekFields.weekOfWeekBasedYear());
        List<Project> projectList = this.projectService.listAll();
        projectList.stream().filter(v -> StringUtils.isNotEmpty(v.getJiraKey())).forEach(v -> {
            IssueTrend trend = new IssueTrend();
            trend.setProjectId(v.getId());
            trend.setJiraKey(v.getJiraKey());
            trend.setIssueYear(now.getYear());
            trend.setIssueWeek(weekNumber);
            trend.setStartWeekTime(mondayFormat);
            trend.setEndWeekTime(sundayFormat);
            Date date = new Date();
            trend.setCreateTime(date.getTime());
            trend.setUpdateTime(date.getTime());
            statisticsService.addProjectIssueTrend(trend);
        });
    }

    public static JobKey getJobKey(String projectId) {
        return new JobKey(projectId, ScheduleGroup.ISSUE_TREND.name());
    }

    public static TriggerKey getTriggerKey(String projectId) {
        return new TriggerKey(projectId, ScheduleGroup.ISSUE_TREND.name());
    }
}
