package io.metersphere.iteration;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.metersphere.commons.exception.CodingException;
import io.metersphere.commons.utils.LogUtil;
import io.metersphere.config.PlatformResponseConfig;
import io.metersphere.iteration.response.Options;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class IterationService {

    private List<Options> updateIterationLists = new ArrayList<>();


    /**
     * 同步迭代数据
     *
     * @param iterationSyncRequest 同步迭代对象
     * @return 同步成功个数
     */
    public JSONObject syncIteration(IterationSyncRequest iterationSyncRequest) {
        String prefix_domain = System.getProperty("coding.domain");
        String url = String.format("%s/iteration/sync/coding", prefix_domain);
        log.info("sync iteration request: {}, url: {}", iterationSyncRequest, url);
        String result = CodingException.checkCodingException(url, iterationSyncRequest);
        return PlatformResponseConfig.parsePlatformDate(result);
    }

    /**
     * 返回迭代列表数据
     *
     * @param request 查询迭代列表入参
     * @return 迭代列表数据
     */
    public JSONObject list(int goPage, int pageSize, IterationSyncRequest request) {
        String prefix_domain = System.getProperty("coding.domain");
        String url = String.format("%s/iteration/list/%s/%s", prefix_domain, goPage, pageSize);
        log.info("iteration list request: {}, url: {}", request, url);
        String result = CodingException.checkCodingException(url, request);
        return PlatformResponseConfig.parsePlatformDate(result);
    }

    /**
     * @param requirementRequest 迭代请求
     * @return 迭代报告
     */
    public JSONObject iterationReport(RequirementRequest requirementRequest) {
        String prefix_domain = System.getProperty("coding.domain");
        String url = String.format("%s/iteration/report", prefix_domain);
        log.info("iteration report request: {}, url: {}", requirementRequest, url);
        String result = CodingException.checkCodingException(url, requirementRequest);
        return PlatformResponseConfig.parsePlatformDate(result);
    }


}
