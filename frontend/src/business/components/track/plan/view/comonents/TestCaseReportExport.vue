<template>
  <ms-report-export-template
    :title="title"
    :type="$t('report.test_plan_report')"
  >
    <div v-if="iterationReport">
      <div v-for="(item, index) in previews" :key="item.id">
        <template-component
          :source="source"
          :is-report-view="true"
          :iteration-report="iterationReport"
          :plan-id="planId"
          :preview="item"
          :index="index"
        />
      </div>
    </div>
    <div v-else>
      <div v-for="(item, index) in previews" :key="item.id">
        <template-component
          ref="templateComponent"
          :is-report-view="true"
          :metric="metric"
          :preview="item"
          :index="index"
        />
      </div>
    </div>
  </ms-report-export-template>
</template>

<script>
import MsReportExportTemplate from "../../../../common/components/report/MsReportExportTemplate";
import TemplateComponent from "./report/TemplateComponent/TemplateComponent";
import MsReportTitle from "../../../../common/components/report/MsReportTitle";
export default {
  name: "MsTestCaseReportExport",
  components: { MsReportTitle, TemplateComponent, MsReportExportTemplate },
  props: {
    previews: {
      type: Array,
      default() {
        return [];
      },
    },
    title: {
      type: String,
      default: "",
    },
    metric: {
      type: Object,
      default() {
        return null;
      },
    },
    iterationReport: {
      type: Object,
      default() {
        return null;
      },
    },
    isReportView: {
      type: Boolean,
      default: false,
    },
    source: {
      type: String,
      default: "",
    },
    planId: {
      type: String,
      default: "",
    },
  },
};
</script>

<style scoped>
.report-export >>> .template-component {
  width: 99%;
  margin-top: 30px;
  margin-bottom: 30px;
}

.report-export >>> .report-title {
  margin-bottom: 50px;
}
</style>
