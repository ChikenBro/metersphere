package io.metersphere.iteration;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.metersphere.commons.exception.CodingException;
import io.metersphere.commons.utils.LogUtil;
import io.metersphere.iteration.response.Options;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Component
@Slf4j
public class RequirementService {

    private List<Options> updateRequirementLists = new ArrayList<>();


    /**
     * 返回需求列表数据
     *
     * @param requirementRequest 查询需求列表入参
     * @return 需求列表数据
     */
    public JSONObject list(RequirementRequest requirementRequest) {
        String prefix_domain = System.getProperty("coding.domain");
        String url = String.format("%s/iteration/requirement", prefix_domain);
        LogUtil.info("requirement data: " + requirementRequest);
        String result = CodingException.checkCodingException(url, requirementRequest);
        return JSON.parseObject(result);
    }
}
