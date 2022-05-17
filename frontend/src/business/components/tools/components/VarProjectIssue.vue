<template>
  <div class="issue-wrapper">
    <el-card>
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
            /> </el-col
        ></el-row>
        <div v-else class="empty">
          <el-empty description="暂无数据" :image-size="200" />
        </div>
      </el-card>
      <el-card>
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
          />
        </el-table>
      </el-card>
    </el-card>
  </div>
</template>

<script>
import MsChart from "@/business/components/common/chart/MsChart";
import IssueFormHeader from "../common/IssueFormHeader";
const templateOption = {
  color: ["#41F73C", "#D43030"],
  title: {
    text: "标题",
    x: "center",
    y: "10%",
  },
  tooltip: {
    trigger: "item",
    formatter: "{b} {d}%",
  },
  // legend: {
  //   orient: "vertical",
  //   left: "left",
  //   data: ["直接访问", "邮件营销", "联盟广告", "视频广告", "搜索引擎"],
  // },

  series: [
    {
      labelLine: false,
      name: "各项目Bug统计",
      type: "pie",
      radius: "45%",
      center: ["50%", "55%"],
      data: [
        {
          name: "已解决问题",
          value: 52,
        },
        {
          name: "未解决问题",
          value: 48,
        },
      ],
      stillShowZeroSum: false,
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
  components: { MsChart, IssueFormHeader },
  data() {
    return {
      tableData: [],
      form: {
        projectName: "",
      },
      title: "各项目Bug统计",
      projectList: [],
      headerComps: [],
      pieOptions: [],
    };
  },
  computed: {
    token() {
      return sessionStorage.getItem("codingToken");
    },
  },
  created() {
    this.getProjectList();
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
    initHeader() {
      const headerComps = [];
      headerComps.push({
        type: "select",
        label: "项目名称:",
        options: this.projectList,
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
              ...item.data,
            };
          });
          this.setPieOption();
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
        url += `&projectName={form.projectName}`;
      }
      return url;
    },
    setPieOption() {
      const pieOptions = [];
      this.tableData.forEach((item) => {
        let option = JSON.parse(JSON.stringify(templateOption));
        option.title = item.projectName;
        option.series.data.push({
          name: "已解决问题",
          value: item.allIssue - item.allUnresolvedIssue,
        });
        option.series.data.push({
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
</style>
