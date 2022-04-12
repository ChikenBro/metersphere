import i18n from "@/i18n/i18n";

export const TEST_CASE_LIST = "test_case_list";

export const CUSTOM_FIELD_LIST = new Set([
  "name",
  "scene",
  "type",
  "remark",
  "system",
  "createTime",
  "updateTime",
]);

export const ISSUE_TEMPLATE_LIST = new Set([
  "name",
  "platform",
  "description",
  "createTime",
  "updateTime",
]);

export const CUSTOM_TABLE_HEADER = {
  CUSTOM_FIELD: [{
      id: "name",
      key: "1",
      label: i18n.t("commons.name")
    },
    {
      id: "scene",
      key: "2",
      label: i18n.t("custom_field.scene")
    },
    {
      id: "type",
      key: "3",
      label: i18n.t("custom_field.attribute_type")
    },
    {
      id: "remark",
      key: "4",
      label: i18n.t("commons.remark")
    },
    {
      id: "system",
      key: "5",
      label: i18n.t("custom_field.system_field")
    },
    {
      id: "createTime",
      key: "6",
      label: i18n.t("commons.create_time")
    },
    {
      id: "updateTime",
      key: "7",
      label: i18n.t("commons.update_time")
    },
  ],
  //接口定义
  API_DEFINITION: [{
      id: "num",
      key: "1",
      label: "ID"
    },
    {
      id: "name",
      key: "2",
      label: i18n.t("api_test.definition.api_name")
    },
    {
      id: "method",
      key: "3",
      label: i18n.t("api_test.definition.api_type")
    },
    {
      id: "userName",
      key: "4",
      label: i18n.t("api_test.definition.api_principal"),
    },
    {
      id: "path",
      key: "5",
      label: i18n.t("api_test.definition.api_path")
    },
    {
      id: "tags",
      key: "6",
      label: i18n.t("commons.tag")
    },
    {
      id: "updateTime",
      key: "7",
      label: i18n.t("api_test.definition.api_last_time"),
    },
    {
      id: "caseTotal",
      key: "8",
      label: i18n.t("api_test.definition.api_case_number"),
    },
    {
      id: "caseStatus",
      key: "9",
      label: i18n.t("api_test.definition.api_case_status"),
    },
    {
      id: "casePassingRate",
      key: "a",
      label: i18n.t("api_test.definition.api_case_passing_rate"),
    },
    {
      id: "status",
      key: "b",
      label: i18n.t("api_test.definition.api_status")
    },
    {
      id: "createTime",
      key: "c",
      label: i18n.t("commons.create_time")
    },
  ],
  //接口用例
  API_CASE: [{
      id: "num",
      key: "1",
      label: "ID"
    },
    {
      id: "name",
      key: "2",
      label: i18n.t("test_track.case.name")
    },
    {
      id: "priority",
      key: "3",
      label: i18n.t("test_track.case.priority")
    },
    {
      id: "path",
      key: "4",
      label: "API" + i18n.t("api_test.definition.api_path"),
    },
    {
      id: "casePath",
      key: "5",
      label: i18n.t("api_test.definition.request.case") +
        i18n.t("api_test.definition.api_path"),
    },
    {
      id: "tags",
      key: "6",
      label: i18n.t("commons.tag")
    },
    {
      id: "createUser",
      key: "7",
      label: "创建人"
    },
    {
      id: "updateTime",
      key: "8",
      label: i18n.t("api_test.definition.api_last_time"),
    },
    {
      id: "createTime",
      key: "9",
      label: i18n.t("commons.create_time")
    },
  ],
  //场景测试
  API_SCENARIO: [{
      id: "num",
      key: "1",
      label: "ID"
    },
    {
      id: "name",
      key: "2",
      label: i18n.t("api_report.scenario_name")
    },
    {
      id: "level",
      key: "3",
      label: i18n.t("api_test.automation.case_level")
    },
    {
      id: "status",
      key: "4",
      label: i18n.t("test_track.plan.plan_status")
    },
    {
      id: "tags",
      key: "5",
      label: i18n.t("commons.tag")
    },
    {
      id: "userName",
      key: "6",
      label: i18n.t("api_test.automation.creator")
    },
    {
      id: "principalName",
      key: "7",
      label: i18n.t("api_test.definition.api_principal"),
    },
    {
      id: "updateTime",
      key: "8",
      label: i18n.t("api_test.definition.api_last_time"),
    },
    {
      id: "stepTotal",
      key: "9",
      label: i18n.t("api_test.automation.step")
    },
    {
      id: "lastResult",
      key: "a",
      label: i18n.t("api_test.automation.last_result"),
    },
    {
      id: "passRate",
      key: "b",
      label: i18n.t("api_test.automation.passing_rate"),
    },
    {
      id: "createTime",
      key: "c",
      label: i18n.t("commons.create_time")
    },
  ],
  //用例评审
  TEST_CASE_REVIEW: [{
      id: "name",
      key: "1",
      label: i18n.t("test_track.review.review_name")
    },
    {
      id: "reviewer",
      key: "2",
      label: i18n.t("test_track.review.reviewer")
    },
    {
      id: "projectName",
      key: "3",
      label: i18n.t("test_track.review.review_project"),
    },
    {
      id: "creatorName",
      key: "4",
      label: i18n.t("test_track.review.review_creator"),
    },
    {
      id: "status",
      key: "5",
      label: i18n.t("test_track.review.review_status"),
    },
    {
      id: "createTime",
      key: "6",
      label: i18n.t("commons.create_time")
    },
    {
      id: "endTime",
      key: "7",
      label: i18n.t("test_track.review.end_time")
    },
    {
      id: "tags",
      key: "8",
      label: i18n.t("commons.tag")
    },
  ],
  //用例评审-功能用例
  TEST_CASE_REVIEW_FUNCTION_TEST_CASE: [{
      id: "num",
      key: "1",
      label: i18n.t("commons.id")
    },
    {
      id: "name",
      key: "2",
      label: i18n.t("commons.name")
    },
    {
      id: "priority",
      key: "3",
      label: i18n.t("test_track.case.priority")
    },
    {
      id: "type",
      key: "4",
      label: i18n.t("test_track.case.type")
    },
    {
      id: "nodePath",
      key: "5",
      label: i18n.t("test_track.case.module")
    },
    {
      id: "projectName",
      key: "6",
      label: i18n.t("test_track.review.review_project"),
    },
    {
      id: "reviewerName",
      key: "7",
      label: i18n.t("test_track.review.reviewer"),
    },
    {
      id: "reviewStatus",
      key: "8",
      label: i18n.t("test_track.case.status")
    },
    {
      id: "updateTime",
      key: "9",
      label: i18n.t("commons.update_time")
    },
    {
      id: "maintainer",
      key: "a",
      label: i18n.t("custom_field.case_maintainer"),
    },
  ],
  //测试计划
  TEST_PLAN_LIST: [{
      id: "name",
      key: "1",
      label: i18n.t("commons.name")
    },
    {
      id: "userName",
      key: "2",
      label: i18n.t("test_track.plan.plan_principal"),
    },
    {
      id: "status",
      key: "3",
      label: i18n.t("test_track.plan.plan_status")
    },
    {
      id: "stage",
      key: "4",
      label: i18n.t("test_track.plan.plan_stage")
    },
    {
      id: "testRate",
      key: "5",
      label: i18n.t("test_track.home.test_rate")
    },
    {
      id: "projectName",
      key: "6",
      label: i18n.t("test_track.plan.plan_project"),
    },
    {
      id: "plannedStartTime",
      key: "7",
      label: i18n.t("test_track.plan.planned_start_time"),
    },
    {
      id: "plannedEndTime",
      key: "8",
      label: i18n.t("test_track.plan.planned_end_time"),
    },
    {
      id: "actualStartTime",
      key: "9",
      label: i18n.t("test_track.plan.actual_start_time"),
    },
    {
      id: "actualEndTime",
      key: "a",
      label: i18n.t("test_track.plan.actual_end_time"),
    },
    {
      id: "tags",
      key: "b",
      label: i18n.t("commons.tag")
    },
    {
      id: "executionTimes",
      key: "c",
      label: i18n.t("commons.execution_times"),
    },
    {
      id: "passRate",
      key: "d",
      label: i18n.t("commons.pass_rate")
    },
    {
      id: "createUser",
      key: "e",
      label: i18n.t("commons.create_user")
    },
  ],
  //测试计划-功能用例
  TEST_PLAN_FUNCTION_TEST_CASE: [{
      id: "num",
      key: "1",
      label: i18n.t("commons.id")
    },
    {
      id: "name",
      key: "2",
      label: i18n.t("commons.name")
    },
    {
      id: "tags",
      key: "3",
      label: i18n.t("commons.tag")
    },
    {
      id: "nodePath",
      key: "4",
      label: i18n.t("test_track.case.module")
    },
    {
      id: "projectName",
      key: "5",
      label: i18n.t("test_track.review.review_project"),
    },
    {
      id: "issuesContent",
      key: "6",
      label: i18n.t("test_track.issue.issue")
    },
    {
      id: "executor",
      key: "7",
      label: i18n.t("test_track.plan_view.executor"),
    },
    {
      id: "status",
      key: "8",
      label: i18n.t("test_track.plan_view.execute_result"),
    },
    {
      id: "updateTime",
      key: "9",
      label: i18n.t("commons.update_time")
    },
    {
      id: "createTime",
      key: "a",
      label: i18n.t("commons.create_time")
    },
  ],
  //测试计划-api用例
  TEST_PLAN_API_CASE: [{
      id: "num",
      key: "1",
      label: i18n.t("commons.id")
    },
    {
      id: "name",
      key: "2",
      label: i18n.t("api_test.definition.api_name")
    },
    {
      id: "priority",
      key: "3",
      label: i18n.t("test_track.case.priority")
    },
    {
      id: "path",
      key: "4",
      label: i18n.t("api_test.definition.api_path")
    },
    {
      id: "createUser",
      key: "5",
      label: i18n.t("api_test.creator")
    },
    {
      id: "custom",
      key: "6",
      label: i18n.t("api_test.definition.api_last_time"),
    },
    {
      id: "tags",
      key: "7",
      label: i18n.t("commons.tag")
    },
    {
      id: "execResult",
      key: "8",
      label: "执行状态"
    },
    {
      id: "maintainer",
      key: "9",
      label: i18n.t("api_test.definition.request.responsible"),
    },
    {
      id: "updateTime",
      key: "a",
      label: i18n.t("api_test.automation.update_time"),
    },
    {
      id: "createTime",
      key: "b",
      label: i18n.t("commons.create_time")
    },
  ],
  //测试计划-性能用例
  TEST_PLAN_LOAD_CASE: [{
      id: "num",
      key: "1",
      label: i18n.t("commons.id")
    },
    {
      id: "caseName",
      key: "2",
      label: i18n.t("commons.name")
    },
    {
      id: "projectName",
      key: "3",
      label: i18n.t("load_test.project_name")
    },
    {
      id: "userName",
      key: "4",
      label: i18n.t("load_test.user_name")
    },
    {
      id: "createTime",
      key: "5",
      label: i18n.t("commons.create_time")
    },
    {
      id: "status",
      key: "6",
      label: i18n.t("commons.status")
    },
    {
      id: "caseStatus",
      key: "7",
      label: i18n.t("test_track.plan.load_case.execution_status"),
    },
    {
      id: "loadReportId",
      key: "8",
      label: i18n.t("test_track.plan.load_case.report"),
    },
  ],
  //测试计划-场景用例
  TEST_PLAN_SCENARIO_CASE: [{
      id: "num",
      key: "1",
      label: i18n.t("commons.id")
    },
    {
      id: "name",
      key: "2",
      label: i18n.t("api_test.automation.scenario_name"),
    },
    {
      id: "level",
      key: "3",
      label: i18n.t("api_test.automation.case_level")
    },
    {
      id: "tagNames",
      key: "4",
      label: i18n.t("api_test.automation.tag")
    },
    {
      id: "stepTotal",
      key: "7",
      label: i18n.t("api_test.automation.success")
    },
    {
      id: "lastResult",
      key: "8",
      label: i18n.t("api_test.automation.fail")
    },
    {
      id: "passRate",
      key: "9",
      label: i18n.t("api_test.automation.passing_rate"),
    },
    {
      id: "maintainer",
      key: "a",
      label: i18n.t("api_test.definition.request.responsible"),
    },
    {
      id: "createUser",
      key: "5",
      label: i18n.t("api_test.automation.creator"),
    },
    {
      id: "updateTime",
      key: "6",
      label: i18n.t("api_test.automation.update_time"),
    },
    {
      id: "createTime",
      key: "b",
      label: i18n.t("commons.create_time")
    },
  ],
  //测试用例
  TRACK_TEST_CASE: [{
      id: "num",
      key: "1",
      label: i18n.t("commons.id")
    },
    {
      id: "name",
      key: "2",
      label: i18n.t("commons.name")
    },
    {
      id: "reviewStatus",
      key: "3",
      label: i18n.t("test_track.case.status")
    },
    {
      id: "tags",
      key: "4",
      label: i18n.t("commons.tag")
    },
    {
      id: "nodePath",
      key: "5",
      label: i18n.t("test_track.case.module")
    },
    {
      id: "updateTime",
      key: "6",
      label: i18n.t("commons.update_time")
    },
    {
      id: "createUser",
      key: "7",
      label: i18n.t("commons.create_user")
    },
    {
      id: "createTime",
      key: "8",
      label: i18n.t("commons.create_time")
    },
  ],
  //缺陷列表
  ISSUE_LIST: [{
      id: 'num',
      key: '1',
      label: i18n.t('test_track.issue.id')
    },
    {
      id: 'title',
      key: '2',
      label: i18n.t('test_track.issue.title')
    },
    // {id: 'status', key: '3', label: i18n.t('test_track.issue.status')},
    {
      id: 'platformStatus',
      key: '3',
      label: i18n.t('test_track.issue.platform_status')
    },
    {
      id: 'platform',
      key: '4',
      label: i18n.t('test_track.issue.platform')
    },
    {
      id: 'assigneeName',
      key: '5',
      label: i18n.t('test_track.issue.handler')
    },
    {
      id: 'description',
      key: '6',
      label: i18n.t('test_track.issue.description')
    },
    {
      id: 'caseCount',
      key: '7',
      label: i18n.t('test_track.issue.case_number')
    },
    {
      id: 'createTime',
      key: '8',
      label: i18n.t('test_track.issue.create_time')
    },
    {
      id: 'defectTypeName',
      key: '9',
      label: i18n.t('test_track.issue.defect_type')
    },
    {
      id: 'priority',
      key: 'a',
      label: i18n.t('test_track.issue.priority')
    },
    {
      id: 'workingHours',
      key: 'b',
      label: i18n.t('test_track.issue.working_hours')
    },
    {
      id: 'iterationName',
      key: 'c',
      label: i18n.t('test_track.issue.iteration')
    },
    {
      id: 'recordedHours',
      key: 'd',
      label: i18n.t('test_track.issue.working_hours_log')
    },
    {
      id: 'watcherName',
      key: 'e',
      label: i18n.t('test_track.issue.followers')
    },
    {
      id: 'startDate',
      key: 'f',
      label: i18n.t('test_track.issue.start_date')
    },
    {
      id: 'dueDate',
      key: 'g',
      label: i18n.t('test_track.issue.due_date')
    },
    {
      id: 'creatorName',
      key: 'h',
      label: i18n.t('test_track.issue.creator')
    },
    {
      id: 'requirementName',
      key: 'i',
      label: i18n.t('test_track.issue.related_requirements')
    },
    {
      id: 'model',
      key: 'j',
      label: i18n.t('test_track.issue.module')
    },
  ],
  ITERATION_LIST: [{
      id: "name",
      key: "1",
      label: "迭代名称"
    },
    {
      id: "leader",
      key: "2",
      label: "负责人"
    },
    {
      id: "status",
      key: "3",
      label: "当前状态"
    },
    {
      id: "schedule",
      key: "4",
      label: "迭代进度"
    },
    {
      id: "startTime",
      key: "5",
      label: "开始时间"
    },
    {
      id: "endTime",
      key: "6",
      label: "结束时间"
    },
    {
      id: "numberOfExecutions",
      key: "7",
      label: "执行次数"
    },
    {
      id: "lastPassRate",
      key: "8",
      label: "最后一次通过率"
    },
  ],

}
