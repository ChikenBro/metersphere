package io.metersphere.iteration;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.metersphere.commons.exception.CodingException;
import io.metersphere.commons.utils.LogUtil;
import io.metersphere.config.PlatformResponseConfig;
import io.metersphere.iteration.response.Options;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Component
@Slf4j
public class RequirementService {

    /**
     * 返回需求列表数据
     *
     * @param requirementRequest 查询需求列表入参
     * @return 需求列表数据
     */
    public JSONObject list(int goPage, int pageSize, RequirementRequest requirementRequest) {
        String prefix_domain = System.getProperty("coding.domain");
        String url = String.format("%s/iteration/requirement/%s/%s", prefix_domain, goPage, pageSize);
        LogUtil.info("requirement data: " + requirementRequest);
        String result = CodingException.checkCodingException(url, requirementRequest);
        return PlatformResponseConfig.parsePlatformDate(result);
    }
}
