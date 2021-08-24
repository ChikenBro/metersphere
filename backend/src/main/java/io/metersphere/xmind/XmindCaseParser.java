package io.metersphere.xmind;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableMap;
import io.metersphere.base.domain.TestCaseWithBLOBs;
import io.metersphere.commons.constants.TestCaseConstants;
import io.metersphere.commons.utils.BeanUtils;
import io.metersphere.commons.utils.LogUtil;
import io.metersphere.excel.domain.ExcelErrData;
import io.metersphere.excel.domain.TestCaseExcelData;
import io.metersphere.excel.utils.FunctionCaseImportEnum;
import io.metersphere.i18n.Translator;
import io.metersphere.track.service.TestCaseService;
import io.metersphere.xmind.parser.XmindParser;
import io.metersphere.xmind.parser.pojo.*;
import io.metersphere.xmind.utils.DetailUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数据转换
 * 1 解析Xmind文件 XmindParser.parseObject
 * 2 解析后的JSON this.parse 转成测试用例
 */
public class XmindCaseParser {

    private TestCaseService testCaseService;
    private String maintainer;
    private String projectId;
    /**
     * 过程校验记录
     */
    private DetailUtil process;
    /**
     * 已存在用例名称
     */
    private Set<String> testCaseNames;
    /**
     * 转换后的案例信息
     */
    private List<TestCaseWithBLOBs> testCases;

    /**
     * 前置用例-> testCases
     */
    private List<PrefixTestCaseTemplate> prefixTestCase = new ArrayList<>();

    /**
     * 一级标题
     */
    private String oneTitle;

    /**
     * 需要更新的用例
     */
    private List<TestCaseWithBLOBs> updateTestCases;

    /**
     * 案例详情重写了hashCode方法去重用
     */
    private List<TestCaseExcelData> compartDatas;
    /**
     * 记录没有用例的目录
     */
    private List<String> nodePaths;

    /**
     * 记录有用例的数据的目录
     */
    private List<TitleSet> caseNode = new ArrayList<>();

    private List<TestCaseWithBLOBs> continueValidatedCase;

    private List<String> errorPath;

    private boolean isUseCustomId;

    private String importType;

    public XmindCaseParser(TestCaseService testCaseService, String userId, String projectId, Set<String> testCaseNames, boolean isUseCustomId, String importType) {
        this.testCaseService = testCaseService;
        this.maintainer = userId;
        this.projectId = projectId;
        this.testCaseNames = testCaseNames;
        testCases = new LinkedList<>();
        updateTestCases = new LinkedList<>();
        compartDatas = new ArrayList<>();
        process = new DetailUtil();
        nodePaths = new ArrayList<>();
        continueValidatedCase = new ArrayList<>();
        errorPath = new ArrayList<>();
        this.isUseCustomId = isUseCustomId;
        this.importType = importType;
    }

    private static final String TC_REGEX = "(?:tc:|tc：|tc)";
    private static final String PC_REGEX = "(?:pc:|pc：)";
    private static final String RC_REGEX = "(?:rc:|rc：)";
    private static final String ID_REGEX = "(?:id:|id：)";
    private static final String TAG_REGEX = "(?:tag:|tag：)";

    public void clear() {
        compartDatas.clear();
        testCases.clear();
        updateTestCases.clear();
        testCaseNames.clear();
        nodePaths.clear();
    }

    public List<TestCaseWithBLOBs> getTestCase() {
        if (StringUtils.equals(this.importType, FunctionCaseImportEnum.Create.name())) {
            return this.testCases;
        } else {
            return new ArrayList<>();
        }
    }

    public List<TestCaseWithBLOBs> getUpdateTestCase() {
        if (StringUtils.equals(this.importType, FunctionCaseImportEnum.Update.name())) {
            return this.updateTestCases;
        } else {
            return new ArrayList<>();
        }
    }

    public List<String> getNodePaths() {
        return this.nodePaths;
    }

    private final Map<String, String> caseTypeMap = ImmutableMap.of("功能测试", "functional", "性能测试", "performance", "接口测试", "api");
    private final List<String> priorityList = Arrays.asList("P0", "P1", "P2", "P3");

