<template>
  <el-card v-loading="isLoading">
    <issue-form-header
      :title="title"
      :attrs="headerComps"
      :form="form"
      @onSearch="getIssueList"
    />
    <el-table
      row-key="id"
      border
      class="adjust-table"
      :data="pagedTableData"
      height="500px"
    >
      <el-table-column
        prop="name"
        label="名称"
        show-overflow-tooltip
        width="400"
      />
      <el-table-column
        prop="issueStatus"
        label="状态"
        show-overflow-tooltip
        width="120"
      />
      <el-table-column
        v-if="bugInfo.type !== 'test'"
        prop="dueDate"
        label="截止时间"
        show-overflow-tooltip
      />
      <el-table-column
        v-if="bugInfo.type !== 'test'"
        prop="startDate"
        label="开始修复时间"
        show-overflow-tooltip
      />
      <el-table-column
        v-if="bugInfo.type !== 'unplan'"
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
        prop="displayName"
        label="所属项目"
        show-overflow-tooltip
      />
    </el-table>
    <ms-table-pagination
      :change="() => {}"
      :current-page.sync="page.currentPage"
      :page-size.sync="page.pageSize"
      :total="page.total"
    />
  </el-card>
</template>

<script>
import IssueFormHeader from "../common/IssueFormHeader";
import MsTablePagination from "@/business/components/common/pagination/TablePagination.vue";

const titleEnum = {
  dev: "开发处理超时Bug",
  test: "测试处理超时Bug",
  unplan: "未规划完成时间Bug",
};
export default {
  name: "AbnormalIssueTable",
  components: { IssueFormHeader, MsTablePagination },
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
        duation: "7",
      },
      isLoading: true,
      page: {
        currentPage: 1,
        pageSize: 10,
        total: 0,
      },
      pagedTableData: [],
    };
  },
  computed: {
    token() {
      return sessionStorage.getItem("codingToken");
    },
  },
  watch: {
    page: {
      handler(newPage) {
        const { currentPage, pageSize } = newPage;
        const startIndex = (currentPage - 1) * pageSize;
        const endIndex = currentPage * pageSize;
        this.pagedTableData = this.tableData.slice(startIndex, endIndex);
      },
      deep: true,
      immediate: true,
    },
  },
  created() {
    this.getIssueList();
    this.initHeader();
  },
  methods: {
    getIssueList() {
      this.isLoading = true;
      const url = this.buildUrl();
      this.$get(url, (response) => {
        let data = response.data;
        if (Array.isArray(data)) {
          const tableData = [];
          data.forEach((item) => {
            if (Array.isArray(item?.data)) {
              tableData.push(
                ...item.data.map((obj) => ({
                  ...obj,
                  projectName: item.projectName,
                  displayName: item.displayName,
                }))
              );
            }
          });
          this.tableData = [...tableData];
          this.page = {
            currentPage: 1,
            pageSize: 10,
            total: tableData.length,
          };
        } else {
          this.$message.closeAll();
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
        prop: "projectName",
      });
      if (this.bugInfo.type !== "unplan") {
        headerComps.push({
          type: "inputNumber",
          label: "超时时长:",
          prop: "duation",
        });
      }
      this.headerComps = headerComps;
    },
    buildUrl() {
      let url = this.bugInfo.baseUrl;
      const { form, token } = this;
      url += `?token=${token}`;
      if (form.projectName) {
        url += `&projectName=${form.projectName}`;
      }
      if (form.duation !== "" && this.bugInfo.type !== "unplan") {
        url += `&duation=${form.duation}`;
      }
      return url;
    },
  },
};
</script>

<style scoped>
.adjust-table >>> .el-table__row {
  height: 46.2px;
}
.adjust-table >>> .el-table__body-wrapper {
  height: calc(100% - 36px) !important;
}
</style>
