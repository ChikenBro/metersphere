package io.metersphere.iteration;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.metersphere.commons.utils.PageUtils;
import io.metersphere.commons.utils.Pager;
import io.metersphere.config.ResponseHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/iteration")
@RestController
@Slf4j
public class IterationController {
    @Autowired
    private IterationService iterationService;
    @Autowired
    private RequirementService requirementService;

    /**
     * 同步coding迭代
     *
     * @return
     */
    @PostMapping("/sync/coding")
    public JSONObject syncIteration(@RequestBody JSONObject jsonObject) {
        return iterationService.syncIteration(jsonObject);
    }

    /**
     * 查询迭代列表页面
     *
     * @param goPage   页数
     * @param pageSize 当前页数数据
     * @param request  请求参数
     * @return 过滤后的迭代数据
     */
    @PostMapping("/list/{goPage}/{pageSize}")
    public JSONObject iterationList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody IterationSyncRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return iterationService.list(goPage, pageSize, request);
    }

    /**
     * 查询需求详情页面
     *
     * @param requirementRequest 请求参数
     * @return 需求列表数据
     */
    @PostMapping("/requirement/{goPage}/{pageSize}")
    public JSONObject requirementList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody RequirementRequest requirementRequest) {
        return requirementService.list(goPage, pageSize, requirementRequest);
    }

    @PostMapping("/report")
    public JSONObject iterationReport(@RequestBody RequirementRequest iterationReport) {
        return iterationService.iterationReport(iterationReport);
    }
}
