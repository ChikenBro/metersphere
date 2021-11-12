package io.metersphere.devops;

import io.metersphere.api.dto.automation.RunScenarioRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TestCaseRequest {
    /**
     * 是否错误提示
     */
    private String message;
    /**
     * 运行环境
     */
    private String env;

    /**
     * 运行环境id
     */
    private String envId;

    /**
     * 业务线
     */
    private String product;

    /**
     * 服务名称
     */
    private String serverName;

    /**
     * 业务线名称
     */
    private String productName;

    /**
     * 场景id(服务)
     */
    private String projectId;

    /**
     * 接口测试运行场景ids
     */
    private List<String> scenarioIds;

    /**
     * 是否存在可执行场景ids
     */
    private boolean ifCase = true;

    /**
     * 接口测试报告名称
     */
    private String reportId;


    /**
     * 接口测试报告名称
     */
    private String reportName;

    /**
     * 接口测试运行状态 0：无任务、1：成功、2：运行中、3：失败
     */
    private int status = 0;

    /**
     * 可运行的接口场景请求入参
     */
    private RunScenarioRequest runScenarioRequest;

    /**
     * 当前服务器域名
     */
    private String localServerName;

    /**
     * 当前服务器端口
     */
    private int localServerPort;

    /**
     * 接口测试报告地址
     */
    private String testCaseReportUrl;


}
