<template>
  <div class="trend-wrapper">
    <ms-chart style="width: 100%" id="chart" ref="chart" :options="trendOptions" :autoresize="false"></ms-chart>
    <el-table
      row-key="id"
      border
      class="adjust-table"
      :data="tableData"
      height="500px">
      <el-table-column
        prop="jiraKey"
        label="项目名称"
        :formatter="jiraNameFormat"
        show-overflow-tooltip>
      </el-table-column>

      <el-table-column
        prop="issueWeek"
        label="周"
        show-overflow-tooltip>
      </el-table-column>

      <el-table-column
        prop="issueTotal"
        label="本周新增Bug"
        show-overflow-tooltip>
      </el-table-column>

      <el-table-column
        prop="resolutionIssueTotal"
        label="修复Bug"
        show-overflow-tooltip>
      </el-table-column>

      <el-table-column
        prop="resolutionWeekIssue"
        label="修复新增Bug"
        show-overflow-tooltip>
      </el-table-column>

      <el-table-column
        prop="resolutionHistoryIssue"
        label="修复历史Bug"
        show-overflow-tooltip>
      </el-table-column>

    </el-table>
  </div>
</template>

<script>
  import MsChart from "@/business/components/common/chart/MsChart";

  const color = ['#60acfc', '#32d3eb', '#5bc49f', '#feb64d', '#ff7c7c', '#9287e7', '#ca8622', '#bda29a', '#6e7074', '#546570', '#c4ccd3'];
  export default {
    name: "IssueTrend.vue",
    components: {MsChart},
    mounted() {
      this.getIssueTrend();
      this.getIssueList();
    },
    data() {
      return {
        jiraKeyMap: {
          "MDR": "目睹云",
          "MDW": "WeLink",
          "MDL": "目睹直播",
          "MDN": "New企播",
          "MDE": "目睹企播",
        },
        tableData: [],
        trendOptions: {
          color: color,
          grid: {
            // right: '35%' // 动态改这个值
          },
          title: {},
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'cross'
            },
          },
          legend: {
            data: []
          },
          xAxis: {type: 'category', data: []},
          yAxis: [{
            name: '数量',
            type: 'value',
            min: 0
          }],
          series: []
        },
      }
    },
    methods: {
      jiraNameFormat(row,column) {
        return this.jiraKeyMap[row.jiraKey];
      },
      getIssueList() {
        this.$get("/trend/issue/list", response => {
          let data = response.data;
          this.tableData = data;
        })
      },
      getIssueTrend() {
        this.$get("/trend/issue/charts", response => {
          let data = response.data;
          this.setLineOption(data);
        })
      },
      setLineOption(data) {
        let legend = [];
        let series = [];
        legend = data.legend;
        this.trendOptions.xAxis.data = data.xaxis;
        this.trendOptions.legend.data = legend;
        legend.forEach((item, index) => {
          if (data.seriesAddBug[index] !== undefined) {
            let seriesAddBug = {
              name: "",
              data: [],
              type: "line"
            };
            seriesAddBug.name = item + "新增bug";
            seriesAddBug.data = data.seriesAddBug[index][item];
            series.push(seriesAddBug);
          }
        });
        legend.forEach((item, index) => {
          if (data.seriesFixBug[index] !== undefined) {
            let seriesFixBug = {
              name: "",
              data: [],
              type: "line"
            };
            seriesFixBug.name = item + "修复bug";
            seriesFixBug.data = data.seriesFixBug[index][item];
            series.push(seriesFixBug);
          }
        });
        this.trendOptions.series = series;
        return this.trendOptions;
      },
    },
  }
</script>

<style scoped>
  .trend-wrapper {
    width: 100%;
  }

</style>
