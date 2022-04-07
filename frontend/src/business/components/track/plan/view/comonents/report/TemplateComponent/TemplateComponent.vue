<template>
  <div>
    <!--模版-->
    <div v-if="!metric">
      <base-info-component :is-report="false" v-if="preview.id == 1" />
      <test-result-component v-if="preview.id == 2" />
      <!--<test-result-chart-component v-if="preview.id == 3"/>-->
      <test-result-advance-chart-component v-if="preview.id == 3" />
      <!--<failure-result-component v-if="preview.id == 4"/>-->
      <failure-result-advance-component v-if="preview.id == 4" />
      <defect-list-component v-if="preview.id == 5" />
      <rich-text-component :preview="preview" v-if="preview.type != 'system'" />
    </div>

    <!--迭代报告-->
    <div v-if="isIterationReport">
      <base-info-component
        id="baseInfoComponent"
        :report-info="iterationReport"
        v-if="preview.id == 1"
      />
      <test-result-component
        id="testResultComponent"
        :test-results="iterationReport.testResult"
        :is-iteration-report="true"
        v-if="preview.id == 2"
      />
      <test-result-advance-chart-component
        id="resultChartComponent"
        :execute-result="testResult"
        :source="source"
        :planId="planId"
        v-if="preview.id == 3"
      />
      <executive-condition-advance-chart-component
        id="executiveConditionChartComponent"
        :execute-result="caseExecutiveCondition"
        :source="source"
        :planId="planId"
        v-if="preview.id == 3"
      />
      <failure-test-cases-list
        id="failureResultComponent"
        :failure-test-cases="iterationReport.failureTestCases"
        v-if="preview.id == 4"
      />
      <defect-list-component
        id="defectListComponent"
        :defect-list="iterationReport.issues"
        :is-iteration-report="true"
        v-if="preview.id == 5"
      />
      <rich-text-component
        id="richTextComponent"
        :is-report-view="isReportView"
        :preview="preview"
        v-if="preview.type != 'system'"
      />
    </div>
    <!--测试报告-->
    <div v-else-if="metric">
      <old-base-info-component
        id="baseInfoComponent"
        :report-info="metric"
        v-if="preview.id == 1"
      />
      <test-result-component
        id="testResultComponent"
        :test-results="metric.moduleExecuteResult"
        v-if="preview.id == 2"
      />
      <!--<test-result-chart-component id="resultChartComponent" :execute-result="metric.executeResult" v-if="preview.id == 3"/>-->
      <old-test-result-advance-chart-component
        id="resultChartComponent"
        :execute-result="metric.executeResult"
        :source="source"
        :planId="planId"
        v-if="preview.id == 3"
      />
      <!--<failure-result-component id="failureResultComponent" :failure-test-cases="metric.failureTestCases" v-if="preview.id == 4"/>-->
      <failure-result-advance-component
        id="failureResultComponent"
        :failure-test-cases="metric.failureTestCases"
        v-if="preview.id == 4"
      />
      <defect-list-component
        id="defectListComponent"
        :defect-list="metric.issues"
        v-if="preview.id == 5"
      />
      <rich-text-component
        id="richTextComponent"
        :is-report-view="isReportView"
        :preview="preview"
        v-if="preview.type != 'system'"
      />
    </div>
  </div>
</template>

<script>
import BaseInfoComponent from "./BaseInfoComponent";
import OldBaseInfoComponent from "./OldBaseInfoComponent";
import FailureTestCasesList from "./component/FailureTestCasesList";
import TestResultComponent from "./TestResultComponent";
import TestResultChartComponent from "./TestResultChartComponent";
import RichTextComponent from "./RichTextComponent";
import FailureResultComponent from "./FailureResultComponent";
import DefectListComponent from "./DefectListComponent";
import html2canvas from "html2canvas";
import TestResultAdvanceChartComponent from "./TestResultAdvanceChartComponent";
import OldTestResultAdvanceChartComponent from "./OldTestResultAdvanceChartComponent";
import FailureResultAdvanceComponent from "./FailureResultAdvanceComponent";
import ExecutiveConditionAdvanceChartComponent from "./ExecutiveConditionAdvanceChartComponent";

