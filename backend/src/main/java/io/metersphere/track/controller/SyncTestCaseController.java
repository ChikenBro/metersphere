package io.metersphere.track.controller;

import io.metersphere.commons.exception.MSException;
import io.metersphere.track.dto.RemoteTestCaseModuleDTO;
import io.metersphere.track.dto.TestCaseNodeDTO;
import io.metersphere.track.service.SyncTestCaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("sync")
@RestController
public class SyncTestCaseController {
    @Autowired
    private SyncTestCaseService syncTestCaseService;

    @GetMapping(value = "/testcase/module/{projectId}/list")
    public List<TestCaseNodeDTO> list(@PathVariable String projectId) {
        if (StringUtils.isEmpty(projectId)) {
            throw MSException.getException("项目Id不能为空");
        }
        return syncTestCaseService.getTestCaseNode(projectId);
    }

    @PostMapping(value = "/testcase/{projectId}")
    public void syncOperation(@PathVariable String projectId, @RequestBody RemoteTestCaseModuleDTO remoteTestCaseModuleDTO) {
        if (StringUtils.isEmpty(projectId)) {
            throw MSException.getException("项目Id不能为空");
        }
        if (!remoteTestCaseModuleDTO.getProjectId().equals(projectId)) {
            throw MSException.getException("参数错误,项目Id不一致");
        }
        syncTestCaseService.syncTestCase(projectId,
                remoteTestCaseModuleDTO.getRemoteNodePath(),
                remoteTestCaseModuleDTO.getMsNodeId(),
                remoteTestCaseModuleDTO.getMsNodePath());
    }
}
