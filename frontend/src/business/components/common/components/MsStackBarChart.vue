<template>
  <div>
    <ms-chart :options="options" @onClick="onClick"> </ms-chart>
  </div>
</template>

<script>
import MsChart from "@/business/components/common/chart/MsChart";
const color = [
  "#67C23A",
  "#F56C6C",
  "#E6A23C",
  "#909399",
  "lightskyblue",
  "#DEDE10",
];
const _label = {
  normal: {
    show: false,
    position: "inside",
    textStyle: {
      color: "#000",
      fontSize: 10,
    },
  },
};
export default {
  name: "MsStackBarChart",
  components: { MsChart },
  mounted() {
    this.getDataNamesByData();
  },
  watch: {
    data() {
      this.getDataNamesByData();
    },
  },
  data() {
    return {
      options: {
        backgroundColor: "#fff",
        title: {
          text: this.text,
          left: this.isShowLegend ? "50%" : "center",
          top: "2%",
        },
        legend: {
          show: this.isShowLegend,
          data: ["通过", "失败", "阻塞", "跳过", "进行中", "未开始"],
          textStyle: {
            color: "#000",
          },
          orient: "vertical",
          x: "2%",
          y: "10%",
        },
        grid: {
          containLabel: true,
          left: this.isShowLegend ? 100 : 10,
          right: 15,
          bottom: 30,
        },
        tooltip: {
          show: true,
          backgroundColor: "#fff",
          borderColor: "#ddd",
          borderWidth: 1,
          textStyle: {
            color: "#3c3c3c",
            fontSize: 14,
          },
          formatter(p) {
            return "状态：" + p.seriesName + "<br>" + "数量：" + p.value;
          },
          extraCssText: "box-shadow: 0 0 5px rgba(0, 0, 0, 0.1)",
        },
        yAxis: {
          axisLine: {
            show: false,
          },
          axisTick: {
            show: false,
          },
          splitLine: {
            show: false,
          },
        },
        xAxis: {
          data: [],
          axisLabel: {
            fontSize: 14,
            color: "#000",
          },
          axisLine: {
            show: true,
          },
          axisTick: {
            show: false,
          },
          splitLine: {
            show: false,
          },
        },
        series: [
          {
            //新的一个柱子 注意不设stack
            name: "总数",
            type: "bar",
            barWidth: 70,
            barGap: "-100%", // 左移100%，stack不再与上面两个在一列
            label: {
              normal: {
                show: true,
                position: "top",
                textStyle: {
                  color: "#000",
                  fontSize: 13,
                },
              },
            },
            itemStyle: {
              normal: {
                color: "rgba(1, 128, 128, 0)",
              },
            },
            data: [],
          },
          {
            type: "bar",
            name: "通过",
            stack: "2",
            label: {
              normal: {
                show: false,
                position: "inside",
                textStyle: {
                  color: "#000",
                  fontSize: 10,
                },
              },
            },
            legendHoverLink: false,
            barWidth: 70,
            itemStyle: {
              normal: {
                color: color[0],
              },
              emphasis: {
                color: color[0],
              },
            },
            data: [],
          },
          {
            type: "bar",
            name: "失败",
            stack: "2",
            legendHoverLink: false,
            barWidth: 70,
            label: _label,
            itemStyle: {
              normal: {
                color: color[1],
              },
              emphasis: {
                color: color[1],
              },
            },
            data: [],
          },
          {
            type: "bar",
            stack: "2",
            name: "阻塞",
            legendHoverLink: false,
            barWidth: 70,
            label: _label,
            itemStyle: {
              normal: {
                color: color[2],
              },
              emphasis: {
                color: color[2],
              },
            },
            data: [],
          },
          {
            type: "bar",
            stack: "2",
            name: "跳过",
            legendHoverLink: false,
            barWidth: 70,
            label: _label,
            itemStyle: {
              normal: {
                color: color[3],
              },
              emphasis: {
                color: color[3],
              },
            },
            data: [],
          },
          {
            type: "bar",
            stack: "2",
            name: "进行中",
            legendHoverLink: false,
            barWidth: 70,
            label: _label,
            itemStyle: {
              normal: {
                color: color[4],
              },
              emphasis: {
                color: color[4],
              },
            },
            data: [],
          },
          {
            type: "bar",
            stack: "2",
            name: "未开始",
            legendHoverLink: false,
            barWidth: 70,
            label: _label,
            itemStyle: {
              normal: {
                color: color[5],
              },
              emphasis: {
                color: color[5],
              },
            },
            data: [],
          },
        ],
      },
    };
  },
  props: {
    text: {
      type: String,
      default: "图表名称",
    },
    name: {
      type: String,
      default: "数据名称",
    },
    subtext: {
      type: String,
      default: "",
    },
    data: {
      type: Array,
      default() {
        return [
          {
            value: 235,
            name: "示例1",
            itemStyle: {
              color: "#67C23A",
            },
          },
          {
            value: 274,
            name: "示例2",
            itemStyle: {
              color: "#E6A23C",
            },
          },
          {
            value: 274,
            name: "示例3",
            itemStyle: {
              color: "#F56C6C",
            },
          },
        ];
      },
    },
    isShowLegend: {
      type: Boolean,
      default: false,
    },
  },
  methods: {
    getDataNamesByData() {
      let itemNames = [];
      this.data.forEach((role) => {
        itemNames.push(role.executorName);
        role.dataList.forEach((item, index) => {
          this.options.series[index + 1].data.push(item.value);
        });
      });
      for (let i = 0; i < 6; i++) {
        this.options.series[0].data[i] = 0;
        for (let j = 1; j <= 6; j++) {
          this.options.series[0].data[i] += this.options.series[j].data[i];
        }
      }
      this.options.xAxis.data = itemNames;
    },
    onClick(params) {
      this.$emit("onClick", params);
    },
  },
};
</script>

<style scoped>
.echarts {
  margin: 0 auto;
}
</style>
