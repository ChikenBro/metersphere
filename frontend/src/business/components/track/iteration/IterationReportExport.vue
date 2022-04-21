<template>
  <ms-report-export-template :title="title" type="迭代报告">
    <div v-if="iterationReport">
      <div v-for="(item, index) in previews" :key="item.id">
        <new-template-component
          :source="source"
          :is-report-view="true"
          :iteration-report="iterationReport"
          :plan-id="planId"
          :preview="item"
          :index="index"
        />
      </div>
    </div>
  </ms-report-export-template>
</template>

<script>
import MsReportExportTemplate from "@/business/components/common/components/report/MsReportExportTemplate";
import NewTemplateComponent from "@/business/components/track/plan/view/comonents/report/TemplateComponent/NewTemplateComponent";
import MsReportTitle from "@/business/components/common/components/report/MsReportTitle";
export default {
  name: "MsTestCaseReportExport",
  components: { MsReportTitle, NewTemplateComponent, MsReportExportTemplate },
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