    /**
     * 验证模块的合规性
     */
    public void validate() {
        nodePaths.forEach(nodePath -> {
            String[] nodes = nodePath.split("/");
            if (nodes.length > TestCaseConstants.MAX_NODE_DEPTH + 1) {
                process.add(Translator.get("test_case_node_level_tip") +
                        TestCaseConstants.MAX_NODE_DEPTH + Translator.get("test_case_node_level"), nodePath);
            }
            String path = "";
            for (int i = 0; i < nodes.length; i++) {
                if (i != 0 && StringUtils.equals(nodes[i].trim(), "")) {
                    process.add(Translator.get("module_not_null"), path);
                } else if (nodes[i].trim().length() > 100) {
                    process.add(Translator.get("module") + Translator.get("test_track.length_less_than") + "100", path + nodes[i].trim());
                } else {
                    path += nodes[i].trim() + "/";
                }
            }
        });
    }

    /**
     * 验证用例的合规性
     */
    private boolean validate(TestCaseWithBLOBs data) {
        boolean validatePass = true;
        String nodePath = data.getNodePath();
        if (!nodePath.startsWith("/")) {
            nodePath = "/" + nodePath;
        }
        if (nodePath.endsWith("/")) {
            nodePath = nodePath.substring(0, nodePath.length() - 1);
        }
        data.setNodePath(nodePath);


        //用例名称判断
        if (StringUtils.isEmpty(data.getName())) {
            validatePass = false;
            process.add("name" + Translator.get("cannot_be_null"), nodePath + "");
        } else {
            if (data.getName().length() > 200) {
                validatePass = false;
                process.add(Translator.get("test_case") + Translator.get("test_track.length_less_than") + "200", nodePath + data.getName());
            }
        }


        if (!StringUtils.isEmpty(nodePath)) {
            String[] nodes = nodePath.split("/");
            if (nodes.length > TestCaseConstants.MAX_NODE_DEPTH + 1) {
                validatePass = false;
                process.add(Translator.get("test_case_node_level_tip") +
                        TestCaseConstants.MAX_NODE_DEPTH + Translator.get("test_case_node_level"), nodePath);
                if (!errorPath.contains(nodePath)) {
                    errorPath.add(nodePath);
                }
            }
            for (int i = 0; i < nodes.length; i++) {
                if (i != 0 && StringUtils.equals(nodes[i].trim(), "")) {
                    validatePass = false;
                    process.add(Translator.get("test_case") + Translator.get("module_not_null"), nodePath + data.getName());
                    if (!errorPath.contains(nodePath)) {
                        errorPath.add(nodePath);
                    }
                    break;
                } else if (nodes[i].trim().length() > 100) {
                    validatePass = false;
                    process.add(Translator.get("module") + Translator.get("test_track.length_less_than") + "100 ", nodes[i].trim());
                    if (!errorPath.contains(nodePath)) {
                        errorPath.add(nodePath);
                    }
                    break;
                }
            }
        }

        if (StringUtils.equals(data.getType(), TestCaseConstants.Type.Functional.getValue()) && StringUtils.equals(data.getMethod(), TestCaseConstants.Method.Auto.getValue())) {
            validatePass = false;
            process.add(Translator.get("functional_method_tip"), nodePath + data.getName());
        }

//        if (testCaseNames.contains(data.getName())) {
//            TestCaseWithBLOBs bloBs = testCaseService.checkTestCaseExist(data);
//            if (bloBs != null) {
//                // process.add(Translator.get("test_case_already_exists_excel"), nodePath + "/" + data.getName());
//                // 记录需要变更的用例
//                BeanUtils.copyBean(bloBs, data, "id");
//                updateTestCases.add(bloBs);
//                return false;
//            }
//
//        } else {
//            testCaseNames.add(data.getName());
//        }

        // 用例等级和用例性质处理
        if (!priorityList.contains(data.getPriority())) {
            validatePass = false;
            process.add(Translator.get("test_case_priority") + Translator.get("incorrect_format"), nodePath + data.getName());
        }
        if (data.getType() == null) {
            validatePass = false;
            process.add(Translator.get("test_case_type") + Translator.get("incorrect_format"), nodePath + data.getName());
        }

        // 重复用例校验
        TestCaseExcelData compartData = new TestCaseExcelData();
        BeanUtils.copyBean(compartData, data);
        if (compartDatas.contains(compartData)) {
            validatePass = false;
            process.add(Translator.get("test_case_already_exists_excel"), nodePath + "/" + compartData.getName());
        }
        compartDatas.add(compartData);


        //自定义ID判断
        if (StringUtils.isEmpty(data.getCustomNum())) {
            if (StringUtils.equals(this.importType, FunctionCaseImportEnum.Update.name())) {
                validatePass = false;
                process.add(Translator.get("id_required"), nodePath + "/" + compartData.getName());
                return false;
            } else {
                if (isUseCustomId) {
                    validatePass = false;
                    process.add(Translator.get("custom_num_is_not_exist"), nodePath + "/" + compartData.getName());
                    return false;
                }
            }
        }

        //判断更新
        if (StringUtils.equals(this.importType, FunctionCaseImportEnum.Update.name())) {
            String checkResult = null;
            if (isUseCustomId) {
                checkResult = testCaseService.checkCustomIdExist(data.getCustomNum().toString(), projectId);
            } else {
                checkResult = testCaseService.checkIdExist(Integer.parseInt(data.getCustomNum()), projectId);
            }
            if (null != checkResult) {  //该ID在当前项目中存在
                //如果前面所经过的校验都没报错
                if (!isUseCustomId) {
                    data.setNum(Integer.parseInt(data.getCustomNum()));
                    data.setCustomNum(null);
                }
                data.setId(checkResult);
                updateTestCases.add(data);
            } else {
                process.add(Translator.get("custom_num_is_not_exist"), nodePath + "/" + compartData.getName());
                validatePass = false;
            }
        }

        if (validatePass) {
            this.continueValidatedCase.add(data);
        }
        return true;
    }

