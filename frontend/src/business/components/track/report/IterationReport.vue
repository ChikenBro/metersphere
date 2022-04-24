<template>
  <ms-container>
    <ms-main-container>
      <iteration-report-list ref="IterationReportList" />
    </ms-main-container>
  </ms-container>
</template>

<script>
import IterationReportList from "./components/IterationReportList";
import MsContainer from "../../common/components/MsContainer";
import MsMainContainer from "../../common/components/MsMainContainer";

export default {
  name: "TestPlanReport",
  components: { MsMainContainer, MsContainer, IterationReportList },
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
    refreshIterationList() {
      this.$refs.IterationReportList.condition = {};
      this.$refs.IterationReportList.initTableData();
    },
  },
};
</script>

<style scoped></style>
