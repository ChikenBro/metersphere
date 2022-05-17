<template>
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
      <el-table-column prop="name" label="名称" show-overflow-tooltip />
      <el-table-column prop="issueStatus" label="状态" show-overflow-tooltip />
      <el-table-column prop="dueDate" label="截止时间" show-overflow-tooltip />
      <el-table-column
        prop="startDate"
        label="开始修复时间"
        show-overflow-tooltip
      />
      <el-table-column
        prop="updatedAt"
        label="完成修复时间"
        show-overflow-tooltip
      />
      <el-table-column
        v-if="bugInfo.type === 'test'"
        prop="creator"
        label="测试人员"
        show-overflow-tooltip
      />
      <el-table-column
        prop="assignee"
        label="开发处理人"
        show-overflow-tooltip
      />
      <el-table-column
        prop="projectName"
        label="所属项目"
        show-overflow-tooltip
      />
    </el-table>
  </el-card>
</template>

<script>
import IssueFormHeader from "../common/IssueFormHeader";

const titleEnum = {
  dev: "开发处理超时Bug",
  test: "测试处理超时Bug",
  unplan: "未规划完成时间Bug",
};
export default {
  name: "AbnormalIssueTable",
  components: { IssueFormHeader },
  props: {
    bugInfo: {
      type: Object,
      default() {
        return {
          type: "",
          baseUrl: "",
        };
      },
    },
  },
  data() {
    return {
      title: titleEnum[this.bugInfo.type],
      headerComps: [],
      tableData: [],
      form: {
        projectName: "",
        duation: "",
      },
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
      headerComps.push({
        type: "select",
        label: "项目名称:",
        options: this.projectList,
        prop: "projectName",
      });
      if (this.bugInfo.type !== "unplan") {
        headerComps.push({
          type: "inputNumber",
          label: "超时时长:",
          prop: "duration",
        });
      }
      this.headerComps = headerComps;
    },
    buildUrl() {
      let url = this.bugInfo.baseUrl;
      const { form, token } = this;
      url += `?token=${token}`;
      if (form.projectName) {
        url += `&projectName={form.projectName}`;
      }
      if (form.duration !== "" && this.bugInfo.type !== "unplan") {
        url += `&duration=${form.duration}`;
      }
      return url;
    },
  },
};
</script>

<style scoped></style>