    /**
     * 递归处理案例数据
     */
    private void recursion(Attached parent, int level, List<Attached> attacheds) {
        for (Attached item : attacheds) {
            if (isAvailable(item.getTitle(), TC_REGEX)) {
                item.setParent(parent);
                this.formatTestCase(item.getTitle(), parent.getPath(), item.getChildren() != null ? item.getChildren().getAttached() : null);
            } else {
                String nodePath = parent.getPath().trim() + "/" + item.getTitle().trim();
                item.setPath(nodePath);
                item.setParent(parent);
                if (item.getChildren() != null && CollectionUtils.isNotEmpty(item.getChildren().getAttached())) {
                    recursion(item, level + 1, item.getChildren().getAttached());
                } else {
                    if (!nodePath.startsWith("/")) {
                        nodePath = "/" + nodePath;
                    }
                    if (nodePath.endsWith("/")) {
                        nodePath = nodePath.substring(0, nodePath.length() - 1);
                    }
                    // 没有用例的路径
                    nodePaths.add(nodePath);
                }
            }
        }
    }

    private boolean isAvailable(String str, String regex) {
        if (StringUtils.isEmpty(str) || StringUtils.isEmpty(regex)) {
            return false;
        }
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher result = pattern.matcher(str);
        return result.find();
    }

    private String replace(String str, String regex) {
        if (StringUtils.isEmpty(str) || StringUtils.isEmpty(regex)) {
            return str;
        }
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher result = pattern.matcher(str);
        str = result.replaceAll("");
        return str;
    }

    /**
     * 获取步骤数据
     */
    private String getSteps(List<Attached> attacheds) {
        JSONArray jsonArray = new JSONArray();
        if (!attacheds.isEmpty()) {
            for (int i = 0; i < attacheds.size(); i++) {
                // 保持插入顺序，判断用例是否有相同的steps
                JSONObject step = new JSONObject(true);
                step.put("num", i + 1);
                step.put("desc", attacheds.get(i).getTitle());
                if (attacheds.get(i) != null && attacheds.get(i).getChildren() != null && attacheds.get(i).getChildren().getAttached() != null) {
                    step.put("result", attacheds.get(i).getChildren().getAttached().get(0).getTitle());
                }
                jsonArray.add(step);
            }
        } else {
            // 保持插入顺序，判断用例是否有相同的steps
            JSONObject step = new JSONObject(true);
            step.put("num", 1);
            step.put("desc", "");
            step.put("result", "");
            jsonArray.add(step);
        }
        return jsonArray.toJSONString();
    }

