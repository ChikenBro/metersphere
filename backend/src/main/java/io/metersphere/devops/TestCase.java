package io.metersphere.devops;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.metersphere.api.dto.QueryAPIReportRequest;
import io.metersphere.api.dto.automation.APIScenarioReportResult;
import io.metersphere.api.dto.automation.ExecuteType;
import io.metersphere.api.dto.automation.RunModeConfig;
import io.metersphere.api.dto.automation.RunScenarioRequest;
import io.metersphere.api.service.ApiAutomationService;
import io.metersphere.api.service.ApiScenarioReportService;
import io.metersphere.base.domain.*;
import io.metersphere.base.mapper.*;
import io.metersphere.base.mapper.ext.ExtApiScenarioReportMapper;
import io.metersphere.commons.constants.ApiRunMode;
import io.metersphere.commons.constants.TriggerMode;
import io.metersphere.commons.utils.LogUtil;
import io.metersphere.commons.utils.PageUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TestCase {
    @Resource
    private WorkspaceMapper workspaceMapper;
    @Resource
    private ProjectMapper projectMapper;
    @Resource
    private ApiScenarioModuleMapper apiScenarioModuleMapper;
    @Resource
    private ApiScenarioMapper apiScenarioMapper;
    @Resource
    private ApiAutomationService apiAutomationService;
    @Resource
    private ExtApiScenarioReportMapper extApiScenarioReportMapper;
    @Resource
    private ApiScenarioReportService apiReportService;
    @Resource
    private ApiScenarioReportMapper apiScenarioReportMapper;
    @Resource
    private ApiTestEnvironmentMapper apiTestEnvironmentMapper;

    public TestCaseRequest getTestCase(String env, String product, String serverName, String localServerName, int localServerPort) {
        //筛选可执行的测试用例
        TestCaseRequest testCaseRequest = this.filterTestCase(env, product, serverName);
        //校验测试数据
        if (!checkTestCaseData(testCaseRequest)) {
            return testCaseRequest;
        }
        testCaseRequest.setEnvId(this.getProjectEnv(testCaseRequest));
        testCaseRequest.setLocalServerName(localServerName);
        testCaseRequest.setLocalServerPort(localServerPort);
        //组装接口自动化测试请求
        this.assembleTestCaseRequest(testCaseRequest);
        // todo 修改数据库方式改成批量
        for (String ScenarioId : testCaseRequest.getScenarioIds()) {
            ApiScenarioWithBLOBs apiScenarioWithBLOBs = apiScenarioMapper.selectByPrimaryKey(ScenarioId);
            String scenarioDefinition = apiScenarioWithBLOBs.getScenarioDefinition();
            //替换环境变量
            String envContent = "{\"" + testCaseRequest.getProjectId() + "\"" + ":" + "\"" + testCaseRequest.getEnvId() + "\"}}";
            String baseScenarioDefinition = scenarioDefinition.split("environmentMap\":")[0] + "environmentMap\":" + envContent;
            apiScenarioWithBLOBs.setScenarioDefinition(baseScenarioDefinition);
            apiScenarioWithBLOBs.setId(ScenarioId);
            apiScenarioMapper.updateByPrimaryKeySelective(apiScenarioWithBLOBs);
        }
        //运行接口场景
        this.runTestCase(testCaseRequest.getRunScenarioRequest());
        testCaseRequest.setStatus(2);
        //获取接口测试报告内容
        Optional<APIScenarioReportResult> reportResult = this.findTestCaseReport(testCaseRequest);
        testCaseRequest.setReportId(reportResult.get().getId());
        testCaseRequest.setTestCaseReportUrl(this.testCaseReportUrl(testCaseRequest));
        //设置触发方式为API调用
        apiScenarioReportMapper.updateByReportId("API", testCaseRequest.getReportName());
        return testCaseRequest;
    }

    /**
     * 校验接口测试数据
     *
     * @param testCaseRequest
     * @return false true
     */
    public boolean checkTestCaseData(TestCaseRequest testCaseRequest) {
        if (null == testCaseRequest.getScenarioIds()) {
            testCaseRequest.setMessage("模块不存在或业务线不存在");
            testCaseRequest.setIfCase(false);
            return false;
        }
        if (testCaseRequest.getScenarioIds().isEmpty()) {
            testCaseRequest.setMessage("无可执行的接口测试用例");
            testCaseRequest.setIfCase(false);
            return false;
        }
        if (null == this.getProjectEnv(testCaseRequest)) {
            testCaseRequest.setMessage("没有配置当前接口测试的环境");
            testCaseRequest.setIfCase(false);
            return false;
        }
        return true;
    }

    /**
     * 获取可用环境
     *
     * @param testCaseRequest
     * @return 环境id
     */
    public String getProjectEnv(TestCaseRequest testCaseRequest) {
        //是否有存在的环境
        List<ApiTestEnvironment> projectEnv = apiTestEnvironmentMapper.selectByProjectEnv(testCaseRequest.getProjectId());
        List optionalEnv = projectEnv.stream().filter(x -> x.getName().equals(testCaseRequest.getEnv())).collect(Collectors.toList())
                .stream().map(ApiTestEnvironment::getId).collect(Collectors.toList());
        if (optionalEnv.isEmpty()) {
            return null;
        }
        return optionalEnv.get(0).toString();
    }

    /**
     * 过滤可执行的接口测试场景
     *
     * @param env        环境
     * @param product    业务线英文名 mdc
     * @param serverName 服务名称
     * @return
     */
    public TestCaseRequest filterTestCase(String env, String product, String serverName) {
        TestCaseRequest testCaseRequest = new TestCaseRequest();
        testCaseRequest.setEnv(env);
        testCaseRequest.setProduct(product);
        testCaseRequest.setServerName(serverName);
        try {
            //查询业务线id
            String workspaceId = workspaceMapper.selectByProductName(product);
            //查询业务线所有项目id
            List<Project> projectIds = projectMapper.selectProjectId(workspaceId);
            List<String> allProjectId = projectIds.stream().map(Project::getId).collect(Collectors.toList());
            //查询模块id
            List<ApiScenarioModule> apiScenarioModules = apiScenarioModuleMapper.selectScenarioModuleId(allProjectId, serverName);
            String scenarioModuleId = apiScenarioModules.get(0).getId();
            testCaseRequest.setProjectId(apiScenarioModules.get(0).getProjectId());
            //查询接口自动化模块下所有场景id
            List<ApiScenario> scenarioIds = apiScenarioMapper.selectScenarioId(scenarioModuleId);
            //dev环境只跑P0与P1级接口用例
            if (env.equals("dev")) {
                List<ApiScenario> devApiScenario = scenarioIds.stream().filter(x -> x.getLevel().equals("P0") || x.getLevel().equals("P1")).collect(Collectors.toList());
                List<String> devScenarioIds = devApiScenario.stream().map(ApiScenario::getId).collect(Collectors.toList());
                testCaseRequest.setScenarioIds(devScenarioIds);
                return testCaseRequest;
            }
            testCaseRequest.setScenarioIds(scenarioIds.stream().map(ApiScenario::getId).collect(Collectors.toList()));
            return testCaseRequest;
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            return testCaseRequest;
        }
    }


    /**
     * 组装接口场景用例request
     *
     * @param testCaseRequest 组装成可执行的接口场景用例request
     * @return testCaseReport
     */
    private TestCaseRequest assembleTestCaseRequest(TestCaseRequest testCaseRequest) {
        RunScenarioRequest runScenarioRequest = new RunScenarioRequest();
        runScenarioRequest.setExecuteType(ExecuteType.Saved.name());
        runScenarioRequest.setTriggerMode(TriggerMode.BATCH.name());
        runScenarioRequest.setRunMode(ApiRunMode.SCENARIO.name());
        runScenarioRequest.setId(UUID.randomUUID().toString());
        RunModeConfig runModeConfig = new RunModeConfig();
        //运行模式 1、并行:parallel 2、串行:serial
        runModeConfig.setMode("serial");
        runModeConfig.setReportType("setReport");
        String reportName = this.setReportName(testCaseRequest.getProduct(), testCaseRequest.getServerName());
        runModeConfig.setReportName(reportName);
        runScenarioRequest.setConfig(runModeConfig);
        runScenarioRequest.setIds(testCaseRequest.getScenarioIds());
        runScenarioRequest.setProjectId(testCaseRequest.getProjectId());
        testCaseRequest.setRunScenarioRequest(runScenarioRequest);
        testCaseRequest.setReportName(reportName);
        return testCaseRequest;
    }


    /**
     * 设置接口测试自动化报告名称,业务线-服务名-当前时间-接口测试报告
     *
     * @param product    product
     * @param serverName serverName
     * @return reportName
     */
    public String setReportName(String product, String serverName) {
        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        return product + "-" + serverName + "-" + currentTime + "-接口测试报告";
    }

    /**
     * 运行接口测试自动化场景
     *
     * @param request request
     */
    public void runTestCase(RunScenarioRequest request) {
        //最大50个接口场景
        apiAutomationService.run(request);
    }

    /**
     * 获取接口测试报告内容
     *
     * @param testCaseRequest TestCaseRequest
     * @return 接口测试报告
     */
    public Optional<APIScenarioReportResult> findTestCaseReport(TestCaseRequest testCaseRequest) {
        QueryAPIReportRequest queryAPIReportRequest = new QueryAPIReportRequest();
        queryAPIReportRequest.setProjectId(testCaseRequest.getProjectId());
        //获取当前项目下最新5条测试报告
        Page<Object> page = PageHelper.startPage(1, 5, true);
        PageUtils.setPageInfo(page, apiReportService.list(queryAPIReportRequest));
        List<APIScenarioReportResult> testCaseReport = extApiScenarioReportMapper.list(queryAPIReportRequest);
        //根据接口测试报告名称过滤
        return testCaseReport.stream().filter(x -> x.getTestName().equals(testCaseRequest.getReportName())).findFirst();
    }

    /**
     * 接口测试报告地址
     *
     * @param testCaseRequest TestCaseRequest
     * @return testCaseReportUrl
     */
    public String testCaseReportUrl(TestCaseRequest testCaseRequest) {
        //没有port或者为80端口
        if (testCaseRequest.getLocalServerPort() == 80) {
            return "http://" + testCaseRequest.getLocalServerName() + "/#/api/automation/report/view/" + testCaseRequest.getReportId();
        }
        return "http://" + testCaseRequest.getLocalServerName() + ":" + testCaseRequest.getLocalServerPort() + "/#/api/automation/report/view/" + testCaseRequest.getReportId();
    }

    public Map<String, Integer> getTestCaseReportStatus(String reportId) {
        Map<String, Integer> map = new HashMap<>();
        //0：无任务、1：成功、2：运行中、3：失败
        int status;
        APIScenarioReportResult result = extApiScenarioReportMapper.get(reportId);
        if (null == result) {
            map.put("status", 0);
            return map;
        }
        switch (result.getStatus()) {
            case "Success":
                status = 1;
                break;
            case "Error":
                status = 3;
                break;
            default:
                status = 2;
                break;
        }
        map.put("status", status);
        return map;
    }
}
