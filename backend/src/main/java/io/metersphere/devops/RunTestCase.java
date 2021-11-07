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
        Map<String, String> map = new HashMap<>();
        map.put("mdc", "目睹云");
        String localServerName = serverRequest.getServerName();
        int localServerPort = serverRequest.getServerPort();

        String env = request.getEnv();
        String product = request.getProduct();
        String serverName = request.getServerName();
        String productName = map.get(product);
        return testCase.getTestCase(env, product, serverName, productName, localServerName, localServerPort);
    }

    @GetMapping("/task/{reportId}")
    public Map<String, Integer> getTaskStatus(@PathVariable String reportId) {
        return testCase.getTestCaseReportStatus(reportId);
    }
}
