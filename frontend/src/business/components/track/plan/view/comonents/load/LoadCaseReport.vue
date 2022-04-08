<template>
  <el-drawer
    class="load-case-report-drawer"
    :visible.sync="drawer"
    direction="ltr"
    size="80%"
    @close="handleClose"
  >
    <load-case-report-view ref="loadCaseReportView" :report-id="reportId" />
  </el-drawer>
</template>

<script>
import LoadCaseReportView from "@/business/components/track/plan/view/comonents/load/LoadCaseReportView";

export default {
  name: "LoadCaseReport",
  components: { LoadCaseReportView },
  props: {
    reportId: String,
  },
  data() {
    return {
      drawer: false,
    };
  },
  methods: {
    handleClose() {
      this.drawer = false;
      this.$emit("refresh");
    },
  },
};
</script>

<style scoped>
.load-case-report-drawer >>> .el-drawer__header {
  margin-bottom: 0;
}
</style>
