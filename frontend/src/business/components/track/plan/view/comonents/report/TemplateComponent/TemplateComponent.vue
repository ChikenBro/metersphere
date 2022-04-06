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

    <!--报告-->
    <div v-if="metric">
      <base-info-component
        id="baseInfoComponent"
        :report-info="iterationReport || metric"
        v-if="preview.id == 1"
      />
      <test-result-component
        id="testResultComponent"
        :test-results="iterationReport.testResult || metric.moduleExecuteResult"
        v-if="preview.id == 2"
      />
      <!--<test-result-chart-component id="resultChartComponent" :execute-result="metric.executeResult" v-if="preview.id == 3"/>-->
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
      <!--<failure-result-component id="failureResultComponent" :failure-test-cases="metric.failureTestCases" v-if="preview.id == 4"/>-->
      <failure-result-advance-component
        id="failureResultComponent"
        :failure-test-cases="failureTestCases || metric.failureTestCases"
        v-if="preview.id == 4"
      />
      <defect-list-component
        id="defectListComponent"
        :defect-list="iterationReport.issues || metric.issues"
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
import TestResultComponent from "./TestResultComponent";
import TestResultChartComponent from "./TestResultChartComponent";
import RichTextComponent from "./RichTextComponent";
import FailureResultComponent from "./FailureResultComponent";
import DefectListComponent from "./DefectListComponent";
import html2canvas from "html2canvas";
import TestResultAdvanceChartComponent from "./TestResultAdvanceChartComponent";
import FailureResultAdvanceComponent from "./FailureResultAdvanceComponent";
import ExecutiveConditionAdvanceChartComponent from "./ExecutiveConditionAdvanceChartComponent";

export default {
  name: "TemplateComponent",
  components: {
    FailureResultAdvanceComponent,
    TestResultAdvanceChartComponent,
    FailureResultComponent,
    DefectListComponent,
    RichTextComponent,
    TestResultChartComponent,
    TestResultComponent,
    BaseInfoComponent,
    ExecutiveConditionAdvanceChartComponent,
  },
  data() {
    return {
      iterationReport: null,
      testResult: [],
      caseExecutiveCondition: [],
      failureTestCases: {
        functionalTestCases: [],
        apiTestCases: [],
        scenarioTestCases: [],
        loadTestCases: [],
      },
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
  },
  created() {
    this.getIterationReport();
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
            testPlanId: "1",
            testPlanName: "测试用例",
            executerTestList: [
              {
                executor: "1",
                executorName: "zerf",
                caseCount: 2,
                passCount: 2,
                failureCount: 3,
                blockingCount: 4,
                skipCount: 4,
                underwayCount: 6,
                prepareCount: 2,
              },
              {
                executor: "2",
                executorName: "coder",
                caseCount: 2,
                passCount: 3,
                failureCount: 4,
                blockingCount: 1,
                skipCount: 4,
                underwayCount: 3,
                prepareCount: 2,
              },
            ],
          },
          {
            testPlanId: "2",
            testPlanName: "测试用例2",
            executerTestList: [
              {
                executor: "3",
                executorName: "zzzzzrf",
                caseCount: 2,
                passCount: 3,
                failureCount: 4,
                blockingCount: 4,
                skipCount: 4,
                underwayCount: 1,
                prepareCount: 2,
              },
              {
                executor: "3",
                executorName: "coding",
                caseCount: 2,
                passCount: 3,
                failureCount: 3,
                blockingCount: 1,
                skipCount: 1,
                underwayCount: 6,
                prepareCount: 2,
              },
            ],
          },
        ],
        failureTestCases: [
          {
            id: "1",
            num: "1",
            name: "失败名称",
            testPlanName: "所属计划",
            module: "所属模块",
            priority: "P0",
            type: "api",
            testMode: "manual",
            executor: "zrf",
            executeResult: "Pass",
            updateTime: "2022-02-01",
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
            executerTestList: item.executerTestList.map((ele) => {
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
      this.failureTestCases.functionalTestCases =
        this.iterationReport.failureTestCases;
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