    /**
     * 格式化一个用例
     */
    private void formatTestCase(String title, String nodePath, List<Attached> attacheds) {
        TestCaseWithBLOBs testCase = new TestCaseWithBLOBs();
        testCase.setProjectId(projectId);
        testCase.setMaintainer(maintainer);
        testCase.setPriority(priorityList.get(0));
        testCase.setMethod("manual");
        testCase.setType("functional");

        String tc = title.replace("：", ":");
        String[] tcArrs = tc.split(":");
        if (tcArrs.length < 1) {
            process.add(Translator.get("test_case_name") + Translator.get("incorrect_format"), title);
            return;
        }
        // 用例名称
        String name = title.replace(tcArrs[0] + "：", "").replace(tcArrs[0] + ":", "");
        testCase.setName(name);
        testCase.setNodePath(nodePath.trim());

        // 用例等级和用例性质处理
        if (tcArrs[0].indexOf("-") != -1) {
            for (String item : tcArrs[0].split("-")) {
                if (isAvailable(item, TC_REGEX)) {
                    continue;
                } else if (item.toUpperCase().startsWith("P")) {
                    testCase.setPriority(item.toUpperCase());
                } else {
                    testCase.setType(caseTypeMap.get(item));
                }
            }
        }

        // 测试步骤处理
        List<Attached> steps = new LinkedList<>();
        StringBuilder rc = new StringBuilder();
        List<String> tags = new LinkedList<>();
        StringBuilder customId = new StringBuilder();
        if (attacheds != null && !attacheds.isEmpty()) {
            attacheds.forEach(item -> {
                if (isAvailable(item.getTitle(), PC_REGEX)) {
                    testCase.setPrerequisite(replace(item.getTitle(), PC_REGEX));
                } else if (isAvailable(item.getTitle(), RC_REGEX)) {
                    rc.append(replace(item.getTitle(), RC_REGEX));
                    rc.append("\n");
                } else if (isAvailable(item.getTitle(), TAG_REGEX)) {
                    tags.add(replace(item.getTitle(), TAG_REGEX));
                } else if (isAvailable(item.getTitle(), ID_REGEX)) {
                    customId.append(replace(item.getTitle(), ID_REGEX));
                } else {
                    steps.add(item);
                }
            });
        }
        testCase.setRemark(rc.toString());
        if (isUseCustomId || StringUtils.equals(importType, FunctionCaseImportEnum.Update.name())) {
            testCase.setCustomNum(customId.toString());
        }

        testCase.setTags(JSON.toJSONString(tags));
        testCase.setSteps(this.getSteps(steps));
        // 校验合规性
        if (validate(testCase)) {
            testCases.add(testCase);
        }

    }

    /**
     * 处理testcase，不返回错误信息
     */
    public List<ExcelErrData<TestCaseExcelData>> realizationTestCase(MultipartFile multipartFile) throws DocumentException, ArchiveException, IOException {
        List<PrefixTestCaseTemplate> testCases = parseTestCase(multipartFile);
        for (PrefixTestCaseTemplate testcase : testCases) {
            formatTestCase(testcase);
        }
        return new ArrayList<>();
    }

    /**
     * 格式化一个用例-重写
     */
    private void formatTestCase(PrefixTestCaseTemplate attached) {
        TestCaseWithBLOBs testCase = new TestCaseWithBLOBs();
        testCase.setNodePath(attached.getModulePath());
        testCase.setProjectId(projectId);
        testCase.setName(attached.getCaseTitle());
        testCase.setMaintainer(maintainer);
        //用例优先级
        int i = Integer.parseInt(attached.getPriority()) - 1;
        testCase.setPriority(priorityList.get(i));
        //前置条件
        testCase.setPrerequisite(attached.getPrecondition());
        testCase.setMethod("manual");
        testCase.setType("functional");
        testCase.setCustomNum(attached.getCustomNum());
        testCase.setSteps(attached.getCaseStep().toString());
        //步骤类型
        testCase.setStepModel("STEP");
        //校验合规性
        if (validate(testCase)) {
            testCases.add(testCase);
        }
    }


