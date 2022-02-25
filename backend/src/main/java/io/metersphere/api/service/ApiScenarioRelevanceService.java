package io.metersphere.api.service;

import io.metersphere.api.dto.datacount.ApiMethodUrlDTO;
import io.metersphere.base.domain.ApiScenarioRelevance;
import io.metersphere.base.mapper.ApiScenarioRelevanceServiceMapper;
import io.metersphere.commons.utils.LogUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@Transactional(rollbackFor = Exception.class)
public class ApiScenarioRelevanceService {

    @Resource
    private ApiScenarioRelevanceServiceMapper apiScenarioRelevanceServiceMapper;

    /**
     * 场景与api的关联关系
     *
     * @param urlList   List<ApiMethodUrlDTO>
     * @param projectId String
     * @return
     */
    public ApiScenarioRelevance apiScenarioRelevance(List<ApiMethodUrlDTO> urlList, String projectId) {
        ApiScenarioRelevance returnModel = new ApiScenarioRelevance();
        System.out.println(urlList);
        //删除所有
        this.deleteApiScenarioRelevance(projectId);
        //插入关联数据
        this.createApiScenarioRelevance(urlList, projectId);
        return returnModel;
    }

    private void createApiScenarioRelevance(List<ApiMethodUrlDTO> urlList, String projectId) {
        List<ApiScenarioRelevance> testList = new ArrayList<>();
        for (ApiMethodUrlDTO url : urlList) {
            ApiScenarioRelevance test = new ApiScenarioRelevance();
            test.setId(UUID.randomUUID().toString());
            test.setScenarioId(url.scenarioId);
            test.setProjectId(projectId);
            test.setMethod(url.method);
            test.setPath(url.url);
            test.setStatus(0);
            test.setCreateTime(System.currentTimeMillis());
            test.setUpdateTime(System.currentTimeMillis());
            testList.add(test);
        }
        //插入关联数据
        int total = apiScenarioRelevanceServiceMapper.insertList(testList);
        LogUtil.info("当前插入接口场景关联api数据条数: " + total);
    }

    private void deleteApiScenarioRelevance(String projectId) {
        //删除当前项目api与scenarios关联数据
        int total = apiScenarioRelevanceServiceMapper.updateList(projectId);
        LogUtil.info("当前删除接口场景关联api数据条数: " + total);
    }
}
