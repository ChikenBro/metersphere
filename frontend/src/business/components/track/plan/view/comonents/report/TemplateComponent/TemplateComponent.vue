<template>
  <div>
    <!--模版-->
    <div v-if="!metric">
      <base-info-component v-if="preview.id == 1" :is-report="false" />
      <test-result-component v-if="preview.id == 2" />
      <!--<test-result-chart-component v-if="preview.id == 3"/>-->
      <test-result-advance-chart-component v-if="preview.id == 3" />
      <!--<failure-result-component v-if="preview.id == 4"/>-->
      <failure-result-advance-component v-if="preview.id == 4" />
      <defect-list-component v-if="preview.id == 5" />
      <rich-text-component v-if="preview.type != 'system'" :preview="preview" />
    </div>

    <!--报告-->
    <div v-if="metric">
      <base-info-component
        v-if="preview.id == 1"
        id="baseInfoComponent"
        :report-info="metric"
      />
      <test-result-component
        v-if="preview.id == 2"
        id="testResultComponent"
        :test-results="metric.moduleExecuteResult"
      />
      <!--<test-result-chart-component id="resultChartComponent" :execute-result="metric.executeResult" v-if="preview.id == 3"/>-->
      <test-result-advance-chart-component
        v-if="preview.id == 3"
        id="resultChartComponent"
        :execute-result="metric.executeResult"
        :source="source"
        :plan-id="planId"
      />
      <!--<failure-result-component id="failureResultComponent" :failure-test-cases="metric.failureTestCases" v-if="preview.id == 4"/>-->
      <failure-result-advance-component
        v-if="preview.id == 4"
        id="failureResultComponent"
        :failure-test-cases="metric.failureTestCases"
      />
      <defect-list-component
        v-if="preview.id == 5"
        id="defectListComponent"
        :defect-list="metric.issues"
      />
      <rich-text-component
        v-if="preview.type != 'system'"
        id="richTextComponent"
        :is-report-view="isReportView"
        :preview="preview"
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