export default {
  name: "TemplateComponent",
  components: {
    FailureResultAdvanceComponent,
    TestResultAdvanceChartComponent,
    OldTestResultAdvanceChartComponent,
    FailureResultComponent,
    DefectListComponent,
    RichTextComponent,
    TestResultChartComponent,
    TestResultComponent,
    BaseInfoComponent,
    OldBaseInfoComponent,
    ExecutiveConditionAdvanceChartComponent,
    FailureTestCasesList,
  },
  data() {
    return {
      iterationReport: null,
      testResult: [],
      caseExecutiveCondition: [],
    };
  },
  props: {
    preview: {
      type: Object,
    },
    metric: {
      type: Object,
    },
    source: String,
    planId: String,
    isReport: {
      type: Boolean,
      default: true,
    },
    isReportView: {
      type: Boolean,
      default: true,
    },
    index: {
      type: Number,
      default: 0,
    },
    isIterationReport: {
      type: Boolean,
      default: false,
    },
  },
  created() {
    if (this.isIterationReport) {
      this.getIterationReport();
    }
  },
  watch: {
    planId() {
      if (this.isIterationReport) {
        this.getIterationReport();
      }
    },
  },
  methods: {
    getComponentId() {
      switch (this.preview.id) {
        case 1:
          return "baseInfoComponent";
        case 2:
          return "testResultComponent";
        case 3:
          return "resultChartComponent";
        case 4:
          return "failureResultComponent";
        case 5:
          return "defectListComponent";
        default:
          return "richTextComponent";
      }
    },
    getIterationReport() {
      // const url = `/iteration/report/${this.planId}`;
      const url = `http://yapi.mudutv.com/mock/1451/iteration/report/${this.planId}`;
      // this.$post(url, {}, (res) => {
      this.iterationReport = {
        projectName: "中台微服务",
        iterationName: "Ver 1.0.1",
        leaderName: "Harry",
        executorNames: ["Harry", "Jack"],
        planStartTime: "2022-01-10",
        planEndTime: "2022-02-10",
        actualStartTime: "2022-01-10",
        actualEndTime: "2022-02-10",
        testResult: [
          {
            testPlanId: "1",
            testPlanName: "测试计划",
            moduleName: "模块",
            moduleId: "1",
            caseCount: 2,
            passCount: 2,
            failureCount: 3,
            blockingCount: 4,
            skipCount: 4,
            underwayCount: 6,
            prepareCount: 2,
            passRate: 29,
            issuesCount: 7,
          },
          {
            testPlanId: "2",
            testPlanName: "测试计划22",
            moduleName: "模块22",
            moduleId: "2",
            caseCount: 3,
            passCount: 4,
            failureCount: 5,
            blockingCount: 14,
            skipCount: 3,
            underwayCount: 6,
            prepareCount: 2,
            passRate: 38,
            issuesCount: 7,
          },
        ],
        caseExecutiveCondition: [
          {
            testPlanId: "45b38c46-b305-4626-afc8-eb90b5575d33",
            testPlanName: "11135125",
            executorTestList: [
              {
                executor: "admin",
                executorName: "Admin",
                caseCount: 5,
                passCount: 1,
                failureCount: 1,
                blockingCount: 1,
                skipCount: 1,
                underwayCount: 1,
                prepareCount: 0,
              },
              {
                executor: "0792",
                executorName: "杨格格",
                caseCount: 2,
                passCount: 0,
                failureCount: 0,
                blockingCount: 0,
                skipCount: 0,
                underwayCount: 0,
                prepareCount: 2,
              },
              {
                executor: "0780",
                executorName: "沈磊",
                caseCount: 1,
                passCount: 0,
                failureCount: 0,
                blockingCount: 0,
                skipCount: 0,
                underwayCount: 0,
                prepareCount: 1,
              },
            ],
          },
        ],
        failureTestCases: [
          {
            id: "39bdea0c-6db9-4408-a429-7cced724a465",
            num: null,
            name: "demo",
            testPlanName: "11135125",
            module: "/webSDK",
            priority: "P0",
            type: "functional",
            testMode: "manual",
            executor: "admin",
            executeResult: "Failure",
            updateTime: "2022-04-07 09:51:58",
          },
          {
            id: "8e1e491c-b023-46ce-8353-f3590e964e39",
            num: null,
            name: "会畅深度定制",
            testPlanName: "11135125",
            module: "/定制用户",
            priority: "P0",
            type: "functional",
            testMode: "manual",
            executor: "admin",
            executeResult: "Blocking",
            updateTime: "2022-04-07 09:52:00",
          },
        ],
        issues: [
          {
            num: 1,
            title: "标题",
            description: "描述",
            module: "所属模块",
            status: "功能测试",
            handler: "处理人",
            createTime: "2022-01-20",
          },
        ],
      };

      this.testResult = this.iterationReport.testResult.map((item) => {
        return {
          title: item.testPlanName,
          dataList: [
            { status: "Pass", count: item.passCount },
            { status: "Failure", count: item.failureCount },
            { status: "Blocking", count: item.blockingCount },
            { status: "Skip", count: item.skipCount },
            { status: "Underway", count: item.underwayCount },
            { status: "Prepare", count: item.prepareCount },
          ],
        };
      });
      this.caseExecutiveCondition =
        this.iterationReport.caseExecutiveCondition.map((item) => {
          return {
            title: item.testPlanName,
            executerTestList: item.executorTestList.map((ele) => {
              return {
                executorName: ele.executorName,
                dataList: [
                  { status: "Pass", count: ele.passCount },
                  { status: "Failure", count: ele.failureCount },
                  { status: "Blocking", count: ele.blockingCount },
                  { status: "Skip", count: ele.skipCount },
                  { status: "Underway", count: ele.underwayCount },
                  { status: "Prepare", count: ele.prepareCount },
                ],
              };
            }),
          };
        });
    },
  },
};
</script>

<style scoped>
.el-card {
  margin: 5px auto;
  min-height: 300px;
  width: 80%;
}

.el-card:hover {
  box-shadow: 0 0 2px 2px #409eff;
}
</style>
