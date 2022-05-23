<template>
  <el-drawer
    ref="drawer"
    :before-close="handleClose"
    :visible.sync="visible"
    size="100%"
    :modal="false"
    title="需求列表"
  >
    <template v-slot:default="">
      <el-main class="container" :style="'height: calc(100vh - 75px)'">
        <el-scrollbar>
          <el-table
            v-loading="isLoading"
            :data="tableData"
            row-key="id"
            border
            :tree-props="{
              children: 'subRequirements',
              hasChildren: 'hasChildren',
            }"
            @filter-change="onFilterChange"
            @sort-change="onSortChange"
          >
            <el-table-column
              prop="id"
              label="ID"
              sortable
              :show-overflow-tooltip="true"
            >
            </el-table-column>
            <el-table-column prop="name" label="标题" sortable>
            </el-table-column>
            <el-table-column prop="priority" label="优先级">
              <template v-slot:default="scope">
                <requiremnet-priority-item
                  :value="scope.row.priority"
                  :text="requiremnetPriority[scope.row.priority]"
                ></requiremnet-priority-item>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态"> </el-table-column>
            <el-table-column prop="assignee" label="处理人"> </el-table-column>
            <el-table-column prop="creator" label="创建人"> </el-table-column>
            <el-table-column label="截止日期" prop="dueDate" sortable>
              <!-- <template v-slot:default="scope">
            <span>{{ scope.row.startTime | timestampFormatDate }}</span>
          </template> -->
            </el-table-column>
            <el-table-column label="创建时间" prop="createTime" sortable>
              <!-- <template v-slot:default="scope">
            <span>{{ scope.row.startTime | timestampFormatDate }}</span>
          </template> -->
            </el-table-column>
            <el-table-column label="更新时间" prop="updateTime" sortable>
              <!-- <template v-slot:default="scope">
            <span>{{ scope.row.startTime | timestampFormatDate }}</span>
          </template> -->
            </el-table-column>
            <!-- <el-table-column prop="labelIds" label="标签"> </el-table-column> -->
            <el-table-column prop="watcher" label="关注人"> </el-table-column>
            <el-table-column label="开始日期" prop="startDate" sortable>
              <!-- <template v-slot:default="scope">
            <span>{{ scope.row.startTime | timestampFormatDate }}</span>
          </template> -->
            </el-table-column>
          </el-table>
          <ms-table-pagination
            :change="fetchRequirements"
            :current-page.sync="pagination.currentPage"
            :page-size.sync="pagination.pageSize"
            :total="pagination.total"
          />
        </el-scrollbar>
      </el-main>
    </template>
  </el-drawer>
</template>

<script>
import { getIterationRequirements } from "@/network/iteration";
import MsTable from "@/business/components/common/components/table/MsTable";
import MsTablePagination from "@/business/components/common/pagination/TablePagination";
import { _filter, _sort } from "@/common/js/tableUtils";
import { requiremnetPriority } from "./constance";
import RequiremnetPriorityItem from "./RequiremnetPriorityItem";

export default {
  name: "IterationRequirementsDrawer",
  components: {
    MsTablePagination,
    MsTable,
    RequiremnetPriorityItem,
  },
  data() {
    return {
      visible: false,
      tableData: [],
      iterationRow: {},
      pagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0,
      },
      page: {},
      condition: {},
      requiremnetPriority,
      isLoading: true,
    };
  },
  computed: {
    pagedTableData() {
      return this.tableData.slice(
        this.pagination.pageSize * (this.pagination.currentPage - 1),
        this.pagination.pageSize * this.pagination.currentPage
      );
    },
  },
  methods: {
    open(iterationRow) {
      this.iterationRow = iterationRow;
      this.visible = true;
      this.fetchRequirements();
      // this.$nextTick(() => {
      //   this.$refs.issueEditDetail.open(data);
      // });
    },
    handleClose() {
      this.visible = false;
    },
    save() {
      // this.$refs.issueEditDetail.save();
    },
    fetchRequirements() {
      this.isLoading = true;
      getIterationRequirements(
        {
          iterationId: this.iterationRow.id,
          ...this.condition,
          goPage: this.pagination.currentPage,
          pageSize: this.pagination.pageSize,
        },
        (res) => {
          this.tableData = res?.data?.listObject || [];
          this.pagination.total = res.data.itemCount;
          this.isLoading = false;
        }
      );
    },
    onFilterChange(filters) {
      _filter(filters, this.condition);
      this.fetchRequirements();
    },
    onSortChange(column) {
      // 每次只对一个字段排序
      if (this.condition.orders) {
        this.condition.orders = [];
      }
      _sort(column, this.condition);
      this.fetchRequirements();
    },
  },
};
</script>

<style scoped>
div /deep/ .el-drawer__header {
  font-size: 16px;
}
div /deep/ .el-drawer__body {
  padding: 0 20px;
}
</style>
