<template>
  <div class="issue-wrapper">
    <el-card v-loading="isLoading">
      <issue-form-header
        :title="title"
        :attrs="headerComps"
        :form="form"
        @onSearch="getIssueList"
      />
      <!-- 父元素必须用el-row包裹，否则页面会卡死 -->
      <el-row>
        <el-col :span="24">
          <el-tabs type="border-card">
            <el-tab-pane label="可视化图表">
              <div v-if="pieOptions.length > 0">
                <el-row>
                  <el-col
                    v-for="(option, index) in pieOptions"
                    :key="index"
                    :span="6"
                  >
                    <ms-chart
                      style="width: 100%"
                      :options="option"
                      :autoresize="true"
                    />
                  </el-col>
                </el-row>
              </div>
              <div v-else class="empty">
                <el-empty description="暂无数据" :image-size="200" />
              </div>
            </el-tab-pane>
            <el-tab-pane label="详细数据">
              <el-table
                row-key="id"
                border
                class="adjust-table"
                :data="tableData"
                :default-sort="{ prop: 'allIssue', order: 'descending' }"
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
                  sortable
                />
                <el-table-column
                  prop="allUnresolvedIssue"
                  label="未解决问题"
                  show-overflow-tooltip
                  sortable
                />
                <el-table-column
                  prop="unresolvedIssuePercent"
                  label="未解决问题率"
                  show-overflow-tooltip
                  sortable
                >
                  <template v-slot:default="scope">
                    {{ scope.row.unresolvedIssuePercent + "%" }}
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
          </el-tabs>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script>
import MsChart from "@/business/components/common/chart/MsChart";
import IssueFormHeader from "../common/IssueFormHeader";
import { deepClone } from "@/common/js/tableUtils";
const templateOption = {
  color: ["#41F73C", "#D43030"],
  title: {
    text: "标题",
    x: "center",
    y: "12%",
  },
  tooltip: {
    trigger: "item",
    formatter: "{b}({c}) {d}%",
  },
  series: [
    {
      name: "各项目Bug统计",
      type: "pie",
      radius: "65%",
      center: ["50%", "60%"],
      data: [],
      // stillShowZeroSum: false,
      itemStyle: {
        emphasis: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: "rgba(0, 0, 0, 0.5)",
        },
      },
      label: {
        normal: {
          position: "inner",
          formatter(params) {
            const { name, value, percent } = params;
            if (value === 0 && name !== "所有问题") return "";
            return `${name}:${value}\n(${percent}%)`;
          },
          textStyle: {
            color: "#fff",
            fontSize: 12,
            align: "center",
          },
        },
      },
    },
  ],
};
export default {
  name: "VarProjectIssue",
  components: { MsChart, IssueFormHeader },
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
      activeIndex: "pie",
    };
  },
  computed: {
    token() {
      return sessionStorage.getItem("codingToken");
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
      const tableData = deepClone(this.tableData).sort(
        (a, b) => b.allIssue - a.allIssue
      );
      tableData.forEach((item) => {
        let option = deepClone(templateOption);
        option.title.text = item.displayName;
        if (item.allIssue !== 0) {
          option.series[0].data.push({
            name: "已解决问题",
            value: item.allIssue - item.allUnresolvedIssue,
          });
          option.series[0].data.push({
            name: "未解决问题",
            value: item.allUnresolvedIssue,
          });
        } else {
          option.series[0].data.push({
            name: "所有问题",
            value: 0,
          });
          option.color = ["#ccc"];
        }
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

.issue-wrapper >>> .el-tabs__content {
  min-height: calc(100vh - 200px);
}
</style>