    /**
     * 导入思维导图处理
     */
    public List<ExcelErrData<TestCaseExcelData>> parse(MultipartFile multipartFile) {
        try {
            // 获取思维导图内容
            List<JsonRootBean> roots = XmindParser.parseObject(multipartFile);
            for (JsonRootBean root : roots) {
                if (root != null && root.getRootTopic() != null && root.getRootTopic().getChildren() != null) {
                    // 判断是模块还是用例
                    for (Attached item : root.getRootTopic().getChildren().getAttached()) {
                        // 用例
                        if (isAvailable(item.getTitle(), TC_REGEX)) {
                            return process.parse(replace(item.getTitle(), TC_REGEX) + "：" + Translator.get("test_case_create_module_fail"));
                        } else {
                            String nodePath = item.getTitle();
                            item.setPath(nodePath);
                            if (item.getChildren() != null && !item.getChildren().getAttached().isEmpty()) {
                                recursion(item, 1, item.getChildren().getAttached());
                            } else {
                                if (!nodePath.startsWith("/")) {
                                    nodePath = "/" + nodePath;
                                }
                                if (nodePath.endsWith("/")) {
                                    nodePath = nodePath.substring(0, nodePath.length() - 1);
                                }
                                // 没有用例的路径
                                nodePaths.add(nodePath);
                            }
                        }
                    }
                }
            }
            //检查目录合规性
            this.validate();
        } catch (Exception ex) {
            ex.printStackTrace();
            return process.parse(ex.getMessage());
        }
        return process.parse();
    }


    public List<PrefixTestCaseTemplate> parseTestCase(MultipartFile multipartFile) throws DocumentException, ArchiveException, IOException {
        // 获取思维导图内容
        List<JsonRootBean> roots = XmindParser.parseObject(multipartFile);
        JsonRootBean root = roots.get(0);
        //一级标题
        oneTitle = root.getRootTopic().getTitle();
        Children children = root.getRootTopic().getChildren();
//        System.out.println(children);

        // 获取所有用例数据,解析模块
        handleNode(children);
        LogUtil.info("当前测试模块: " + caseNode);

        //解析成用例内容
        parseCase(children, "");
        LogUtil.info("当前测试: " + prefixTestCase);
        return prefixTestCase;
    }

    /**
     * 处理模块
     */
    public void handleNode(Children children) {
        List<TitleSet> titleSets = new ArrayList<>();
        // 拼接上下级为一组，直到用例标题
        List<TitleSet> nodeTitle = parseNode(children, titleSets);
        //获取所有模块节点名称-id唯一，title可能重复 比如path 1/1/2
        Map<String, String> map = new HashMap<>();
        for (TitleSet node : nodeTitle) {
            map.put(node.getFirstId(), node.getFirstTitle());
        }
        //筛选用例数据
        for (TitleSet n : nodeTitle) {
            if (map.containsKey(n.getId())) {
                continue;
            } else {
                caseNode.add(n);
            }
        }

        // 拼接模块
        for (TitleSet node : caseNode) {
            String firstTitle = node.getFirstTitle();
            String firstId = node.getFirstId();
            List<String> path = new ArrayList<>();
            List<String> allNode = splicingPath(firstTitle, firstId, nodeTitle, path);
            //替换用例下的路径
            node.setFirstTitle(handleNodeName(allNode));
        }
    }

    /**
     * 拼接处理节点名称
     */
    public String handleNodeName(List<String> allNode) {
        //数组反转
        Collections.reverse(allNode);
        String nodeName = "";
        for (String node : allNode) {
            nodeName = nodeName + "/" + node;
        }
        //增加一级标题
//        nodeName = oneTitle + nodeName;
        return nodeName;
    }

    /**
     * 获取用例上级所有节点名称
     */
    public List<String> splicingPath(String firstTitle, String firstId, List<TitleSet> nodeTitle, List<String> nodeModules) {
        nodeModules.add(firstTitle);
        while (true) {
            List<String> list = getParentNodeTitle(nodeTitle, firstTitle, firstId);
            String superTitle = list.get(0);
            firstId = list.get(1);
            if (superTitle.equals("")) {
                return nodeModules;
            } else {
                nodeModules.add(superTitle);
                firstTitle = superTitle;
                // 处理无限循环
                if (nodeModules.size() > 15) {
                    return nodeModules;
                }
            }
        }
    }

