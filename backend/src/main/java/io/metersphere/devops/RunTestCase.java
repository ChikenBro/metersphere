package io.metersphere.devops;

import io.metersphere.api.dto.runTestRequest;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping(value = "devOps/api/test/case")
public class RunTestCase {

    @Resource
    private TestCase testCase;
    @Resource
    private HttpServletRequest serverRequest;


    @PostMapping(value = "/run")
    public TestCaseRequest runTestCase(@RequestBody runTestRequest request) {
        String localServerName = serverRequest.getServerName();
        int localServerPort = serverRequest.getServerPort();

        String env = request.getEnv();
        String product = request.getProduct();
        String serverName = request.getServerName();
        return testCase.getTestCase(env, product, serverName, localServerName, localServerPort);
    }

    @GetMapping("/task/{reportId}")
    public Map<String, Integer> getTaskStatus(@PathVariable String reportId) {
        return testCase.getTestCaseReportStatus(reportId);
    }
}
