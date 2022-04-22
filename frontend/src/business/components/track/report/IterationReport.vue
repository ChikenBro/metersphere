<template>
  <ms-container>
    <ms-main-container> 迭代报告展示区 </ms-main-container>
  </ms-container>
</template>

<script>
import TestPlanReportList from "./components/TestPlanReportList";
import MsContainer from "../../common/components/MsContainer";
import MsMainContainer from "../../common/components/MsMainContainer";

export default {
  name: "IterationReport",
  components: { MsMainContainer, MsContainer, TestPlanReportList },
  data() {
    return {};
  },
  activated() {
    this.refreshTestPlanList();
  },
  mounted() {
    this.refreshTestPlanList();
  },
  watch: {
    $route(to, from) {},
  },
  methods: {
    refreshTestPlanList() {
      this.$refs.testPlanReportList.condition = {};
      this.$refs.testPlanReportList.initTableData();
    },
  },
};
</script>

<style scoped></style>