    /**
     * 获取上级标题：模块节点
     */
    public List<String> getParentNodeTitle(List<TitleSet> nodeTitle, String firstTitle, String firstId) {
        List<String> list = new ArrayList<>();
        for (TitleSet node : nodeTitle) {
            if (firstTitle.equals(node.getTitle()) && firstId.equals(node.getId())) {
                firstTitle = node.getFirstTitle();
                firstId = node.getFirstId();
                list.add(firstTitle);
                list.add(firstId);
                return list;
            }
        }
        list.add("");
        list.add(firstId);
        return list;
    }

    /**
     * 处理模块
     */
    public List<TitleSet> parseNode(Children children, List<TitleSet> titleSets) {
        for (Attached root : children.getAttached()) {
            String firstTitle = root.getTitle();
            String firstId = root.getId();
            // 如果有优先级，忽略后面节点 或者 没有下级
            if (root.getMarkerRefs() != null || root.getChildren() == null) {
                continue;
            }
            // 有前置条件，忽略后面节点
            List<Attached> topic = root.getChildren().getAttached();
            if (topic.get(0).getTitle().contains("前置条件:") || topic.get(0).getTitle().contains("前置条件：")) {
                continue;
            }
            // 如果下级不为空
            if (root.getChildren() != null) {
                List<Attached> datas = root.getChildren().getAttached();
                for (Attached data : datas) {
                    TitleSet titleSet = new TitleSet();
                    String title = data.getTitle();
                    String id = data.getId();
                    titleSet.setFirstTitle(firstTitle);
                    titleSet.setFirstId(firstId);
                    titleSet.setTitle(title);
                    titleSet.setId(id);
                    titleSets.add(titleSet);
                }
                parseNode(root.getChildren(), titleSets);
            }
        }
        return titleSets;
    }

    /**
     * 处理用例
     */
    private void parseCase(Children children, String treeId) {
        for (Attached root : children.getAttached()) {
            // 如果没有下级
            if (root.getChildren() == null || root.getChildren().getAttached() == null) {
                //当前节点有优先级
                if (root.getMarkerRefs() != null) {
                    PrefixTestCaseTemplate caseStep = testPoint(root);
                    prefixTestCase.add(caseStep);
                    continue;
                } else {
                    //当前节点无优先级
                    continue;
                }

            } else {
                //如果勾选用例优先级 或者 下级有前置条件
                String context = root.getChildren().getAttached().get(0).getTitle();
                if (root.getMarkerRefs() != null || context.split("前置条件：").length > 1 || context.split("前置条件:").length > 1) {
                    //如果标记❌,忽略下面所有用例
                    Marker marker = root.getMarkerRefs();
                    if (marker != null && root.getMarkerRefs().getMarkerRef().get(0).markerId.equals("symbol-wrong")) {
                        continue;
                    }
                    //下级有前置条件
                    PrefixTestCaseTemplate caseStep = handleCase(root.getChildren().getAttached(), root.getId(), root.getTitle(), marker);
//                    if (caseStep == null) {
//                        break;
//                    }
//                    System.out.println(caseStep);
                    prefixTestCase.add(caseStep);
                    continue;
                } else {
                    String newTreeId = root.getId();
                    parseCase(root.getChildren(), newTreeId);
                }
            }
        }
    }

    /**
     * 处理只有优先级的用例标题-测试点
     */
    private PrefixTestCaseTemplate testPoint(Attached roots) {
        PrefixTestCaseTemplate testCaseTemplate = new PrefixTestCaseTemplate();
        //只有测试点
        testCaseTemplate.setCaseTitle(roots.getTitle());
        testCaseTemplate.setPrecondition("");
        testCaseTemplate.setTreeId(roots.getId());
        testCaseTemplate.setPriority(handleCasePriority(roots.getMarkerRefs()));
        for (TitleSet node : caseNode) {
            if (roots.getTitle().equals(node.getTitle()) && roots.getId().equals(node.getId())) {
                testCaseTemplate.setModulePath(node.getFirstTitle());
                nodePaths.add(node.getFirstTitle());
                break;
            }
        }
        //空数组
        List<Map> lists = new ArrayList<>();
        testCaseTemplate.setCaseStep(lists);
        return testCaseTemplate;
    }

