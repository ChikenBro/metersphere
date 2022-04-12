<template>
  <ms-container>
    <ms-main-container>
      <el-card class="table-card">
        <template v-slot:header>
          <ms-table-header
            :condition.sync="page.condition"
            :tip="$t('commons.search_by_name')"
            :show-create="false"
            @search="fetchIterationList"
          >
            <template v-slot:button>
              <ms-table-button
                icon="el-icon-refresh"
                content="同步coding迭代"
                @click="syncIterationCoding"
              />
            </template>
          </ms-table-header>
        </template>

        <ms-table
          ref="table"
          v-loading="page.result.loading"
          :data="page.data"
          :enable-selection="false"
          :condition="page.condition"
          :total="page.total"
          :page-size.sync="page.pageSize"
          :show-select-all="false"
          :screen-height="screenHeight"
          :fields.sync="fields"
          :field-key="tableHeaderKey"
          :operators="operators"
          @handlePageChange="fetchIterationList"
          @refresh="fetchIterationList"
          @handleRowClick="showRequirementList"
        >
          <span v-for="item in fields" :key="item.key">
            <ms-table-column width="1"> </ms-table-column>
            <ms-table-column
              label="迭代名称"
              prop="name"
              :field="item"
              sortable
              :fields-width="fieldsWidth"
            >
            </ms-table-column>
            <ms-table-column
              label="负责人"
              prop="leader"
              :field="item"
              sortable
              :fields-width="fieldsWidth"
            >
            </ms-table-column>
            <ms-table-column
              label="当前状态"
              prop="status"
              :field="item"
              sortable
              :fields-width="fieldsWidth"
            >
              <template v-slot:default="scope">
                <el-tag :type="StatusTagMap[scope.row.status]" size="mini">
                  {{ IterationStatusEnum[scope.row.status] || "-" }}
                </el-tag>
              </template>
            </ms-table-column>
            <ms-table-column
              label="迭代进度"
              prop="schedule"
              :field="item"
              sortable
              :fields-width="fieldsWidth"
            >
              <template v-slot:default="scope">
                <span>{{ (scope.row.schedule / 10000).toFixed(2) + "%" }}</span>
              </template>
            </ms-table-column>
            <ms-table-column
              label="开始时间"
              prop="startTime"
              :field="item"
              sortable
              :fields-width="fieldsWidth"
            >
              <template v-slot:default="scope">
                <span>{{ scope.row.startTime }}</span>
              </template>
            </ms-table-column>
            <ms-table-column
              label="结束时间"
              prop="endTime"
              :field="item"
              sortable
              :fields-width="fieldsWidth"
            >
              <template v-slot:default="scope">
                <span>{{ scope.row.endTime }}</span>
              </template>
            </ms-table-column>
            <ms-table-column
              label="执行次数"
              prop="numberOfExecutions"
              :field="item"
              sortable
              :fields-width="fieldsWidth"
            >
            </ms-table-column>
            <ms-table-column
              label="最后一次通过率"
              prop="lastPassRate"
              :field="item"
              sortable
              :fields-width="fieldsWidth"
            >
              <template v-slot:default="scope">
                <span>{{ (scope.row.schedule / 10000).toFixed(2) + "%" }}</span>
              </template>
            </ms-table-column>
          </span>
        </ms-table>

        <ms-table-pagination
          :change="fetchIterationList"
          :current-page.sync="page.currentPage"
          :page-size.sync="page.pageSize"
          :total="page.total"
        />
      </el-card>
      <iteration-requirements-drawer
        ref="requirementsDrawer"
      ></iteration-requirements-drawer>
      <iteration-report-view
        ref="iterationReportView"
        @refresh="initTableData"
      />
    </ms-main-container>
  </ms-container>
</template>

<script>
import MsContainer from "../../common/components/MsContainer";
import MsMainContainer from "../../common/components/MsMainContainer";
import MsTable from "@/business/components/common/components/table/MsTable";
import MsTableColumn from "@/business/components/common/components/table/MsTableColumn";
import MsTableOperators from "@/business/components/common/components/MsTableOperators";
import MsTableButton from "@/business/components/common/components/MsTableButton";
import MsTablePagination from "@/business/components/common/pagination/TablePagination";
import MsTableHeader from "@/business/components/common/components/MsTableHeader";
import IterationRequirementsDrawer from "./IterationRequirementsDrawer";
import IterationReportView from "./IterationReportView";
import {
  getCustomTableWidth,
  getPageInfo,
  getTableHeaderWithCustomFields,
} from "@/common/js/tableUtils";

import {
  getIterationList,
  syncCoding,
  getIterationRequirements,
} from "@/network/iteration";
import { IterationStatusEnum, StatusTagMap } from "./constance";

const pageKey = "ITERATION_LIST";

export default {
  name: "IterationIndex",
  components: {
    MsContainer,
    MsMainContainer,
    MsTableHeader,
    MsTablePagination,
    MsTableButton,
    MsTableOperators,
    MsTableColumn,
    MsTable,
    IterationRequirementsDrawer,
    IterationReportView,
  },
  data() {
    return {
      page: getPageInfo(),
      fields: [],
      tableHeaderKey: pageKey,
      screenHeight: "calc(100vh - 200px)",
      fieldsWidth: getCustomTableWidth(pageKey),
      iterationTemplate: {},
      IterationStatusEnum,
      StatusTagMap,
      operators: [
        {
          tip: "查看报告",
          icon: "el-icon-s-data",
          exec: this.handleEdit,
        },
      ],
    };
  },
  activated() {
    this.getTableTitleFields();
    this.fetchIterationList();
  },
  methods: {
    fetchIterationList() {
      this.page.result = getIterationList(this.page);
    },
    getTableTitleFields() {
      this.fields = getTableHeaderWithCustomFields(pageKey, []);
      this.$refs.table?.reloadTable();
    },
    syncIterationCoding() {
      syncCoding(() => {
        this.$success("开始同步");
      });
    },
    showRequirementList(row) {
      this.$refs.requirementsDrawer.open(row);
    },
    handleEdit({ code, projectId }) {
      if (code && projectId) {
        this.$refs.iterationReportView.open(code, projectId);
      }
    },
    initTableData() {},
  },
};
</script>

<style scoped></style>
