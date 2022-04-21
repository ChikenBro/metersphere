<template>
  <div>
    <!--迭代报告-->
    <div v-if="iterationReport">
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
        id="testResultAdvanceChartComponent"
        :execute-result="iterationReport.handledTestResult"
        :source="source"
        :planId="planId"
        v-if="preview.id == 3"
      />
      <executive-condition-advance-chart-component
        id="executiveConditionAdvanceChartComponent"
        :execute-result="iterationReport.handledCaseExecutiveCondition"
        :source="source"
        :planId="planId"
        v-if="preview.id == 4"
      />
      <failure-test-cases-list
        id="failureTestCasesList"
        :failure-test-cases="iterationReport.failureTestCases"
        v-if="preview.id == 5"
      />
      <defect-list-component
        id="defectListComponent"
        :defect-list="iterationReport.issues"
        :is-iteration-report="true"
        v-if="preview.id == 6"
      />
    </div>

    <!--模版-->
    <div v-else>
      <base-info-component :is-report="false" v-if="preview.id == 1" />
      <test-result-component v-if="preview.id == 2" />
      <test-result-advance-chart-component v-if="preview.id == 3" />
      <executive-condition-advance-chart-component v-if="preview.id == 4" />
      <failure-test-cases-list v-if="preview.id == 5" />
      <defect-list-component v-if="preview.id == 6" />
      <rich-text-component :preview="preview" v-if="preview.type != 'system'" />
    </div>
  </div>
</template>

<script>
import BaseInfoComponent from "./NewBaseInfoComponent";
import TestResultComponent from "./TestResultComponent";
import TestResultAdvanceChartComponent from "./NewTestResultAdvanceChartComponent";
import ExecutiveConditionAdvanceChartComponent from "./ExecutiveConditionAdvanceChartComponent";
import FailureTestCasesList from "./component/FailureTestCasesList";
import DefectListComponent from "./DefectListComponent";
import RichTextComponent from "./RichTextComponent";

export default {
  name: "TemplateComponent",
  components: {
    TestResultAdvanceChartComponent,
    DefectListComponent,
    RichTextComponent,
    TestResultComponent,
    BaseInfoComponent,
    ExecutiveConditionAdvanceChartComponent,
    FailureTestCasesList,
  },
  props: {
    preview: {
      type: Object,
      default() {
        return [];
      },
    },
    source: {
      type: String,
      default: "",
    },
    planId: {
      type: String,
      default: "",
    },
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
    iterationReport: {
      type: Object,
      default() {
        return null;
      },
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
          return "testResultAdvanceChartComponent";
        case 4:
          return "executiveConditionAdvanceChartComponent";
        case 5:
          return "failureTestCasesList";
        case 6:
          return "defectListComponent";
        default:
          return "richTextComponent";
      }
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