    /**
     * 处理常规用例
     */
    private PrefixTestCaseTemplate handleCase(List<Attached> roots, String treeId, String superTitle, Marker priority) {
        PrefixTestCaseTemplate testCaseTemplate = getRecondition(roots.get(0));
        //判断第一行是否为前置条件
        if (!testCaseTemplate.getPrecondition().equals("")) {
            roots.remove(0);
        }
//        System.out.println(precondition);
//        testCaseTemplate.setPrecondition(precondition);
        testCaseTemplate.setCaseTitle(superTitle);
        testCaseTemplate.setTreeId(treeId);
        testCaseTemplate.setPriority(handleCasePriority(priority));
        for (TitleSet node : caseNode) {
            if (superTitle.equals(node.getTitle()) && treeId.equals(node.getId())) {
                testCaseTemplate.setModulePath(node.getFirstTitle());
                nodePaths.add(node.getFirstTitle());
                break;
            }
        }
        //步骤结果
        List<Map> lists = new ArrayList<>();
        int num = 1;
        for (Attached root : roots) {
            JSONObject map = new JSONObject(true);
            String step = root.getTitle();
            String expect;
            //如果没有下级-也就是无结果，默认expect为空
            if (root.getChildren() != null) {
                expect = root.getChildren().getAttached().get(0).getTitle();
            } else {
                expect = "";
            }
            map.put("num", num);
            map.put("desc", step);
            map.put("result", expect);
            lists.add(map);
            num++;
        }
        testCaseTemplate.setCaseStep(lists);
        return testCaseTemplate;
    }

    /**
     * 处理用例优先级
     */
    private String handleCasePriority(Marker priority) {
        if (priority == null) {
            return "3";
        }
        String pri = priority.getMarkerRef().get(0).markerId.replace("priority-", "");
        if (pri.length() > 1) {
            return "3";
        }
        return pri;
    }

    /**
     * 获取前置条件和更新id
     */
    private PrefixTestCaseTemplate getRecondition(Attached root) {
        String precondition;
        String customNum;

        PrefixTestCaseTemplate testCaseTemplate = new PrefixTestCaseTemplate();
        //第一列为前置条件
        if (root.getTitle().split("前置条件").length > 1) {
            precondition = root.getTitle().replace("前置条件:", "").replace("前置条件：", "");
            testCaseTemplate.setPrecondition(precondition);
            //前置条件后无更新用例id
            if (root.getChildren() == null) {
                testCaseTemplate.setCustomNum(null);
            } else if (root.getChildren().getAttached().get(0).getTitle().split("id").length > 1) {
                customNum = root.getChildren().getAttached().get(0).getTitle().replace("id:", "").replace("id：", "");
                testCaseTemplate.setCustomNum(customNum);
            } else {
                testCaseTemplate.setCustomNum(null);
            }
            return testCaseTemplate;
        }
        //第一列为更新用例id
        if (root.getTitle().split("id").length > 1) {
            customNum = root.getTitle().replace("id:", "").replace("id：", "");
            testCaseTemplate.setPrecondition("");
            testCaseTemplate.setCustomNum(customNum);
            return testCaseTemplate;
        }
        //第一列无前置条件与更新用例id
        testCaseTemplate.setPrecondition("");
        testCaseTemplate.setCustomNum(null);
        return testCaseTemplate;
    }

    public List<TestCaseWithBLOBs> getContinueValidatedCase() {
        return this.continueValidatedCase;
    }

    public List<String> getValidatedNodePath() {
        List<String> returnPathList = new ArrayList<>(nodePaths);
        if (CollectionUtils.isNotEmpty(returnPathList)) {
            if (CollectionUtils.isNotEmpty(errorPath)) {
                returnPathList.removeAll(errorPath);
            }
        }
        return returnPathList;
    }
}