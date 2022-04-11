<template>
  <common-component :title="$t('test_track.plan_view.failure_case')">
    <div class="failure-cases-list">
      <el-table
        row-key="id"
        @row-click="goFailureTestCase"
        :data="failureTestCases"
      >
        <el-table-column
          prop="num"
          :label="$t('commons.id')"
          show-overflow-tooltip
        >
        </el-table-column>
        <el-table-column
          prop="name"
          :label="$t('commons.name')"
          show-overflow-tooltip
        >
        </el-table-column>

        <el-table-column
          prop="testPlanName"
          label="所属计划"
          show-overflow-tooltip
        >
        </el-table-column>

        <el-table-column
          prop="module"
          :label="$t('test_track.case.module')"
          show-overflow-tooltip
        >
        </el-table-column>

        <el-table-column
          prop="priority"
          column-key="priority"
          :label="$t('test_track.case.priority')"
        >
          <template v-slot:default="scope">
            <priority-table-item :value="scope.row.priority" ref="priority" />
          </template>
        </el-table-column>

        <el-table-column
          prop="type"
          column-key="type"
          :label="$t('test_track.case.type')"
          show-overflow-tooltip
        >
          <template v-slot:default="scope">
            <type-table-item :value="scope.row.type" />
          </template>
        </el-table-column>

        <el-table-column
          column-key="method"
          prop="testMode"
          :label="$t('test_track.case.method')"
          show-overflow-tooltip
        >
          <template v-slot:default="scope">
            <method-table-item :value="scope.row.testMode" />
          </template>
        </el-table-column>

        <el-table-column
          prop="executor"
          :label="$t('test_track.plan_view.executor')"
        >
        </el-table-column>

        <el-table-column
          column-key="executeResult"
          prop="executeResult"
          :label="$t('test_track.plan_view.execute_result')"
        >
          <template v-slot:default="scope">
            <status-table-item :value="scope.row.executeResult" />
          </template>
        </el-table-column>

        <el-table-column
          prop="updateTime"
          :label="$t('api_test.automation.update_time')"
          show-overflow-tooltip
        >
          <template v-slot:default="scope">
            <span>{{ scope.row.updateTime | timestampFormatDate }}</span>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </common-component>
</template>

<script>
import CommonComponent from "../CommonComponent";
import StatusTableItem from "../../../../../../common/tableItems/planview/StatusTableItem";
import MethodTableItem from "../../../../../../common/tableItems/planview/MethodTableItem";
import TypeTableItem from "../../../../../../common/tableItems/planview/TypeTableItem";
import PriorityTableItem from "../../../../../../common/tableItems/planview/PriorityTableItem";
export default {
  name: "FailureTestCasesList",
  components: {
    PriorityTableItem,
    TypeTableItem,
    MethodTableItem,
    StatusTableItem,
    CommonComponent,
  },
  props: {
    failureTestCases: {
      type: Array,
      default() {
        return [];
      },
    },
  },
  methods: {
    goFailureTestCase(row) {
      this.$emit("openFailureTestCase", row);
    },
  },
};
</script>

<style scoped></style>
