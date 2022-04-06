<template>
  <common-component :title="$t('test_track.plan_view.base_info')">
    <template>
      <el-row type="flex" justify="space-between">
        <el-col :span="8">
          <span>{{ $t("test_track.plan.plan_project") }}：</span>
          <span class="item-value">{{ reportInfo.projectName }}</span>
        </el-col>
        <el-col :span="8">
          <span>所属迭代：</span>
          <span class="item-value">{{ reportInfo.iterationName }}</span>
        </el-col>
        <el-col :span="8">
          <span>实际开始时间：</span>
          <span v-if="!isReport">{{ reportInfo.actualStartTime }}</span>
          <el-date-picker
            @change="planStartTimeChange"
            v-if="isReport"
            size="mini"
            disabled
            type="date"
            :placeholder="$t('commons.select_date')"
            v-model="reportInfo.planStartTime"
          />
        </el-col>
      </el-row>

      <el-row type="flex" justify="space-between" class="select-time">
        <el-col :span="8">
          <span>负责人：</span>
          <span class="item-value">{{ reportInfo.leaderName }}</span>
        </el-col>
        <el-col :span="8">
          <span>计划开始时间：</span>
          <span v-if="!isReport">{{ reportInfo.planStartTime }}</span>
          <el-date-picker
            @change="planStartTimeChange"
            v-if="isReport"
            size="mini"
            disabled
            type="date"
            :placeholder="$t('commons.select_date')"
            v-model="reportInfo.planStartTime"
          />
        </el-col>
        <el-col :span="8">
          <span>实际结束时间：</span>
          <span v-if="!isReport">{{ reportInfo.actualEndTime }}</span>
          <el-date-picker
            @change="planEndTimeChange"
            v-if="isReport"
            size="mini"
            disabled
            type="date"
            :placeholder="$t('commons.select_date')"
            v-model="reportInfo.actualEndTime"
          />
        </el-col>
      </el-row>

      <el-row type="flex">
        <el-col :span="8">
          <span>{{ $t("test_track.plan_view.executor") }}：</span>
          <span v-if="Array.isArray(reportInfo.executorNames)"
            >{{ reportInfo.executorNames.join("、") }}
          </span>
        </el-col>
        <el-col :span="8">
          <span>计划结束时间：</span>
          <span v-if="!isReport">{{ reportInfo.planEndTime }}</span>
          <el-date-picker
            @change="planEndTimeChange"
            v-if="isReport"
            size="mini"
            disabled
            type="date"
            :placeholder="$t('commons.select_date')"
            v-model="reportInfo.planEndTime"
          />
        </el-col>
      </el-row>
    </template>
  </common-component>
</template>

<script>
import CommonComponent from "./CommonComponent";
export default {
  name: "BaseInfoComponent",
  components: { CommonComponent },
  props: {
    reportInfo: {
      type: Object,
      default() {
        return {
          projectName: "中台微服务",
          iterationName: "Ver 1.0.1",
          leaderName: "Harry",
          executorNames: ["Harry", "Jack"],
          planStartTime: "2022-01-10",
          planEndTime: "2022-02-10",
          actualStartTime: "2022-01-10",
          actualEndTime: "2022-02-10",
        };
      },
    },
    isReport: {
      type: Boolean,
      default: true,
    },
  },
  mounted() {
    console.log("reportInfo", this.reportInfo);
  },
  methods: {
    planStartTimeChange(value) {
      if (
        !!this.reportInfo.planEndTime &&
        this.reportInfo.planEndTime - this.reportInfo.planStartTime < 0
      ) {
        this.reportInfo.planStartTime = undefined;
        this.$warning(this.$t("commons.date.data_time_error"));
      }
    },
    planEndTimeChange(value) {
      if (
        !!this.reportInfo.planStartTime &&
        this.reportInfo.planEndTime - this.reportInfo.planStartTime < 0
      ) {
        this.reportInfo.planEndTime = undefined;
        this.$warning(this.$t("commons.date.data_time_error"));
      }
    },
  },
};
</script>

<style scoped>
span {
  margin-right: 5px;
  display: inline-block;
}

.el-col span:first-child {
  font-weight: bold;
  width: 100px;
}

.el-row {
  height: 60px;
}

.select-time span {
  display: inline-block;
}

.el-date-editor {
  width: 150px;
}
</style>
