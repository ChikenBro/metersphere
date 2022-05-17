<template>
  <div class="issues-wrapper">
    <el-card>
      <issue-form-header
        :title="title"
        :attrs="headerComps"
        :form="form"
        @onSearch="getIssueList"
      />
      <el-table
        v-loading="isLoading"
        row-key="id"
        border
        class="adjust-table"
        :data="tableData"
        height="500px"
      >
        <el-table-column
          prop="projectName"
          label="项目名称"
          show-overflow-tooltip
        />
        <el-table-column
          prop="weekResolvedIssue"
          label="本周解决的问题"
          show-overflow-tooltip
        />
        <el-table-column
          prop="weekResolvedHistoryIssue"
          label="本周解决的历史问题"
          show-overflow-tooltip
        />
        <el-table-column
          prop="allUnresolvedIssue"
          label="所有解决的问题"
          show-overflow-tooltip
        />
        <el-table-column
          prop="newCreateIssue"
          label="本周新增的问题"
          show-overflow-tooltip
        />
        <el-table-column
          prop="weekResolvedWeekIssue"
          label="本周解决本周的问题"
          show-overflow-tooltip
        />
      </el-table>
    </el-card>
  </div>
</template>

<script>
import MsChart from "@/business/components/common/chart/MsChart";
import IssueFormHeader from "../common/IssueFormHeader";
export default {
  name: "WeeklyIssue",
  components: { MsChart, IssueFormHeader },
  data() {
    return {
      tableData: [],
      projectList: [],
      form: {
        projectName: "",
        dateRange: [],
      },
      title: "本周Bug统计",
      headerComps: [],
      isLoading: true,
    };
  },
  computed: {
    token() {
      return sessionStorage.getItem("codingToken");
    },
  },
  created() {
    this.getProjectList();
    this.getIssueList();
  },
  methods: {
    getProjectList() {
      this.$get(
        `/trend/issue/total/getAllProject/?token=${this.token}`,
        (response) => {
          const data = response.data;
          if (Array.isArray(data)) {
            this.projectList = data.map((item) => {
              return {
                label: item.displayName,
                value: item.projectName,
                ...item,
              };
            });
          }
          this.initHeader();
        }
      );
    },
    getIssueList() {
      this.isLoading = true;
      const url = this.buildUrl();
      this.$get(url, (response) => {
        let data = response.data;
        if (Array.isArray(data)) {
          this.tableData = data.map((item) => {
            return {
              projectName: item.projectName,
              ...item.data,
            };
          });
        } else {
          this.$message.warning(data);
        }
        this.isLoading = false;
      });
    },
    initHeader() {
      const headerComps = [];
      headerComps.push(
        {
          type: "select",
          label: "项目名称:",
          options: this.projectList,
          prop: "projectName",
        },
        {
          type: "dateRange",
          label: "起止时间:",
          prop: "dateRange",
        }
      );
      this.headerComps = headerComps;
    },
    buildUrl() {
      let url = "/trend/issue/total/projectquality/resolved/list";
      const { form, token } = this;
      url += `?token=${token}`;
      if (form.projectName) {
        url += `&projectName={form.projectName}`;
      }
      if (form.dateRange.length > 0) {
        url += `&startDate=${form.dateRange[0]}&endDate=${form.dateRange[1]}`;
      }
      return url;
    },
  },
};
</script>

<style scoped>
.issues-wrapper {
  width: 100%;
  padding-top: 10px;
}
</style>
