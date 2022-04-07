<template>
  <div>
    <el-drawer
      :visible.sync="showDialog"
      :with-header="false"
      :modal-append-to-body="false"
      size="100%"
      ref="drawer"
      v-loading="result.loading"
    >
      <template v-slot:default="scope">
        <el-row type="flex" class="head-bar">
          <el-col :span="12">
            <div class="name-edit">
              <el-button
                plain
                size="mini"
                icon="el-icon-back"
                @click="handleClose"
                >{{ $t("test_track.return") }} </el-button
              >&nbsp;
              <span class="title">{{ report.name }}</span>
            </div>
          </el-col>
          <el-col :span="12" class="head-right">
            <el-button
              v-permission="['PROJECT_TRACK_REPORT:READ+EXPORT']"
              :disabled="!isTestManagerOrTestUser"
              plain
              size="mini"
              @click="handleExport(report.name)"
            >
              {{ $t("test_track.plan_view.export_report") }}
            </el-button>
            <el-button
              :disabled="!isTestManagerOrTestUser"
              plain
              size="mini"
              @click="handleSave"
            >
              {{ $t("commons.save") }}
            </el-button>
            <el-button
              :disabled="!isTestManagerOrTestUser"
              plain
              size="mini"
              @click="handleEdit"
            >
              {{ $t("test_track.plan_view.edit_component") }}
            </el-button>
          </el-col>
        </el-row>

        <div class="container" ref="resume" id="app">
          <el-main>
            <div v-for="(item, index) in previews" :key="item.id">
              <template-component
                :source="source"
                :isReportView="true"
                :metric="metric"
                :iteration-report="iterationReport"
                :planId="planId"
                :preview="item"
                :index="index"
                ref="templateComponent"
              />
            </div>
          </el-main>
        </div>
      </template>
    </el-drawer>
    <ms-test-plan-report-export
      v-if="reportExportVisible"
      id="testCaseReportExport"
      :title="report.name"
      :metric="metric"
      :previews="previews"
    />
    <test-case-report-template-edit
      :metric="metric"
      ref="templateEdit"
      @refresh="getReport"
    />
  </div>
</template>

<script>
import { exportPdf, jsonToMap, mapToJson } from "@/common/js/utils";
import BaseInfoComponent from "@/business/components/track/plan/view/comonents/report/TemplateComponent/BaseInfoComponent";
import TestResultChartComponent from "@/business/components/track/plan/view/comonents/report/TemplateComponent/TestResultChartComponent";
import TestResultComponent from "@/business/components/track/plan/view/comonents/report/TemplateComponent/TestResultComponent";
import RichTextComponent from "@/business/components/track/plan/view/comonents/report/TemplateComponent/RichTextComponent";
import TestCaseReportTemplateEdit from "@/business/components/track/plan/view/comonents/report/TestCaseReportTemplateEdit";
import TemplateComponent from "@/business/components/track/plan/view/comonents/report/TemplateComponent/TemplateComponent";
import html2canvas from "html2canvas";
import MsTestPlanReportExport from "./TestPlanReportExport";

