<template>
  <div>
    <el-drawer
      ref="drawer"
      v-loading="result.loading"
      :visible.sync="showDialog"
      :with-header="false"
      :modal-append-to-body="false"
      size="100%"
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
          </el-col>
        </el-row>

        <div id="app" ref="resume" class="container">
          <el-main>
            <div
              v-for="(item, index) in [
                { id: 1 },
                { id: 2 },
                { id: 3 },
                { id: 4 },
                { id: 5 },
              ]"
              :key="item.id"
            >
              <template-component
                :source="source"
                :isReportView="true"
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
    <ms-test-case-report-export
      v-if="reportExportVisible"
      id="iterationReportExport"
      :title="report.name"
      :iteration-report="iterationReport"
      :previews="[{ id: 1 }, { id: 2 }, { id: 3 }, { id: 4 }, { id: 5 }]"
      :source="source"
      :plan-id="planId"
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
import MsTestPlanReportExport from "@/business/components/track/report/components/TestPlanReportExport";
import MsTestCaseReportExport from "@/business/components/track/plan/view/comonents/TestCaseReportExport";

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
    MsTestCaseReportExport,
  },
  data() {
    return {
      iterationReport: {
        testResult: [],
        caseExecutiveCondition: [],
        failureTestCases: [],
        issues: [],
        handledTestResult: [],
        handledCaseExecutiveCondition: [],
      },
      result: {},
      imgUrl: "",
      showDialog: false,
      previews: [],
      report: {
        name: "迭代报告",
      },
      source: "ReportView",
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
      iterationCode: "",
      projectId: "",
    };
  },
  mounted() {
    this.isTestManagerOrTestUser = true;
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
    open(id, projectId) {
      this.iterationCode = id;
      this.projectId = projectId;
      this.getReport();
      this.showDialog = true;
      this.listenGoBack();
    },
    getReport() {
      this.getIterationReport();
    },
    handleClose() {
      window.removeEventListener("popstate", this.goBack, false);
      this.$emit("refresh");
      this.showDialog = false;
    },
    handleExport(name) {
      this.result.loading = true;
      this.reportExportVisible = true;
      let reset = this.exportReportReset;

      this.$nextTick(function () {
        setTimeout(() => {
          html2canvas(document.getElementById("iterationReportExport"), {
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
    getIterationReport() {
      const url = `/iteration/report`;
      const { iterationCode, projectId } = this;
      this.$post(url, { iterationCode, projectId }, (res) => {
        let iterationReport = res?.data || {};
        iterationReport.testResult ??= [];
        iterationReport.caseExecutiveCondition ??= [];
        iterationReport.failureTestCases ??= [];
        iterationReport.issues ??= [];
        const { testResult, caseExecutiveCondition } = iterationReport;
        iterationReport.handledTestResult =
          (testResult &&
            testResult.map((item) => {
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
            })) ||
          [];
        iterationReport.handledCaseExecutiveCondition =
          caseExecutiveCondition &&
          caseExecutiveCondition.map((item) => {
            return {
              title: item.testPlanName,
              executerTestList:
                (item.executorTestList &&
                  item.executorTestList.map((ele) => {
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
                  })) ||
                [],
            };
          });

        this.iterationReport = iterationReport;
      });
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
