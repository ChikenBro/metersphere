package io.metersphere.track.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.metersphere.api.dto.automation.*;
import io.metersphere.api.dto.definition.ApiTestCaseRequest;
import io.metersphere.api.dto.definition.TestPlanApiCaseDTO;
import io.metersphere.api.dto.definition.request.*;
import io.metersphere.api.dto.definition.request.variable.ScenarioVariable;
import io.metersphere.api.jmeter.JMeterService;
import io.metersphere.api.service.ApiAutomationService;
import io.metersphere.api.service.ApiTestCaseService;
import io.metersphere.base.domain.*;
import io.metersphere.base.mapper.*;
import io.metersphere.base.mapper.ext.*;
import io.metersphere.commons.constants.*;
import io.metersphere.commons.exception.MSException;
import io.metersphere.commons.user.SessionUser;
import io.metersphere.commons.utils.*;
import io.metersphere.dto.BaseSystemConfigDTO;
import io.metersphere.i18n.Translator;
import io.metersphere.iteration.RequirementRequest;
import io.metersphere.log.utils.ReflexObjectUtil;
import io.metersphere.log.vo.DetailColumn;
import io.metersphere.log.vo.OperatingLogDetails;
import io.metersphere.log.vo.track.TestPlanReference;
import io.metersphere.notice.sender.NoticeModel;
import io.metersphere.notice.service.NoticeSendService;
import io.metersphere.performance.request.RunTestPlanRequest;
import io.metersphere.performance.service.PerformanceTestService;
import io.metersphere.service.ScheduleService;
import io.metersphere.service.SystemParameterService;
import io.metersphere.track.Factory.ReportComponentFactory;
import io.metersphere.track.domain.ReportComponent;
import io.metersphere.track.dto.*;
import io.metersphere.track.request.report.TestPlanReportSaveRequest;
import io.metersphere.track.request.testcase.PlanCaseRelevanceRequest;
import io.metersphere.track.request.testcase.QueryTestPlanRequest;
import io.metersphere.track.request.testplan.AddTestPlanRequest;
import io.metersphere.track.request.testplan.LoadCaseRequest;
import io.metersphere.track.request.testplancase.QueryTestPlanCaseRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.collections.ListedHashTree;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class TestPlanService {
    @Resource
    ExtScheduleMapper extScheduleMapper;
    @Resource
    TestPlanMapper testPlanMapper;
    @Resource
    ExtTestPlanMapper extTestPlanMapper;
    @Resource
    ScheduleService scheduleService;
    @Resource
    ExtTestPlanTestCaseMapper extTestPlanTestCaseMapper;
    @Resource
    TestCaseMapper testCaseMapper;
    @Resource
    TestPlanTestCaseMapper testPlanTestCaseMapper;
    @Resource
    ExtApiScenarioMapper extApiScenarioMapper;
    @Resource
    SqlSessionFactory sqlSessionFactory;
    @Lazy
    @Resource
    TestPlanTestCaseService testPlanTestCaseService;
    @Resource
    TestCaseReportMapper testCaseReportMapper;
    @Resource
    TestPlanProjectMapper testPlanProjectMapper;
    @Resource
    TestPlanProjectService testPlanProjectService;
    @Resource
    ProjectMapper projectMapper;
    @Resource
    ExtTestCaseMapper extTestCaseMapper;
    @Resource
    UserMapper userMapper;
    @Resource
    private NoticeSendService noticeSendService;
    @Resource
    private SystemParameterService systemParameterService;
    @Resource
    private TestPlanApiCaseService testPlanApiCaseService;
    @Resource
    private TestPlanScenarioCaseService testPlanScenarioCaseService;
    @Resource
    private TestPlanLoadCaseService testPlanLoadCaseService;
    @Resource
    private JMeterService jMeterService;
    @Resource
    private ApiAutomationService apiAutomationService;
    @Resource
    private ExtTestPlanApiCaseMapper extTestPlanApiCaseMapper;
    @Resource
    private ExtTestPlanLoadCaseMapper extTestPlanLoadCaseMapper;
    @Resource
    private ExtTestPlanScenarioCaseMapper extTestPlanScenarioCaseMapper;
    @Resource
    private ApiTestCaseService apiTestCaseService;
    @Resource
    private PerformanceTestService performanceTestService;
    @Resource
    private TestPlanLoadCaseMapper testPlanLoadCaseMapper;
    @Resource
    private ApiTestCaseMapper apiTestCaseMapper;
    @Resource
    private ApiDefinitionMapper apiDefinitionMapper;
    @Resource
    private TestPlanApiCaseMapper testPlanApiCaseMapper;
    @Resource
    private TestPlanApiScenarioMapper testPlanApiScenarioMapper;
    @Resource
    private ApiScenarioMapper apiScenarioMapper;
    @Resource
    private TestCaseTestMapper testCaseTestMapper;
    @Resource
    private ApiScenarioReportMapper apiScenarioReportMapper;
    @Resource
    private TestPlanReportMapper testPlanReportMapper;
    @Resource
    private TestPlanReportService testPlanReportService;
    @Resource
    private IterationMapper iterationMapper;
    @Lazy
    @Resource
    private IssuesService issuesService;

    public synchronized String addTestPlan(AddTestPlanRequest testPlan) {
        if (getTestPlanByName(testPlan.getName()).size() > 0) {
            MSException.throwException(Translator.get("plan_name_already_exists"));
        }
        if (null != testPlan.getPlannedStartTime() && null != testPlan.getPlannedEndTime() && testPlan.getPlannedStartTime() >= testPlan.getPlannedEndTime()) {
            MSException.throwException("计划结束时间应大于计划开始时间");
        }
        testPlan.setStatus(TestPlanStatus.Prepare.name());
        testPlan.setCreateTime(System.currentTimeMillis());
        testPlan.setUpdateTime(System.currentTimeMillis());
        testPlan.setCreator(SessionUtils.getUser().getId());
        if (StringUtils.isBlank(testPlan.getProjectId())) {
            testPlan.setProjectId(SessionUtils.getCurrentProjectId());
        }
        if (null != testPlan.getIterationCode()) {
            List<Iteration> iteration = iterationMapper.getIterationByIterationCode(testPlan.getIterationCode());
            iteration = iteration.stream().filter(s -> s.getProjectId().equals(testPlan.getProjectId())).collect(Collectors.toList());
            if (!iteration.isEmpty()) {
                testPlan.setIterationId(iteration.get(0).getId());
                checkTestPlanSuper(iteration.get(0).getId(), testPlan.getTestPlanInherit());
            }
        }
        testPlanMapper.insert(testPlan);

        List<String> userIds = new ArrayList<>();
        userIds.add(testPlan.getPrincipal());
        String context = getTestPlanContext(testPlan, NoticeConstants.Event.CREATE);
        User user = userMapper.selectByPrimaryKey(testPlan.getCreator());
        Map<String, Object> paramMap = getTestPlanParamMap(testPlan);
        paramMap.put("creator", user.getName());
        NoticeModel noticeModel = NoticeModel.builder().context(context).relatedUsers(userIds).subject(Translator.get("test_plan_notification")).mailTemplate("TestPlanStart").paramMap(paramMap).event(NoticeConstants.Event.CREATE).build();
        noticeSendService.send(NoticeConstants.TaskType.TEST_PLAN_TASK, noticeModel);
        //继承用例内容
        if (null != testPlan.getTestPlanInherit() && !testPlan.getTestPlanInherit().equals("")) {
            TestPlanTestCaseExample testPlanTestCaseExample = new TestPlanTestCaseExample();
            testPlanTestCaseExample.createCriteria().andPlanIdEqualTo(testPlan.getTestPlanInherit());
            List<TestPlanTestCase> testPlanTestCases = testPlanTestCaseMapper.selectByExample(testPlanTestCaseExample);
            if (!testPlanTestCases.isEmpty()) {
                testPlanTestCases = syncTestPlanStatusByRetain(testPlanTestCases, testPlan);
                //入库
                int count = testPlanTestCaseMapper.insertTestPlanTestCases(testPlanTestCases);
                System.out.println(count);
            }
        }
        return testPlan.getId();
    }


    /**
     * 校验测试计划继承字段是否正确
     *
     * @param iterationId     迭代id
     * @param superTestPlanID 继承的测试计划id
     */
    private void checkTestPlanSuper(String iterationId, String superTestPlanID) {
        if (null == superTestPlanID || superTestPlanID.equals("")) {
            return;
        }
        //校验迭代id下是否有此测试计划-计划继承
        try {
            List<TestPlan> testPlans = testPlanMapper.selectByIterationId(iterationId);
            List<TestPlan> TestPlan = testPlans.stream().filter(s -> s.getId().equals(superTestPlanID)).collect(Collectors.toList());
            if (TestPlan.isEmpty()) {
                MSException.throwException("所属迭代下计划继承的测试计划有误");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            MSException.throwException("所属迭代下计划继承的测试计划有误");
        }
    }


    /**
     * 根据是否
     *
     * @param testPlanTestCases 继承的测试用例内容
     */
    public List<TestPlanTestCase> syncTestPlanStatusByRetain(List<TestPlanTestCase> testPlanTestCases, AddTestPlanRequest testPlan) {
        testPlanTestCases.forEach(testPlanTestCase -> {
            testPlanTestCase.setId(UUID.randomUUID().toString());
            testPlanTestCase.setPlanId(testPlan.getId());
            testPlanTestCase.setReportId(null);
            //
            if (testPlanTestCase.getStatus().equals("Blocking") && testPlan.getIfRetain()) {
                testPlanTestCase.setStatus("Blocking");
            } else {
                testPlanTestCase.setStatus("Prepare");
            }
            testPlanTestCase.setResults(null);
            testPlanTestCase.setRemark(null);
            testPlanTestCase.setCreateTime(System.currentTimeMillis());
            testPlanTestCase.setUpdateTime(System.currentTimeMillis());
            testPlanTestCase.setActualResult(null);
            testPlanTestCase.setCreateUser(testPlan.getCreator());
        });
        return testPlanTestCases;
    }


    public List<TestPlan> getTestPlanByName(String name) {
        TestPlanExample example = new TestPlanExample();
        example.createCriteria().andProjectIdEqualTo(SessionUtils.getCurrentProjectId()).andNameEqualTo(name);
        return testPlanMapper.selectByExample(example);
    }

    public TestPlan getTestPlan(String testPlanId) {
        return Optional.ofNullable(testPlanMapper.selectByPrimaryKey(testPlanId)).orElse(new TestPlan());
    }

    public String editTestPlan(TestPlanDTO testPlan, Boolean isSendMessage) {
        checkTestPlan(testPlan);

        TestPlan res = testPlanMapper.selectByPrimaryKey(testPlan.getId()); //  先查一次库
        testPlan.setUpdateTime(System.currentTimeMillis());
        if (!res.getStatus().equals(testPlan.getStatus())) {    //  若有改变才更新时间
            if (TestPlanStatus.Underway.name().equals(testPlan.getStatus())) {
                if (res.getStatus().equals(TestPlanStatus.Prepare.name())) {
                    testPlan.setActualStartTime(System.currentTimeMillis());
                }   //  未开始->进行中，写入实际开始时间
                else if (res.getStatus().equals(TestPlanStatus.Completed.name())) {
                    testPlan.setActualEndTime(null);
                }   //  已完成->进行中，结束时间置空
            } else if (!res.getStatus().equals(TestPlanStatus.Prepare.name()) && TestPlanStatus.Prepare.name().equals(testPlan.getStatus())) {
                testPlan.setActualStartTime(null);
                testPlan.setActualEndTime(null);
            }   //  非未开始->未开始，时间都置空
            else if (TestPlanStatus.Completed.name().equals(testPlan.getStatus()) && !TestPlanStatus.Completed.name().equals(res.getStatus())) {
                //已完成，写入实际完成时间
                testPlan.setActualEndTime(System.currentTimeMillis());
            } else if (!res.getStatus().equals(TestPlanStatus.Finished.name()) && TestPlanStatus.Finished.name().equals(testPlan.getStatus())) {
                testPlan.setActualEndTime(System.currentTimeMillis());
            }   //  非已结束->已结束，更新结束时间
        }

        List<String> userIds = new ArrayList<>();
        userIds.add(testPlan.getPrincipal());
        AddTestPlanRequest testPlans = new AddTestPlanRequest();
        int i;
        if (testPlan.getName() == null) {//  若是点击该测试计划，则仅更新了updateTime，其它字段全为null，使用updateByPrimaryKeySelective
            i = testPlanMapper.updateByPrimaryKeySelective(testPlan);
        } else {  //  有修改字段的调用，为保证将某些时间置null的情况，使用updateByPrimaryKey
            extScheduleMapper.updateNameByResourceID(testPlan.getId(), testPlan.getName());//   同步更新该测试的定时任务的name
            i = testPlanMapper.updateByPrimaryKeyWithBLOBs(testPlan); //  更新
        }
        if (!StringUtils.isBlank(testPlan.getStatus()) && isSendMessage) {
            BeanUtils.copyBean(testPlans, getTestPlan(testPlan.getId()));
            String context = getTestPlanContext(testPlans, NoticeConstants.Event.UPDATE);
            User user = userMapper.selectByPrimaryKey(testPlans.getCreator());
            Map<String, Object> paramMap = getTestPlanParamMap(testPlans);
            paramMap.put("creator", user.getName());
            NoticeModel noticeModel = NoticeModel.builder().context(context).relatedUsers(userIds).subject(Translator.get("test_plan_notification")).mailTemplate("TestPlanEnd").paramMap(paramMap).event(NoticeConstants.Event.UPDATE).build();
            noticeSendService.send(NoticeConstants.TaskType.TEST_PLAN_TASK, noticeModel);
        }
        return testPlan.getId();
    }

    //计划内容
    private Map<String, Object> getTestPlanParamMap(TestPlan testPlan) {
        Long startTime = testPlan.getPlannedStartTime();
        Long endTime = testPlan.getPlannedEndTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String start = null;
        String sTime = String.valueOf(startTime);
        String eTime = String.valueOf(endTime);
        if (!sTime.equals("null")) {
            start = sdf.format(new Date(Long.parseLong(sTime)));
        }
        String end = null;
        if (!eTime.equals("null")) {
            end = sdf.format(new Date(Long.parseLong(eTime)));
        }

        Map<String, Object> context = new HashMap<>();
        BaseSystemConfigDTO baseSystemConfigDTO = systemParameterService.getBaseInfo();
        context.put("url", baseSystemConfigDTO.getUrl());
        context.put("testPlanName", testPlan.getName());
        context.put("start", start);
        context.put("end", end);
        context.put("id", testPlan.getId());
        String status = "";
        if (StringUtils.equals(TestPlanStatus.Underway.name(), testPlan.getStatus())) {
            status = "进行中";
        } else if (StringUtils.equals(TestPlanStatus.Prepare.name(), testPlan.getStatus())) {
            status = "未开始";
        } else if (StringUtils.equals(TestPlanStatus.Completed.name(), testPlan.getStatus())) {
            status = "已完成";
        }
        context.put("status", status);
        User user = userMapper.selectByPrimaryKey(testPlan.getCreator());
        context.put("creator", user.getName());
        return context;
    }


    private void checkTestPlan(TestPlan testPlan) {
        if (testPlan.getName() != null) {
            TestPlanExample example = new TestPlanExample();
            example.createCriteria().andNameEqualTo(testPlan.getName()).andWorkspaceIdEqualTo(SessionUtils.getCurrentWorkspaceId()).andIdNotEqualTo(testPlan.getId());
            if (testPlanMapper.selectByExample(example).size() > 0) {
                MSException.throwException(Translator.get("plan_name_already_exists"));
            }
            if (null != testPlan.getPlannedStartTime() && null != testPlan.getPlannedEndTime() && testPlan.getPlannedStartTime() >= testPlan.getPlannedEndTime()) {
                MSException.throwException("计划结束时间应大于计划开始时间");
            }
        }
    }

    public int deleteTestPlan(String planId) {
        TestPlan testPlan = getTestPlan(planId);
        deleteTestCaseByPlanId(planId);
        testPlanApiCaseService.deleteByPlanId(planId);
        testPlanScenarioCaseService.deleteByPlanId(planId);

        //删除定时任务
        scheduleService.deleteByResourceId(planId, ScheduleGroup.TEST_PLAN_TEST.name());

        int num = testPlanMapper.deleteByPrimaryKey(planId);
        List<String> relatedUsers = new ArrayList<>();
        AddTestPlanRequest testPlans = new AddTestPlanRequest();
        relatedUsers.add(testPlan.getCreator());
        try {
            BeanUtils.copyBean(testPlans, testPlan);
            String context = getTestPlanContext(testPlans, NoticeConstants.Event.DELETE);
            User user = userMapper.selectByPrimaryKey(testPlan.getCreator());
            Map<String, Object> paramMap = getTestPlanParamMap(testPlan);
            paramMap.put("creator", user.getName());
            NoticeModel noticeModel = NoticeModel.builder().context(context).relatedUsers(relatedUsers).subject(Translator.get("test_plan_notification")).mailTemplate("TestPlanDelete").paramMap(paramMap).event(NoticeConstants.Event.DELETE).build();
            noticeSendService.send(NoticeConstants.TaskType.TEST_PLAN_TASK, noticeModel);
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
        }
        return num;
    }

    public void deleteTestCaseByPlanId(String testPlanId) {
        TestPlanTestCaseExample testPlanTestCaseExample = new TestPlanTestCaseExample();
        testPlanTestCaseExample.createCriteria().andPlanIdEqualTo(testPlanId);
        testPlanTestCaseMapper.deleteByExample(testPlanTestCaseExample);
    }

    private void calcTestPlanRate(List<TestPlanDTOWithMetric> testPlans) {
        // todo 优化查询方式，数据库一次取出来
        testPlans.forEach(testPlan -> {
            testPlan.setTested(0);
            testPlan.setPassed(0);
            testPlan.setTotal(0);

            List<String> functionalExecResults = extTestPlanTestCaseMapper.getExecResultByPlanId(testPlan.getId());
            functionalExecResults.forEach(item -> {
                if (!StringUtils.equals(item, TestPlanTestCaseStatus.Prepare.name()) && !StringUtils.equals(item, TestPlanTestCaseStatus.Underway.name())) {
                    testPlan.setTested(testPlan.getTested() + 1);
                    if (StringUtils.equals(item, TestPlanTestCaseStatus.Pass.name())) {
                        testPlan.setPassed(testPlan.getPassed() + 1);
                    }
                }
            });

            List<String> apiExecResults = testPlanApiCaseService.getExecResultByPlanId(testPlan.getId());
            apiExecResults.forEach(item -> {
                if (StringUtils.isNotBlank(item)) {
                    testPlan.setTested(testPlan.getTested() + 1);
                    if (StringUtils.equals(item, "success")) {
                        testPlan.setPassed(testPlan.getPassed() + 1);
                    }
                }
            });

            List<String> scenarioExecResults = testPlanScenarioCaseService.getExecResultByPlanId(testPlan.getId());
            scenarioExecResults.forEach(item -> {
                if (StringUtils.isNotBlank(item)) {
                    testPlan.setTested(testPlan.getTested() + 1);
                    if (StringUtils.equals(item, ScenarioStatus.Success.name())) {
                        testPlan.setPassed(testPlan.getPassed() + 1);
                    }
                }
            });

            List<String> loadResults = testPlanLoadCaseService.getStatus(testPlan.getId());
            loadResults.forEach(item -> {
                if (StringUtils.isNotBlank(item)) {
                    testPlan.setTested(testPlan.getTested() + 1);
                    if (StringUtils.equals(item, "success")) {
                        testPlan.setPassed(testPlan.getPassed() + 1);
                    }
                }
            });

            testPlan.setTotal(apiExecResults.size() + scenarioExecResults.size() + functionalExecResults.size() + loadResults.size());

            testPlan.setPassRate(MathUtils.getPercentWithDecimal(testPlan.getTested() == 0 ? 0 : testPlan.getPassed() * 1.0 / testPlan.getTotal()));
            testPlan.setTestRate(MathUtils.getPercentWithDecimal(testPlan.getTotal() == 0 ? 0 : testPlan.getTested() * 1.0 / testPlan.getTotal()));
        });
    }


    public List<TestPlanDTOWithMetric> listTestPlan(QueryTestPlanRequest request) {
        request.setOrders(ServiceUtils.getDefaultOrder(request.getOrders()));
        if (StringUtils.isNotBlank(request.getProjectId())) {
            request.setProjectId(request.getProjectId());
        }
        List<TestPlanDTOWithMetric> testPlans = extTestPlanMapper.list(request);
        if (testPlans.isEmpty()) {
            return testPlans;
        }
        List<String> iterationIds = testPlans.stream().map(TestPlanDTOWithMetric::getIterationId).collect(Collectors.toList());
        List<Iteration> iterations = iterationMapper.getIterationsByIterationId(iterationIds);
        testPlans.forEach(item -> {
            TestPlanReportExample example = new TestPlanReportExample();
            example.createCriteria().andTestPlanIdEqualTo(item.getId());
            if (null != item.getIterationId() && !iterations.isEmpty()) {
                List<Iteration> iteration = iterations.stream().filter(s -> s.getId().equals(item.getIterationId())).collect(Collectors.toList());
                if (!iteration.isEmpty()) {
                    item.setIterationName(iteration.get(0).getName());
                    item.setIterationCode(iteration.get(0).getCode());
                }
            }
            item.setExecutionTimes((int) testPlanReportMapper.countByExample(example));
        });
        calcTestPlanRate(testPlans);
        return testPlans;
    }

    public void checkStatus(String testPlanId) { //  检查执行结果，自动更新计划状态
        List<String> statusList = new ArrayList<>();
        statusList.addAll(extTestPlanTestCaseMapper.getExecResultByPlanId(testPlanId));
        statusList.addAll(testPlanApiCaseService.getExecResultByPlanId(testPlanId));
        statusList.addAll(testPlanScenarioCaseService.getExecResultByPlanId(testPlanId));
        statusList.addAll(testPlanLoadCaseService.getStatus(testPlanId));
        TestPlanDTO testPlanDTO = new TestPlanDTO();
        testPlanDTO.setId(testPlanId);
        if (statusList.size() == 0) { //  原先status不是prepare, 但删除所有关联用例的情况
            testPlanDTO.setStatus(TestPlanStatus.Prepare.name());
            editTestPlan(testPlanDTO, false);
            return;
        }
        int passNum = 0, prepareNum = 0, failNum = 0;
        for (String res : statusList) {
            if (StringUtils.equals(res, TestPlanTestCaseStatus.Pass.name()) || StringUtils.equals(res, "success") || StringUtils.equals(res, ScenarioStatus.Success.name())) {
                passNum++;
            } else if (res == null || StringUtils.equals(TestPlanStatus.Prepare.name(), res)) {
                prepareNum++;
            } else {
                failNum++;
            }
        }
        if (passNum == statusList.size()) {   //  全部通过
            testPlanDTO.setStatus(TestPlanStatus.Completed.name());
            this.editTestPlan(testPlanDTO, false);
        } else if (prepareNum == 0 && passNum + failNum == statusList.size()) {  //  已结束
            testPlanDTO.setStatus(TestPlanStatus.Finished.name());
            editTestPlan(testPlanDTO, false);
        } else if (prepareNum != 0) {    //  进行中
            testPlanDTO.setStatus(TestPlanStatus.Underway.name());
            editTestPlan(testPlanDTO, false);
        }
    }

    public List<TestPlanDTOWithMetric> listTestPlanByProject(QueryTestPlanRequest request) {
        List<TestPlanDTOWithMetric> testPlans = extTestPlanMapper.list(request);
        return testPlans;
    }


    /**
     * 校验测试计划下测试用例是否唯一
     *
     * @param request      请求
     * @param testCaseList 数据库测试计划的用例
     */
    public void checkTestPlanTestCaseRepeat(PlanCaseRelevanceRequest request, List<TestCase> testCaseList) {
        List<String> repeatTestCases = new ArrayList<>();
        try {
            List<String> testPlanIds = Stream.of(request.getPlanId()).collect(Collectors.toList());
            List<TestPlanTestCase> testPlanTestCases = testPlanTestCaseMapper.selectTestPlanTestCase(testPlanIds);
            request.getIds().forEach(id -> {
                List<TestPlanTestCase> testCase = testPlanTestCases.stream().filter(s -> s.getCaseId().equals(id)).collect(Collectors.toList());
                if (!testCase.isEmpty()) {
                    String name = testCaseList.stream().filter(s -> s.getId().equals(id)).collect(Collectors.toList()).get(0).getName();
                    repeatTestCases.add(name);
                }
            });
        } catch (Exception e) {
            log.error("校验测试计划下测试用例是否唯一 error,ids: {}", request.getIds());
        }
        if (!repeatTestCases.isEmpty()) {
            MSException.throwException(String.format("用例: %s 在当前测试计划中已存在", repeatTestCases));
        }
    }

    public void testPlanRelevance(PlanCaseRelevanceRequest request) {

        ServiceUtils.getSelectAllIds(request, request.getRequest(), (query) -> extTestCaseMapper.selectRelateIdsByQuery(query));

        List<String> testCaseIds = request.getIds();
        if (testCaseIds.isEmpty()) {
            return;
        }

        TestCaseExample testCaseExample = new TestCaseExample();
        testCaseExample.createCriteria().andIdIn(testCaseIds);
        List<TestCase> testCaseList = testCaseMapper.selectByExample(testCaseExample);
        checkTestPlanTestCaseRepeat(request, testCaseList);
        Map<String, String> userMap = testCaseList.stream().collect(Collectors.toMap(TestCase::getId, TestCase::getMaintainer));

        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        TestPlanTestCaseMapper batchMapper = sqlSession.getMapper(TestPlanTestCaseMapper.class);

        testCaseIds.forEach(caseId -> {
            TestPlanTestCaseWithBLOBs testPlanTestCase = new TestPlanTestCaseWithBLOBs();
            testPlanTestCase.setId(UUID.randomUUID().toString());
            testPlanTestCase.setCreateUser(SessionUtils.getUserId());
            testPlanTestCase.setExecutor(userMap.get(caseId) == null ? SessionUtils.getUserId() : userMap.get(caseId));
            testPlanTestCase.setCaseId(caseId);
            testPlanTestCase.setCreateTime(System.currentTimeMillis());
            testPlanTestCase.setUpdateTime(System.currentTimeMillis());
            testPlanTestCase.setPlanId(request.getPlanId());
            testPlanTestCase.setStatus(TestPlanStatus.Prepare.name());
            batchMapper.insert(testPlanTestCase);
        });

        sqlSession.flushStatements();
        //同步添加关联的接口和测试用例
        if (request.getChecked()) {
            if (!testCaseIds.isEmpty()) {
                testCaseIds.forEach(caseId -> {
                    List<TestCaseTest> list = new ArrayList<>();
                    TestCaseTestExample examp = new TestCaseTestExample();
                    examp.createCriteria().andTestCaseIdEqualTo(caseId);
                    if (testCaseTestMapper.countByExample(examp) > 0) {
                        list = testCaseTestMapper.selectByExample(examp);
                    }
                    list.forEach(l -> {
                        if (StringUtils.equals(l.getTestType(), TestCaseStatus.performance.name())) {
                            TestPlanLoadCase t = new TestPlanLoadCase();
                            t.setId(UUID.randomUUID().toString());
                            t.setTestPlanId(request.getPlanId());
                            t.setLoadCaseId(l.getTestId());
                            t.setCreateTime(System.currentTimeMillis());
                            t.setUpdateTime(System.currentTimeMillis());
                            TestPlanLoadCaseExample testPlanLoadCaseExample = new TestPlanLoadCaseExample();
                            testPlanLoadCaseExample.createCriteria().andTestPlanIdEqualTo(request.getPlanId()).andLoadCaseIdEqualTo(t.getLoadCaseId());
                            if (testPlanLoadCaseMapper.countByExample(testPlanLoadCaseExample) <= 0) {
                                testPlanLoadCaseMapper.insert(t);
                            }

                        }
                        if (StringUtils.equals(l.getTestType(), TestCaseStatus.testcase.name())) {
                            TestPlanApiCase t = new TestPlanApiCase();
                            ApiTestCaseWithBLOBs apitest = apiTestCaseMapper.selectByPrimaryKey(l.getTestId());
                            if (null != apitest) {
                                ApiDefinitionWithBLOBs apidefinition = apiDefinitionMapper.selectByPrimaryKey(apitest.getApiDefinitionId());
                                t.setId(UUID.randomUUID().toString());
                                t.setTestPlanId(request.getPlanId());
                                t.setApiCaseId(l.getTestId());
                                t.setEnvironmentId(apidefinition.getEnvironmentId());
                                t.setCreateTime(System.currentTimeMillis());
                                t.setUpdateTime(System.currentTimeMillis());
                                TestPlanApiCaseExample example = new TestPlanApiCaseExample();
                                example.createCriteria().andTestPlanIdEqualTo(request.getPlanId()).andApiCaseIdEqualTo(t.getApiCaseId());
                                if (testPlanApiCaseMapper.countByExample(example) <= 0) {
                                    testPlanApiCaseMapper.insert(t);
                                }
                            }


                        }
                        if (StringUtils.equals(l.getTestType(), TestCaseStatus.automation.name())) {
                            TestPlanApiScenario t = new TestPlanApiScenario();
                            ApiScenarioWithBLOBs testPlanApiScenario = apiScenarioMapper.selectByPrimaryKey(l.getTestId());
                            if (testPlanApiScenario != null) {
                                t.setId(UUID.randomUUID().toString());
                                t.setTestPlanId(request.getPlanId());
                                t.setApiScenarioId(l.getTestId());
                                t.setLastResult(testPlanApiScenario.getLastResult());
                                t.setPassRate(testPlanApiScenario.getPassRate());
                                t.setReportId(testPlanApiScenario.getReportId());
                                t.setStatus(testPlanApiScenario.getStatus());
                                t.setCreateTime(System.currentTimeMillis());
                                t.setUpdateTime(System.currentTimeMillis());
                                TestPlanApiScenarioExample example = new TestPlanApiScenarioExample();
                                example.createCriteria().andTestPlanIdEqualTo(request.getPlanId()).andApiScenarioIdEqualTo(t.getApiScenarioId());
                                if (testPlanApiScenarioMapper.countByExample(example) <= 0) {
                                    testPlanApiScenarioMapper.insert(t);
                                }
                            }

                        }
                    });
                });
            }
        }
        TestPlan testPlan = testPlanMapper.selectByPrimaryKey(request.getPlanId());
        if (StringUtils.equals(testPlan.getStatus(), TestPlanStatus.Prepare.name()) || StringUtils.equals(testPlan.getStatus(), TestPlanStatus.Completed.name())) {
            testPlan.setStatus(TestPlanStatus.Underway.name());
            testPlan.setActualStartTime(System.currentTimeMillis());  // 将状态更新为进行中时，开始时间也要更新
            testPlan.setActualEndTime(null);
            testPlanMapper.updateByPrimaryKey(testPlan);
        }
    }

    public List<TestPlan> recentTestPlans(String projectId) {
        return extTestPlanMapper.listRecent(SessionUtils.getUserId(), projectId);
    }

    public List<TestPlan> listTestAllPlan(QueryTestPlanRequest request) {
        String projectId = request.getProjectId();
        if (StringUtils.isBlank(projectId)) {
            return new ArrayList<>();
        }

        TestPlanExample example = new TestPlanExample();
        example.createCriteria().andProjectIdEqualTo(projectId);
        return testPlanMapper.selectByExample(example);
    }

    public List<TestPlanDTOWithMetric> listRelateAllPlan() {
        SessionUser user = SessionUtils.getUser();
        QueryTestPlanRequest request = new QueryTestPlanRequest();
        request.setPrincipal(user.getId());
        request.setWorkspaceId(SessionUtils.getCurrentWorkspaceId());
        request.setProjectId(SessionUtils.getCurrentProjectId());
        request.setPlanIds(extTestPlanTestCaseMapper.findRelateTestPlanId(user.getId(), SessionUtils.getCurrentWorkspaceId(), SessionUtils.getCurrentProjectId()));
        List<TestPlanDTOWithMetric> testPlans = extTestPlanMapper.listRelate(request);
        calcTestPlanRate(testPlans);
        return testPlans;
    }

    public List<TestPlanCaseDTO> listTestCaseByPlanId(String planId) {
        QueryTestPlanCaseRequest request = new QueryTestPlanCaseRequest();
        request.setPlanId(planId);
        return testPlanTestCaseService.list(request);
    }

    private List<TestPlanCaseDTO> listTestCaseByProjectIds(List<String> projectIds) {
        if (CollectionUtils.isEmpty(projectIds)) {
            return new ArrayList<>();
        }
        return extTestPlanTestCaseMapper.listTestCaseByProjectIds(projectIds);
    }

    public TestCaseReportMetricDTO getMetric(String planId) {
        QueryTestPlanRequest queryTestPlanRequest = new QueryTestPlanRequest();
        queryTestPlanRequest.setId(planId);

        TestPlanDTO testPlan = extTestPlanMapper.list(queryTestPlanRequest).get(0);
        Project project = projectMapper.selectByPrimaryKey(testPlan.getProjectId());
        testPlan.setProjectName(project.getName());

        TestCaseReport testCaseReport = testCaseReportMapper.selectByPrimaryKey(testPlan.getReportId());
        JSONObject content = JSONObject.parseObject(testCaseReport.getContent());
        JSONArray componentIds = content.getJSONArray("components");

        List<ReportComponent> components = ReportComponentFactory.createComponents(componentIds.toJavaList(String.class), testPlan);
        List<IssuesDao> issues = buildFunctionalCaseReport(planId, components);
        buildApiCaseReport(planId, components);
        buildScenarioCaseReport(planId, components);
        buildLoadCaseReport(planId, components);

        TestCaseReportMetricDTO testCaseReportMetricDTO = new TestCaseReportMetricDTO();
        components.forEach(component -> {
            component.afterBuild(testCaseReportMetricDTO);
        });
        testCaseReportMetricDTO.setIssues(issues);
        return testCaseReportMetricDTO;
    }

    public List<TestPlan> getTestPlanByIds(List<String> planIds) {
        if (CollectionUtils.isEmpty(planIds)) {
            return new ArrayList<>();
        }
        TestPlanExample example = new TestPlanExample();
        example.createCriteria().andIdIn(planIds);
        return testPlanMapper.selectByExample(example);
    }

    public void editTestPlanStatus(String planId) {
        TestPlan testPlan = new TestPlan();
        testPlan.setId(planId);
        String status = calcTestPlanStatus(planId);
        testPlan.setStatus(status);
        testPlanMapper.updateByPrimaryKeySelective(testPlan);
        TestPlan testPlans = getTestPlan(planId);
        List<String> userIds = new ArrayList<>();
        userIds.add(testPlans.getCreator());
        AddTestPlanRequest _testPlans = new AddTestPlanRequest();
        if (StringUtils.equals(TestPlanStatus.Completed.name(), testPlans.getStatus())) {
            try {
                BeanUtils.copyBean(_testPlans, testPlans);
                String context = getTestPlanContext(_testPlans, NoticeConstants.Event.UPDATE);
                User user = userMapper.selectByPrimaryKey(_testPlans.getCreator());
                Map<String, Object> paramMap = getTestPlanParamMap(_testPlans);
                paramMap.put("creator", user.getName());
                NoticeModel noticeModel = NoticeModel.builder().context(context).relatedUsers(userIds).subject(Translator.get("test_plan_notification")).mailTemplate("TestPlanEnd").paramMap(paramMap).event(NoticeConstants.Event.UPDATE).build();
                noticeSendService.send(NoticeConstants.TaskType.TEST_PLAN_TASK, noticeModel);
            } catch (Exception e) {
                LogUtil.error(e.getMessage(), e);
            }
        }

    }


    private String calcTestPlanStatus(String planId) {
        // test-plan-functional-case status
        List<String> funcStatusList = extTestPlanTestCaseMapper.getStatusByPlanId(planId);
        for (String funcStatus : funcStatusList) {
            if (StringUtils.equals(funcStatus, TestPlanTestCaseStatus.Prepare.name()) || StringUtils.equals(funcStatus, TestPlanTestCaseStatus.Underway.name())) {
                return TestPlanStatus.Underway.name();
            }
        }

        // test-plan-api-case status
        List<String> apiStatusList = extTestPlanApiCaseMapper.getStatusByTestPlanId(planId);
        for (String apiStatus : apiStatusList) {
            if (apiStatus == null) {
                return TestPlanStatus.Underway.name();
            }
        }

        // test-plan-scenario-case status
        List<String> scenarioStatusList = extTestPlanScenarioCaseMapper.getExecResultByPlanId(planId);
        for (String scenarioStatus : scenarioStatusList) {
            if (scenarioStatus == null) {
                return TestPlanStatus.Underway.name();
            }
        }

        // test-plan-load-case status
        List<String> loadStatusList = extTestPlanLoadCaseMapper.getStatusByTestPlanId(planId);
        for (String loadStatus : loadStatusList) {
            if (loadStatus == null) {
                return TestPlanStatus.Underway.name();
            }
        }

        return TestPlanStatus.Completed.name();
    }

    public String getProjectNameByPlanId(String testPlanId) {
        List<String> projectIds = testPlanProjectService.getProjectIdsByPlanId(testPlanId);
        ProjectExample projectExample = new ProjectExample();
        projectExample.createCriteria().andIdIn(projectIds);

        List<Project> projects = projectMapper.selectByExample(projectExample);
        StringBuilder stringBuilder = new StringBuilder();
        String projectName = "";

        if (projects.size() > 0) {
            for (Project project : projects) {
                stringBuilder.append(project.getName()).append("、");
            }
            if (StringUtils.isNotBlank(stringBuilder)) {
                projectName = stringBuilder.substring(0, stringBuilder.length() - 1);
            }
        }

        return projectName;
    }

    private String getTestPlanContext(AddTestPlanRequest testPlan, String type) {
        User user = userMapper.selectByPrimaryKey(testPlan.getCreator());
        Long startTime = testPlan.getPlannedStartTime();
        Long endTime = testPlan.getPlannedEndTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String start = null;
        String sTime = String.valueOf(startTime);
        String eTime = String.valueOf(endTime);
        if (!sTime.equals("null")) {
            start = sdf.format(new Date(Long.parseLong(sTime)));
        } else {
            start = "未设置";
        }
        String end = null;
        if (!eTime.equals("null")) {
            end = sdf.format(new Date(Long.parseLong(eTime)));
        } else {
            end = "未设置";
        }
        String context = "";
        if (StringUtils.equals(NoticeConstants.Event.CREATE, type)) {
            context = "测试计划任务通知：" + user.getName() + "创建的" + "'" + testPlan.getName() + "'" + "待开始，计划开始时间是:" + "'" + start + "'" + ";" + "计划结束时间是:" + "'" + end + "'" + " " + "请跟进";
        } else if (StringUtils.equals(NoticeConstants.Event.UPDATE, type)) {
            String status = "";
            if (StringUtils.equals(TestPlanStatus.Underway.name(), testPlan.getStatus())) {
                status = "进行中";
            } else if (StringUtils.equals(TestPlanStatus.Prepare.name(), testPlan.getStatus())) {
                status = "未开始";
            } else if (StringUtils.equals(TestPlanStatus.Completed.name(), testPlan.getStatus())) {
                status = "已完成";
            }
            context = "测试计划任务通知：" + user.getName() + "创建的" + "'" + testPlan.getName() + "'" + "计划开始时间是:" + "'" + start + "'" + ";" + "计划结束时间是:" + "'" + end + "'" + " " + status;
        } else if (StringUtils.equals(NoticeConstants.Event.DELETE, type)) {
            context = "测试计划任务通知：" + user.getName() + "创建的" + "'" + testPlan.getName() + "'" + "计划开始时间是:" + "'" + start + "'" + ";" + "计划结束时间是:" + "'" + end + "'" + " " + "已删除";
        }
        return context;
    }

    public TestCaseReportMetricDTO getStatisticsMetric(String planId) {
        QueryTestPlanRequest queryTestPlanRequest = new QueryTestPlanRequest();
        queryTestPlanRequest.setId(planId);

        TestPlanDTO testPlan = extTestPlanMapper.list(queryTestPlanRequest).get(0);
        Project project = projectMapper.selectByPrimaryKey(testPlan.getProjectId());
        testPlan.setProjectName(project.getName());

        TestCaseReport testCaseReport = testCaseReportMapper.selectByPrimaryKey(testPlan.getReportId());
        JSONObject content = JSONObject.parseObject(testCaseReport.getContent());
        JSONArray componentIds = content.getJSONArray("components");

        List<ReportComponent> components = ReportComponentFactory.createComponents(componentIds.toJavaList(String.class), testPlan);
        List<IssuesDao> issues = buildFunctionalCaseReport(planId, components);
        buildApiCaseReport(planId, components);
        buildScenarioCaseReport(planId, components);
        buildLoadCaseReport(planId, components);

        TestCaseReportMetricDTO testCaseReportMetricDTO = new TestCaseReportMetricDTO();
        components.forEach(component -> {
            component.afterBuild(testCaseReportMetricDTO);
        });
        testCaseReportMetricDTO.setIssues(issues);
        return testCaseReportMetricDTO;
    }

    public void buildApiCaseReport(String planId, List<ReportComponent> components) {
        ApiTestCaseRequest request = new ApiTestCaseRequest();
        request.setPlanId(planId);
        List<TestPlanApiCaseDTO> apiCaseDTOS = testPlanApiCaseService.list(request);
        for (TestPlanApiCaseDTO item : apiCaseDTOS) {
            for (ReportComponent component : components) {
                component.readRecord(item);
            }
        }
    }

    public void buildScenarioCaseReport(String planId, List<ReportComponent> components) {
        TestPlanScenarioRequest request = new TestPlanScenarioRequest();
        request.setPlanId(planId);
        List<ApiScenarioDTO> scenarioDTOS = testPlanScenarioCaseService.list(request);
        for (ApiScenarioDTO item : scenarioDTOS) {
            for (ReportComponent component : components) {
                component.readRecord(item);
            }
        }
    }

    public void buildLoadCaseReport(String planId, List<ReportComponent> components) {
        LoadCaseRequest request = new LoadCaseRequest();
        request.setTestPlanId(planId);
        List<TestPlanLoadCaseDTO> loadDTOs = testPlanLoadCaseService.list(request);
        for (TestPlanLoadCaseDTO item : loadDTOs) {
            for (ReportComponent component : components) {
                component.readRecord(item);
            }
        }
    }

    public List<IssuesDao> buildFunctionalCaseReport(String planId, List<ReportComponent> components) {
        List<TestPlanCaseDTO> testPlanTestCases = listTestCaseByPlanId(planId);
        List<IssuesDao> issues = new ArrayList<>();
        for (TestPlanCaseDTO testCase : testPlanTestCases) {
            List<IssuesDao> issue = issuesService.getIssues(testCase.getCaseId());
            if (issue.size() > 0) {
                for (IssuesDao i : issue) {
                    i.setModel(testCase.getNodePath());
                    i.setProjectName(testCase.getProjectName());
                    if (StringUtils.isNotBlank(i.getDescription())) {
                        String des = i.getDescription().replaceAll("<p>", "").replaceAll("</p>", "");
                        i.setDescription(des);
                    }
                    if (i.getLastModify() == null || i.getLastModify() == "") {
                        if (i.getReporter() != null || i.getReporter() != "") {
                            i.setLastModify(i.getReporter());
                        }
                    }
                }
                issues.addAll(issue);
                Collections.sort(issues, Comparator.comparing(Issues::getCreateTime, (t1, t2) -> {
                    if (t1 == null) {
                        return 1;
                    } else if (t2 == null) {
                        return -1;
                    }
                    return t2.compareTo(t1);
                }));
            }
            components.forEach(component -> {
                component.readRecord(testCase);
            });
        }
        return issues;
    }

    public List<TestPlanDTO> selectTestPlanByRelevancy(QueryTestPlanRequest params) {
        return extTestPlanMapper.selectTestPlanByRelevancy(params);
    }

    public String findTestProjectNameByTestPlanID(String testPlanId) {
        return extTestPlanMapper.findTestProjectNameByTestPlanID(testPlanId);
    }

    public String findScheduleCreateUserById(String testPlanId) {
        return extTestPlanMapper.findScheduleCreateUserById(testPlanId);
    }

    public String scenarioRunModeConfig(SchedulePlanScenarioExecuteRequest planScenarioExecuteRequest) {
        Map<String, Map<String, String>> testPlanScenarioIdMap = planScenarioExecuteRequest.getTestPlanScenarioIDMap();

        String returnStr = null;
        for (Map.Entry<String, Map<String, String>> entry : testPlanScenarioIdMap.entrySet()) {
            Map<String, String> scenarioMap = entry.getValue();

            RunScenarioRequest request = new RunScenarioRequest();
            request.setReportId(planScenarioExecuteRequest.getReportId());
            request.setEnvironmentId(planScenarioExecuteRequest.getEnvironmentId());
            request.setTriggerMode(planScenarioExecuteRequest.getTriggerMode());
            request.setExecuteType(planScenarioExecuteRequest.getExecuteType());
            request.setRunMode(planScenarioExecuteRequest.getRunMode());
            request.setIds(new ArrayList<>(scenarioMap.keySet()));//场景IDS
            request.setReportUserID(planScenarioExecuteRequest.getReportUserID());
            request.setScenarioTestPlanIdMap(scenarioMap);//未知
            request.setConfig(planScenarioExecuteRequest.getConfig());
            request.setTestPlanScheduleJob(true);
            request.setTestPlanReportId(planScenarioExecuteRequest.getTestPlanReportId());
//            request.getConfig().getReportType()
            request.setId(UUID.randomUUID().toString());
            if (request.getConfig() != null) {
                if (request.getConfig().getMode().equals(RunModeConstants.PARALLEL.toString())) {
                    // 校验并发数量
                    int count = 50;
                    BaseSystemConfigDTO dto = systemParameterService.getBaseInfo();
                    if (StringUtils.isNotEmpty(dto.getConcurrency())) {
                        count = Integer.parseInt(dto.getConcurrency());
                    }
                    if (request.getIds().size() > count) {
                        MSException.throwException("并发数量过大，请重新选择！");
                    }
                    returnStr = apiAutomationService.modeRun(request);
                } else {
                    returnStr = apiAutomationService.modeRun(request);
                }
            } else {
                returnStr = apiAutomationService.excute(request);
            }
        }
        return returnStr;
    }

    /**
     * 测试计划的定时任务--执行场景案例
     *
     * @param request
     * @return
     */
    public String runScenarioCase(SchedulePlanScenarioExecuteRequest request) {
        String returnId = "";
        MsTestPlan testPlan = new MsTestPlan();
        testPlan.setHashTree(new LinkedList<>());
        HashTree jmeterHashTree = new ListedHashTree();
        Map<String, Map<String, String>> testPlanScenarioIdMap = request.getTestPlanScenarioIDMap();

        for (Map.Entry<String, Map<String, String>> entry : testPlanScenarioIdMap.entrySet()) {
            Map<String, String> planScenarioIdMap = entry.getValue();

            try {
                returnId = this.generateHashTreeByScenarioList(testPlan, planScenarioIdMap, request);
            } catch (Exception ex) {
                MSException.throwException(ex.getMessage());
            }

            testPlan.toHashTree(jmeterHashTree, testPlan.getHashTree(), new ParameterConfig());
            String runMode = ApiRunMode.SCHEDULE_SCENARIO_PLAN.name();
            // 调用执行方法
            jMeterService.runLocal(request.getId(), jmeterHashTree, request.getReportId(), runMode);
        }

        return returnId;
    }

    private String generateHashTreeByScenarioList(MsTestPlan testPlan, Map<String, String> planScenarioIdMap, SchedulePlanScenarioExecuteRequest request) throws Exception {
        String returnId = "";
        boolean isFirst = true;
        List<ApiScenarioWithBLOBs> apiScenarios = extApiScenarioMapper.selectIds(new ArrayList<>(planScenarioIdMap.keySet()));
        for (ApiScenarioWithBLOBs item : apiScenarios) {
            String apiScenarioID = item.getId();
            String planScenarioID = planScenarioIdMap.get(apiScenarioID);
            if (StringUtils.isEmpty(planScenarioID)) {
                continue;
            }
            if (item.getStepTotal() == 0) {
                // 只有一个场景且没有测试步骤，则提示
                if (apiScenarios.size() == 1) {
                    MSException.throwException((item.getName() + "，" + Translator.get("automation_exec_info")));
                }
                LogUtil.warn(item.getName() + "，" + Translator.get("automation_exec_info"));
                continue;
            }
            MsThreadGroup group = new MsThreadGroup();
            group.setLabel(item.getName());
            group.setName(UUID.randomUUID().toString());
            // 批量执行的结果直接存储为报告
            if (isFirst) {
                group.setName(request.getId());
                isFirst = false;
            }
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JSONObject element = JSON.parseObject(item.getScenarioDefinition());
            MsScenario scenario = JSONObject.parseObject(item.getScenarioDefinition(), MsScenario.class);
            group.setOnSampleError(scenario.getOnSampleError());
            // 多态JSON普通转换会丢失内容，需要通过 ObjectMapper 获取
            if (element != null && StringUtils.isNotEmpty(element.getString("hashTree"))) {
                LinkedList<MsTestElement> elements = mapper.readValue(element.getString("hashTree"), new TypeReference<LinkedList<MsTestElement>>() {
                });
                scenario.setHashTree(elements);
            }
            if (StringUtils.isNotEmpty(element.getString("variables"))) {
                LinkedList<ScenarioVariable> variables = mapper.readValue(element.getString("variables"), new TypeReference<LinkedList<ScenarioVariable>>() {
                });
                scenario.setVariables(variables);
            }
            group.setEnableCookieShare(scenario.isEnableCookieShare());
            LinkedList<MsTestElement> scenarios = new LinkedList<>();
            scenarios.add(scenario);
            // 创建场景报告
            //不同的运行模式，第二个参数入参不同
            APIScenarioReportResult report = apiAutomationService.createScenarioReport(group.getName(), planScenarioID + ":" + request.getTestPlanReportId(), item.getName(), request.getTriggerMode() == null ? ReportTriggerMode.MANUAL.name() : request.getTriggerMode(), request.getExecuteType(), item.getProjectId(), request.getReportUserID(), request.getConfig());
            apiScenarioReportMapper.insert(report);
            group.setHashTree(scenarios);
            testPlan.getHashTree().add(group);
            returnId = request.getId();
        }
        return returnId;
    }

    public String run(String testPlanID, String projectID, String userId, String triggerMode, String apiRunConfig) {
        if (StringUtils.isEmpty(apiRunConfig)) {
            apiRunConfig = "{\"mode\":\"parallel\"," + "\"reportType\":\"iddReport\"," + "\"onSampleError\":true," + "\"runWithinResourcePool\":true," + "\"resourcePoolId\":\"29773f4f-55e4-4bce-ad3d-b531b4eb59c2\"}";
        }
        //创建测试报告，然后返回的ID重新赋值为resourceID，作为后续的参数
        TestPlanScheduleReportInfoDTO reportInfoDTO = testPlanReportService.genTestPlanReportBySchedule(projectID, testPlanID, userId, triggerMode);
        TestPlanReport testPlanReport = reportInfoDTO.getTestPlanReport();
        Map<String, String> planScenarioIdMap = reportInfoDTO.getPlanScenarioIdMap();
        Map<String, String> apiTestCaseIdMap = reportInfoDTO.getApiTestCaseIdMap();
        Map<String, String> performanceIdMap = reportInfoDTO.getPerformanceIdMap();

        String planReportId = testPlanReport.getId();

        //不同任务的执行ID
        Map<String, String> executePerformanceIdMap = new HashMap<>();
        Map<String, String> executeApiCaseIdMap = new HashMap<>();
        Map<String, String> executeScenarioCaseIdMap = new HashMap<>();

        //执行性能测试任务
        List<String> performaneReportIDList = new ArrayList<>();

        for (Map.Entry<String, String> entry : performanceIdMap.entrySet()) {
            String id = entry.getKey();
            String caseID = entry.getValue();
            RunTestPlanRequest performanceRequest = new RunTestPlanRequest();
            performanceRequest.setId(caseID);
            performanceRequest.setTestPlanLoadId(caseID);
            if (StringUtils.equals(ReportTriggerMode.API.name(), triggerMode)) {
                performanceRequest.setTriggerMode(ReportTriggerMode.TEST_PLAN_API.name());
            } else {
                performanceRequest.setTriggerMode(ReportTriggerMode.TEST_PLAN_SCHEDULE.name());
            }
            String reportId = null;
            try {
                reportId = performanceTestService.run(performanceRequest);
                if (reportId != null) {
                    performaneReportIDList.add(reportId);
                    TestPlanLoadCase testPlanLoadCase = new TestPlanLoadCase();
                    testPlanLoadCase.setId(performanceRequest.getTestPlanLoadId());
                    testPlanLoadCase.setLoadReportId(reportId);
                    testPlanLoadCaseService.update(testPlanLoadCase);

                    //更新关联处的报告
                    TestPlanLoadCase loadCase = new TestPlanLoadCaseDTO();
                    loadCase.setId(id);
                    loadCase.setLoadReportId(reportId);
                    testPlanLoadCaseService.update(loadCase);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (StringUtils.isNotEmpty(reportId)) {
                executePerformanceIdMap.put(caseID, TestPlanApiExecuteStatus.RUNNING.name());
            }
        }
        if (!performaneReportIDList.isEmpty()) {
            //性能测试时保存性能测试报告ID，在结果返回时用于捕捉并进行
            testPlanReportService.updatePerformanceInfo(testPlanReport, performaneReportIDList, triggerMode);

        }

        for (Map.Entry<String, String> entry : apiTestCaseIdMap.entrySet()) {
            String apiCaseID = entry.getKey();
            executeApiCaseIdMap.put(apiCaseID, TestPlanApiExecuteStatus.RUNNING.name());
        }
        for (String id : planScenarioIdMap.keySet()) {
            executeScenarioCaseIdMap.put(id, TestPlanApiExecuteStatus.RUNNING.name());
        }
        testPlanReportService.updateExecuteApis(planReportId, executeApiCaseIdMap, executeScenarioCaseIdMap, executePerformanceIdMap);


        //执行接口案例任务
        for (Map.Entry<String, String> entry : apiTestCaseIdMap.entrySet()) {
            String apiCaseID = entry.getKey();
//            String planCaseID = entry.getValue();
            ApiTestCaseWithBLOBs blobs = apiTestCaseService.get(apiCaseID);
            //需要更新这里来保证PlanCase的状态能正常更改
            if (StringUtils.equals(triggerMode, ReportTriggerMode.API.name())) {
                apiTestCaseService.run(blobs, UUID.randomUUID().toString(), planReportId, testPlanID, ApiRunMode.JENKINS_API_PLAN.name());
            } else {
                apiTestCaseService.run(blobs, UUID.randomUUID().toString(), planReportId, testPlanID, ApiRunMode.SCHEDULE_API_PLAN.name());
            }
            executeApiCaseIdMap.put(apiCaseID, TestPlanApiExecuteStatus.RUNNING.name());
        }

        //执行场景执行任务
        if (!planScenarioIdMap.isEmpty()) {
            SchedulePlanScenarioExecuteRequest scenarioRequest = new SchedulePlanScenarioExecuteRequest();
            String senarionReportID = UUID.randomUUID().toString();
            scenarioRequest.setId(senarionReportID);
            scenarioRequest.setReportId(senarionReportID);
            scenarioRequest.setProjectId(projectID);
            if (StringUtils.equals(triggerMode, ReportTriggerMode.API.name())) {
                scenarioRequest.setTriggerMode(ReportTriggerMode.API.name());
                scenarioRequest.setRunMode(ApiRunMode.JENKINS_SCENARIO_PLAN.name());

            } else {
                scenarioRequest.setTriggerMode(ReportTriggerMode.SCHEDULE.name());
                scenarioRequest.setRunMode(ApiRunMode.SCHEDULE_SCENARIO_PLAN.name());
            }
            scenarioRequest.setExecuteType(ExecuteType.Saved.name());
            Map<String, Map<String, String>> testPlanScenarioIdMap = new HashMap<>();
            testPlanScenarioIdMap.put(testPlanID, planScenarioIdMap);
            scenarioRequest.setTestPlanScenarioIDMap(testPlanScenarioIdMap);
            scenarioRequest.setReportUserID(userId);
            scenarioRequest.setTestPlanID(testPlanID);

            scenarioRequest.setTestPlanReportId(planReportId);
            RunModeConfig runModeConfig = JSONObject.parseObject(apiRunConfig, RunModeConfig.class);
            scenarioRequest.setConfig(runModeConfig);
            String scenarioReportID = this.scenarioRunModeConfig(scenarioRequest);
//            if (StringUtils.isNotEmpty(scenarioReportID)) {
//                scenarioIsExcuting = true;
//                scenarioCaseIdArray = JSONArray.toJSONString(new ArrayList<>(planScenarioIdMap.keySet()));
//            }
        }
        return testPlanReport.getId();
    }

    public String getLogDetails(String id) {
        TestPlan plan = testPlanMapper.selectByPrimaryKey(id);
        if (plan != null) {
            List<DetailColumn> columns = ReflexObjectUtil.getColumns(plan, TestPlanReference.testPlanColumns);
            OperatingLogDetails details = new OperatingLogDetails(JSON.toJSONString(id), plan.getProjectId(), plan.getName(), plan.getCreator(), columns);
            return JSON.toJSONString(details);
        }
        return null;
    }

    public String getLogDetails(PlanCaseRelevanceRequest request) {
        List<String> testCaseIds = request.getIds();
        TestCaseExample example = new TestCaseExample();
        example.createCriteria().andIdIn(testCaseIds);
        List<TestCase> cases = testCaseMapper.selectByExample(example);
        List<String> names = cases.stream().map(TestCase::getName).collect(Collectors.toList());
        TestPlan testPlan = testPlanMapper.selectByPrimaryKey(request.getPlanId());
        if (testPlan != null) {
            List<DetailColumn> columns = new LinkedList<>();
            OperatingLogDetails details = new OperatingLogDetails(JSON.toJSONString(testCaseIds), testPlan.getProjectId(), String.join(",", names) + " 关联到 " + "【" + testPlan.getName() + "】", testPlan.getCreator(), columns);
            return JSON.toJSONString(details);
        }
        return null;
    }

    /**
     * 根据迭代id获取测试计划列表
     *
     * @param testPlanBody 迭代id + name
     * @return 测试计划列表
     */
    public List<TestPlan> selectIterationTestPlan(RequirementRequest testPlanBody) {
        List<TestPlan> testPlans = new ArrayList<>();
        List<Iteration> iteration = iterationMapper.getIterationByIterationCode(testPlanBody.getIterationCode());
        log.info("根据迭代id获取测试计划列表 iteration: {}", iteration);
        if (null != testPlanBody.getProjectId()) {
            iteration = iteration.stream().filter(s -> s.getProjectId().equals(testPlanBody.getProjectId())).collect(Collectors.toList());
        }
        if (iteration.isEmpty()) {
            return testPlans;
        }
        testPlans = testPlanMapper.selectByIterationId(iteration.get(0).getId());
        try {
            if (null != testPlanBody.getName() && !testPlanBody.getName().equals("")) {
                return testPlans.stream().filter(s -> s.getName().contains(testPlanBody.getName())).collect(Collectors.toList());
            }
            return testPlans;
        } catch (Exception e) {
            log.error("迭代下过滤测试计划有误:{}", e.getMessage());
            return testPlans;
        }
    }
}