export default {
  name: "TestPlanReportView",
  components: {
    MsTestPlanReportExport,
    TemplateComponent,
    TestCaseReportTemplateEdit,
    RichTextComponent,
    TestResultComponent,
    TestResultChartComponent,
    BaseInfoComponent,
  },
  data() {
    return {
      result: {},
      imgUrl: "",
      showDialog: false,
      previews: [],
      report: {},
      reportId: "",
      source: "ReportView",
      reportComponents: [1, 3, 4],
      metric: {},
      planId: "",
      reportExportVisible: false,
      componentMap: new Map([
        [
          1,
          {
            name: this.$t("test_track.plan_view.base_info"),
            id: 1,
            type: "system",
          },
        ],
        [
          2,
          {
            name: this.$t("test_track.plan_view.test_result"),
            id: 2,
            type: "system",
          },
        ],
        [
          3,
          {
            name: this.$t("test_track.plan_view.result_distribution"),
            id: 3,
            type: "system",
          },
        ],
        [
          4,
          {
            name: this.$t("test_track.plan_view.failure_case"),
            id: 4,
            type: "system",
          },
        ],
        [
          5,
          {
            name: this.$t("test_track.plan_view.defect_list"),
            id: 5,
            type: "system",
          },
        ],
        [
          6,
          {
            name: this.$t("test_track.plan_view.custom_component"),
            id: 6,
            type: "custom",
          },
        ],
      ]),
      isTestManagerOrTestUser: false,
      iterationReport: null,
    };
  },
  mounted() {
    this.isTestManagerOrTestUser = true;
  },
  watch: {
    reportComponents() {
      this.initPreviews();
    },
  },
  methods: {
    listenGoBack() {
      //监听浏览器返回操作，关闭该对话框
      if (window.history && window.history.pushState) {
        history.pushState(null, null, document.URL);
        window.addEventListener("popstate", this.goBack, false);
      }
    },
    goBack() {
      this.handleClose();
    },
    open(reportId) {
      this.reportId = reportId;
      this.getReport();
      this.showDialog = true;
      this.listenGoBack();
    },
    getReport() {
      this.getMetric();
      this.getIterationReport();
      this.initPreviews();
    },
    initPreviews() {
      this.previews = [];
      this.reportComponents.forEach((item) => {
        let preview = this.componentMap.get(item);
        if (preview && preview.type != "custom") {
          this.previews.push(preview);
        } else {
          if (this.report.content.customComponent) {
            let customComponent = this.report.content.customComponent.get(
              item.toString()
            );
            if (customComponent) {
              this.previews.push({
                id: item,
                title: customComponent.title,
                content: customComponent.content,
              });
            }
          }
        }
      });
    },
    handleClose() {
      window.removeEventListener("popstate", this.goBack, false);
      this.$emit("refresh");
      this.showDialog = false;
    },
    getMetric() {
      this.result = this.$get(
        "/test/plan/report/getMetric/" + this.reportId,
        (response) => {
          this.metric = response.data;
          let components = response.data.reportComponents;
          this.planId = response.data.testPlanId;
          this.report.name = response.data.name;
          this.report.startTime = response.data.startTime;
          this.report.endTime = response.data.endTime;
          if (components === null || components === "") {
            this.reportComponents = [1, 3, 4];
          } else {
            this.reportComponents = JSON.parse(components);
          }

          if (!this.metric.failureTestCases) {
            this.metric.failureTestCases = [];
          }
          if (!this.metric.executeResult) {
            this.metric.executeResult = [];
          }
          if (!this.metric.moduleExecuteResult) {
            this.metric.moduleExecuteResult = [];
          }
          /*缺陷列表*/
          if (!this.metric.issues) {
            this.metric.issues = [];
          }

          if (this.report.startTime) {
            this.metric.startTime = new Date(this.report.startTime);
          }
          if (this.report.endTime) {
            this.metric.endTime = new Date(this.report.endTime);
          }
        }
      );
    },
    getIterationReport() {
      const url = `/iteration/report`;
      // this.$post(url, {iterationCode, projectId}, (res) => {})
      let iterationReport = null;
      iterationReport = {
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
                executorName: "AdminAdminAdminAdmin",
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

      iterationReport.handledTestResult = iterationReport.testResult.map(
        (item) => {
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
        }
      );
      iterationReport.handledCaseExecutiveCondition =
        iterationReport.caseExecutiveCondition.map((item) => {
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

      this.iterationReport = iterationReport;
    },
    handleExport(name) {
      this.result.loading = true;
      this.reportExportVisible = true;
      let reset = this.exportReportReset;

      this.$nextTick(function () {
        setTimeout(() => {
          html2canvas(document.getElementById("testCaseReportExport"), {
            scale: 2,
          }).then(function (canvas) {
            exportPdf(name, [canvas]);
            reset();
          });
        }, 1000);
      });
    },
    exportReportReset() {
      this.reportExportVisible = false;
      this.result.loading = false;
    },
    handleSave() {
      let param = {};
      this.buildParam(param);
      this.$get(
        "/test/plan/report/saveTestPlanReport/" + this.planId + "/MANUAL",
        () => {
          this.result = this.$post("/case/report/edit", param, () => {
            this.$success(this.$t("commons.save_success"));
          });
        }
      );
    },
    buildParam(param) {
      let content = {};
      content.components = [];
      this.previews.forEach((item) => {
        content.components.push(item.id);
        if (!this.componentMap.get(item.id)) {
          content.customComponent = new Map();
          content.customComponent.set(item.id, {
            title: item.title,
            content: item.content,
          });
        }
      });
      param.name = this.report.name;
      if (content.customComponent) {
        content.customComponent = mapToJson(content.customComponent);
      }
      param.content = JSON.stringify(content);
      param.id = this.report.id;
      if (this.metric.startTime) {
        param.startTime = this.metric.startTime.getTime();
      }
      if (this.metric.endTime) {
        param.endTime = this.metric.endTime.getTime();
      }
    },
    handleEdit() {
      this.$refs.templateEdit.open(this.reportId, true);
    },
  },
};
</script>
cd

<style scoped>
.el-main {
  height: calc(100vh - 70px);
  width: 100%;
}

.head-bar {
  background: white;
  height: 45px;
  line-height: 45px;
  padding: 0 10px;
  border: 1px solid #ebeef5;
  box-shadow: 0 0 2px 0 rgba(31, 31, 31, 0.15),
    0 1px 2px 0 rgba(31, 31, 31, 0.15);
}

.container {
  height: 100vh;
  background: #f5f5f5;
}

.el-card {
  width: 70%;
  margin: 5px auto;
}

.head-right {
  text-align: right;
}
</style>
