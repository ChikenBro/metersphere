<template>
  <common-component title="用例执行情况">
    <div class="char-component">
      <el-row>
        <el-col
          :span="8"
          v-for="(item, index) in executerTestList"
          :key="index"
        >
          <ms-stack-bar-chart
            v-if="isShow"
            :text="item.title"
            :is-show-legend="index === 0"
            @onClick="onTestResultClick"
            :name="$t('test_track.plan_view.test_result')"
            :data="item.executerTestList"
          />
        </el-col>
      </el-row>
    </div>
  </common-component>
</template>

<script>
import CommonComponent from "./CommonComponent";
import MsStackBarChart from "../../../../../../common/components/MsStackBarChart";

export default {
  name: "ExecutiveConditionAdvanceChartComponent",
  components: { MsStackBarChart, CommonComponent },
  data() {
    return {
      dataMap: new Map([
        [
          "Pass",
          {
            name: this.$t("test_track.plan_view.pass"),
            itemStyle: { color: "#67C23A" },
          },
        ],
        [
          "Failure",
          {
            name: this.$t("test_track.plan_view.failure"),
            itemStyle: { color: "#F56C6C" },
          },
        ],
        [
          "Blocking",
          {
            name: this.$t("test_track.plan_view.blocking"),
            itemStyle: { color: "#E6A23C" },
          },
        ],
        [
          "Skip",
          {
            name: this.$t("test_track.plan_view.skip"),
            itemStyle: { color: "#909399" },
          },
        ],
        [
          "Underway",
          {
            name: this.$t("test_track.plan.plan_status_running"),
            itemStyle: { color: "lightskyblue" },
          },
        ],
        [
          "Prepare",
          {
            name: this.$t("test_track.plan.plan_status_prepare"),
            itemStyle: { color: "#DEDE10" },
          },
        ],
      ]),
      executerTestList: [],
      isShow: true,
    };
  },
  props: {
    planId: String,
    source: String,
    executeResult: {
      type: Array,
      default() {
        return [];
      },
    },
  },
  watch: {
    executeResult() {
      this.getCharData();
    },
  },
  created() {
    this.getCharData();
  },
  methods: {
    getCharData() {
      this.getTestResultCharData();
      this.reload();
    },
    getTestResultCharData() {
      if (this.executeResult) {
        this.executeResult.forEach((obj) => {
          obj.executerTestList.forEach((role) => {
            const arr = [];
            role.dataList.forEach((item) => {
              let data = this.copyData(item.status);
              data.value = item.count;
              arr.push(data);
            });
            role.dataList = arr;
          });
        });
      }
      this.executerTestList = this.executeResult;
    },
    copyData(status) {
      if (this.dataMap.get(status)) {
        return JSON.parse(JSON.stringify(this.dataMap.get(status)));
      }
    },
    reload() {
      this.isShow = false;
      this.$nextTick(function () {
        this.isShow = true;
      });
    },
    onvertDataStatus(status) {
      if (status == this.$t("test_track.plan_view.pass")) {
        status = "Pass";
      } else if (status == this.$t("test_track.plan_view.failure")) {
        status = "Failure";
      } else if (status == this.$t("test_track.plan_view.blocking")) {
        status = "Blocking";
      } else if (status == this.$t("test_track.plan.plan_status_prepare")) {
        status = "Prepare";
      } else if (status == this.$t("test_track.plan.plan_status_running")) {
        status = "running";
      }
      return status;
    },
    onTestResultClick(params) {
      let clickType = params["name"];
      clickType = this.onvertDataStatus(clickType);
      this.redirectPage("functional", clickType);
    },
    redirectPage(charType, clickType) {
      if (this.source == "ReportView") {
        this.$router.push({
          name: "planView",
          params: {
            planId: this.planId,
            charType: charType,
            clickType: clickType,
          },
        });
      }
    },
  },
};
</script>

<style scoped>
.echarts {
  margin: 0 auto;
}

.char-component {
  text-align: center;
}
</style>
