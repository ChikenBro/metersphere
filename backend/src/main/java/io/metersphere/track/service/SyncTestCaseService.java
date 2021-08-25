package io.metersphere.track.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.metersphere.base.domain.*;
import io.metersphere.base.mapper.ProjectCaseConfigMapper;
import io.metersphere.base.mapper.TestCaseNodeMapper;
import io.metersphere.base.mapper.WorkspaceMapper;
import io.metersphere.commons.exception.MSException;
import io.metersphere.commons.utils.LogUtil;
import io.metersphere.commons.utils.SessionUtils;
import io.metersphere.service.ProjectService;
import io.metersphere.track.domain.FolderInfo;
import io.metersphere.track.dto.TestCaseNodeDTO;
import io.metersphere.track.issue.MDJiraPlatform;
import io.metersphere.track.issue.domain.Jira.XrayFolders;
import io.metersphere.track.issue.domain.Jira.XrayTestCases;
import io.metersphere.track.issue.domain.Jira.XrayTestStep;
import io.metersphere.track.issue.domain.Jira.XrayTreeNode;
import io.metersphere.track.request.testcase.IssuesRequest;
import io.metersphere.track.service.utils.TestCasePriority;
import org.apache.commons.lang3.StringUtils;
import org.python.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SyncTestCaseService {

    @Autowired
    private ProjectService projectService;
    @Autowired
    private WorkspaceMapper workspaceMapper;
    @Autowired
    private ProjectCaseConfigMapper projectCaseConfigMapper;
    @Autowired
    private TestCaseNodeService testCaseNodeService;
    @Autowired
    private TestCaseNodeMapper testCaseNodeMapper;
    @Autowired
    private TestCaseService testCaseService;

    private MDJiraPlatform mdJiraPlatform;


    public List<TestCaseNodeDTO> getTestCaseNode(String projectId) {
        XrayFolders xrayFolders = getXrayTestCaseNode(projectId);
        return transferTestCaseNode(xrayFolders, projectId);
    }

    public void syncTestCase(String projectId, String remoteNodePath, String msNodeId, String msNodePath) {
        ProjectCaseConfig caseConfig = projectCaseConfigMapper.selectByProjectId(projectId);
        // 查询节点是否包含子节点
        String[] nodePath = remoteNodePath.split("/");
        XrayFolders xrayFolders = getXrayTestCaseNode(projectId);
        List<XrayTreeNode> xrayTreeNodes = Lists.newArrayList();
        for (int i = 0; i < nodePath.length; i++) {
            int finalI = i;
            if (xrayTreeNodes.size() == 0) {
                xrayTreeNodes = xrayFolders.getFolders()
                        .stream()
                        .filter(v -> v.getName().equals(nodePath[finalI]))
                        .collect(Collectors.toList());
            } else {
                // TODO: 待优化
                xrayTreeNodes = xrayTreeNodes.get(0).getFolders().stream().filter(v -> v.getName().equals(nodePath[finalI])).collect(Collectors.toList());
            }
        }
        TestCaseNode testCaseNode = testCaseNodeMapper.selectByPrimaryKey(msNodeId);
        if (null == testCaseNode) {
            throw MSException.getException("测试用例模块不存在, nodeId: " + msNodeId);
        }
        List<FolderInfo> folderIds = Lists.newArrayList();
        getNodeIds(xrayTreeNodes, folderIds, projectId, msNodeId, msNodePath);
        // 待同步模块是否根节点
        for (FolderInfo folderInfo : folderIds) {
            XrayTestCases xrayTestCases = this.mdJiraPlatform.getXrayTests(caseConfig.getCaseProjectKey(), folderInfo.getId());
            // 获取节点用例内容
            if (xrayTestCases.getTotal() == 0) {
                continue;
            }
            // 测试用例入库
            xrayTestCases.getTests().forEach(testcase -> {
                try {
                    List<XrayTestStep> xrayTestStepList =
                            this.mdJiraPlatform.getXrayTestCaseSteps(testcase.getKey(), testcase.getId(), testcase.getTestType());
                    TestCaseWithBLOBs caseWithBLOBs = new TestCaseWithBLOBs();
                    caseWithBLOBs.setName(testcase.getSummary());
                    caseWithBLOBs.setId(UUID.randomUUID().toString());
                    caseWithBLOBs.setNodeId(folderInfo.getMsNodeId());
                    caseWithBLOBs.setNodePath(folderInfo.getMsNodePath());
                    caseWithBLOBs.setProjectId(projectId);
                    caseWithBLOBs.setMaintainer(SessionUtils.getUserId());
                    caseWithBLOBs.setPriority(TestCasePriority.getPriority(testcase.getPriority()));
                    caseWithBLOBs.setType("function");
                    if (testcase.getTestType().equals("Cucumber")) {
                        caseWithBLOBs.setStepModel("Cucumber");
                        caseWithBLOBs.setStepDescription(xrayTestStepList.get(0).getStep());
                    } else {
                        caseWithBLOBs.setStepModel("STEP");
                        List<JSONObject> jsonObjects = new ArrayList<>();
                        for (int i = 0; i < xrayTestStepList.size(); i++) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("num", i + 1);
                            jsonObject.put("desc", xrayTestStepList.get(i).getStep());
                            jsonObject.put("result", xrayTestStepList.get(i).getResult());
                            jsonObjects.add(jsonObject);
                        }
                        caseWithBLOBs.setSteps(JSON.toJSONString(jsonObjects));
                    }
                    testCaseService.addTestCase(caseWithBLOBs);
                } catch (Exception e) {
                    LogUtil.error("用例保存出错", e);
                }
            });
        }
    }

    private void getNodeIds(List<XrayTreeNode> xrayTreeNodes, List<FolderInfo> folderIds, String projectId, String nodeId, String nodePath) {
        // 需要处理用例模块
        xrayTreeNodes.forEach(element -> {
            FolderInfo folder = new FolderInfo();
            folder.setId(element.getId());
            folder.setName(element.getName());
            // 查询节点下面子节点
            TestCaseNodeExample testCaseNodeExample = new TestCaseNodeExample();
            testCaseNodeExample.createCriteria().andParentIdEqualTo(nodeId).andProjectIdEqualTo(projectId);
            List<TestCaseNode> testCaseNodes = testCaseNodeMapper.selectByExample(testCaseNodeExample);
            // 节点下面是否有相同模块名称
            Optional<TestCaseNode> opt = testCaseNodes.stream().filter(e -> e.getName().equals(element.getName())).findFirst();
            if (opt.isPresent()) {
                folder.setMsNodeId(opt.get().getId());
                folder.setMsParentId(opt.get().getParentId());
                folder.setMsNodePath(nodePath + "/" + element.getName());
            } else {
                String[] paths = nodePath.split("/");
                // 节点名称是否一样
                if (!paths[paths.length - 1].equals(element.getName())) {
                    TestCaseNode testCaseNode = new TestCaseNode();
                    testCaseNode.setProjectId(projectId);
                    testCaseNode.setParentId(nodeId);
                    testCaseNode.setName(element.getName());
                    // 节点深度
                    testCaseNode.setLevel(paths.length);
                    String nId = testCaseNodeService.addNode(testCaseNode);
                    folder.setMsNodeId(nId);
                    folder.setMsParentId(nodeId);
                    folder.setMsNodePath(nodePath + "/" + element.getName());
                } else {
                    folder.setMsNodeId(nodeId);
                    folder.setMsNodePath(nodePath);
                }
            }
            folderIds.add(folder);
            getNodeIds(element.getFolders(), folderIds, projectId, folder.getMsNodeId(), folder.getMsNodePath());
        });
    }


    /**
     * @param projectId
     * @return XrayFolders
     * 封装获取jira用例模块树
     */
    private XrayFolders getXrayTestCaseNode(String projectId) {
        Project project = projectService.getProjectById(projectId);
        if (null == project) {
            throw MSException.getException("project does not exist");
        }
        Workspace workspace = workspaceMapper.selectByPrimaryKey(project.getWorkspaceId());
        // 获取项目对应的jira xray key
        ProjectCaseConfig caseConfig = projectCaseConfigMapper.selectByProjectId(projectId);
        if (!Optional.ofNullable(caseConfig).isPresent()) {
            throw MSException.getException("没有配置远程用例仓库，请联系管理员");
        }
        IssuesRequest issuesRequest = new IssuesRequest();
        issuesRequest.setProjectId(projectId);
        issuesRequest.setOrganizationId(workspace.getOrganizationId());
        this.mdJiraPlatform = new MDJiraPlatform(issuesRequest);
        return this.mdJiraPlatform.getXrayFolders(caseConfig.getCaseProjectKey());
    }

    /**
     * @param xrayFolders
     * @param projectId
     * @return List<TestCaseNodeDTO>
     * jira用例树转换成MS用例树
     */
    private List<TestCaseNodeDTO> transferTestCaseNode(XrayFolders xrayFolders, String projectId) {
        List<TestCaseNodeDTO> caseNodeDTOList = Lists.newArrayList();
        xrayFolders.getFolders().forEach(element -> {
            TestCaseNodeDTO caseNodeDTO = new TestCaseNodeDTO();
            caseNodeDTO.setLevel(1);
            caseNodeDTO.setProjectId(projectId);
            handleTreeNode(element, caseNodeDTO);
            caseNodeDTOList.add(caseNodeDTO);
        });
        return caseNodeDTOList;
    }

    /**
     * @param xrayTreeNode
     * @param caseNodeDTO  递归处理节点
     */
    private void handleTreeNode(XrayTreeNode xrayTreeNode, TestCaseNodeDTO caseNodeDTO) {
        caseNodeDTO.setId(String.valueOf(xrayTreeNode.getId()));
        caseNodeDTO.setName(xrayTreeNode.getName());
        caseNodeDTO.setLabel(xrayTreeNode.getName());
        caseNodeDTO.setCaseNum(xrayTreeNode.getTestCount());
        if (StringUtils.isNotEmpty(caseNodeDTO.getParentId())) {
            caseNodeDTO.setParentId(caseNodeDTO.getParentId() + "/" + xrayTreeNode.getName());
        } else {
            caseNodeDTO.setParentId(xrayTreeNode.getName());
        }
        caseNodeDTO.setCreateTime(new Date().getTime());
        caseNodeDTO.setUpdateTime(new Date().getTime());
        List<TestCaseNodeDTO> caseNodeList = Lists.newArrayList();
        xrayTreeNode.getFolders().forEach(folder -> {
            TestCaseNodeDTO testCaseNode = new TestCaseNodeDTO();
            testCaseNode.setLevel(caseNodeDTO.getLevel() + 1);
            testCaseNode.setProjectId(caseNodeDTO.getProjectId());
            testCaseNode.setParentId(caseNodeDTO.getParentId());
            testCaseNode.setLabel(caseNodeDTO.getName());
            handleTreeNode(folder, testCaseNode);
            caseNodeList.add(testCaseNode);
        });
        caseNodeDTO.setChildren(caseNodeList);
    }
}
