<template>
  <div class="issue-wrapper">
    <el-card v-loading="isLoading">
      <issue-form-header
        :title="title"
        :attrs="headerComps"
        :form="form"
        @onSearch="getIssueList"
      />
      <el-card style="height: 500px; overflow: auto; margin-bottom: 10px">
        <el-row v-if="pieOptions.length > 0">
          <el-col v-for="(option, index) in pieOptions" :key="index" :span="6">
            <ms-chart
              style="width: 100%"
              :options="option"
              :autoresize="false"
            />
          </el-col>
        </el-row>
        <div v-else class="empty">
          <el-empty description="暂无数据" :image-size="200" />
        </div>
      </el-card>
      <el-card>
        <el-table
          row-key="id"
          border
          class="adjust-table"
          :data="pagedTableData"
          height="500px"
        >
          <el-table-column
            prop="displayName"
            label="项目名称"
            show-overflow-tooltip
          />
          <el-table-column
            prop="allIssue"
            label="所有问题"
            show-overflow-tooltip
          />
          <el-table-column
            prop="allUnresolvedIssue"
            label="未解决问题"
            show-overflow-tooltip
          />
          <el-table-column
            prop="unresolvedIssuePercent"
            label="未解决问题率"
            show-overflow-tooltip
          >
            <template v-slot:default="scope">
              {{ scope.row.unresolvedIssuePercent + "%" }}
            </template>
          </el-table-column>
        </el-table>
        <ms-table-pagination
          :change="() => {}"
          :current-page.sync="page.currentPage"
          :page-size.sync="page.pageSize"
          :total="page.total"
        />
      </el-card>
    </el-card>
  </div>
</template>

<script>
import MsChart from "@/business/components/common/chart/MsChart";
import IssueFormHeader from "../common/IssueFormHeader";
import MsTablePagination from "@/business/components/common/pagination/TablePagination.vue";

const templateOption = {
  color: ["#41F73C", "#D43030"],
  title: {
    text: "标题",
    x: "center",
    y: "10%",
  },
  tooltip: {
    trigger: "item",
    formatter: "{b}({c}) {d}%",
  },
  // legend: {
  //   orient: "vertical",
  //   left: "left",
  //   data: ["直接访问", "邮件营销", "联盟广告", "视频广告", "搜索引擎"],
  // },

  series: [
    {
      name: "各项目Bug统计",
      type: "pie",
      radius: "45%",
      center: ["50%", "55%"],
      data: [],
      // stillShowZeroSum: false,
      itemStyle: {
        emphasis: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: "rgba(0, 0, 0, 0.5)",
        },
      },
    },
  ],
};
export default {
  name: "VarProjectIssue",
  components: { MsChart, IssueFormHeader, MsTablePagination },
  data() {
    return {
      tableData: [],
      form: {
        projectName: "",
      },
      title: "各项目Bug统计",
      headerComps: [],
      pieOptions: [],
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

        this.setPieOption();
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
    initHeader() {
      const headerComps = [];
      headerComps.push({
        type: "select",
        label: "项目名称:",
        prop: "projectName",
      });
      this.headerComps = headerComps;
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
              displayName: item.displayName,
              ...item.data,
            };
          });
          this.page = {
            currentPage: 1,
            pageSize: 10,
            total: this.tableData.length,
          };
          this.$nextTick(() => this.setPieOption());
        } else {
          this.$message.warning(data);
        }
        this.isLoading = false;
      });
    },
    buildUrl() {
      let url = "/trend//issue/total/projectquality/unresolved/list";
      const { form, token } = this;
      url += `?token=${token}`;
      if (form.projectName) {
        url += `&projectName=${form.projectName}`;
      }
      return url;
    },
    setPieOption() {
      const pieOptions = [];
      this.pagedTableData.forEach((item) => {
        let option = JSON.parse(JSON.stringify(templateOption));
        option.title.text = item.projectName;
        option.series[0].data.push({
          name: "已解决问题",
          value: item.allIssue - item.allUnresolvedIssue,
        });
        option.series[0].data.push({
          name: "未解决问题",
          value: item.allUnresolvedIssue,
        });
        pieOptions.push(option);
      });
      this.pieOptions = pieOptions;
    },
  },
};
</script>

<style scoped>
.issue-wrapper {
  width: 100%;
}
.empty {
  margin-top: 100px;
}
.empty >>> .el-empty__image {
  margin-left: 50%;
  transform: translateX(-50%);
}
.empty >>> .el-empty__description {
  text-align: center;
  color: rgba(0, 0, 0, 0.5);
}
.adjust-table >>> .el-table__row {
  height: 46.2px;
}
.adjust-table >>> .el-table__body-wrapper {
  height: calc(100% - 36px) !important;
}
</style>
