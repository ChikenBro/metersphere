<template>
  <ms-container>
    <ms-main-container>
      <el-card class="table-card">
        <template v-slot:header>
          <ms-table-header :create-permission="['PROJECT_TRACK_ISSUE:READ+CREATE']" :condition.sync="page.condition" @search="getIssues" @create="handleCreate"
                           :create-tip="$t('test_track.issue.create_issue')"
                           :tip="$t('commons.search_by_name_or_id')">
            <template v-slot:button>
              <el-tooltip v-if="true" :content="$t('test_track.issue.update_third_party_bugs')">
                <ms-table-button icon="el-icon-refresh" v-if="true"
                                 :content="$t('test_track.issue.sync_bugs')" @click="syncIssues"/>
              </el-tooltip>
            </template>
          </ms-table-header>
        </template>

        <ms-table
          v-loading="page.result.loading"
          :data="page.data"
          :enableSelection="false"
          :condition="page.condition"
          :total="page.total"
          :page-size.sync="page.pageSize"
          :operators="operators"
          :show-select-all="false"
          :screen-height="screenHeight"
          @handlePageChange="getIssues"
          :fields.sync="fields"
          :field-key="tableHeaderKey"
          @saveSortField="saveSortField"
          @refresh="getIssues"
          :custom-fields="[]"
          ref="table"
        >
          <span v-for="(item) in fields" :key="item.key">
            <!-- 缺陷id -->
            <ms-table-column
              :label="$t('test_track.issue.id')"
              prop="num"
              :field="item"
              sortable
              :fields-width="fieldsWidth">
              <template v-slot="scope">
                <el-link :href="linkBaseUrl + scope.row.id" type="primary" target="_blank" >
                  {{scope.row.num}}
                </el-link>
              </template>
            </ms-table-column>
            <!-- 缺陷标题 -->
            <ms-table-column
              :field="item"
              :fields-width="fieldsWidth"
              :label="$t('test_track.issue.title')"
              sortable
              min-width="120px"
              prop="title">
            </ms-table-column>
            <!-- 缺陷状态 -->
            <ms-table-column
              :field="item"
              :fields-width="fieldsWidth"
              :filters="getFilterOptions('platformStatus')" 
              :label="$t('test_track.issue.status')"
              min-width="140px"
              prop="platformStatus">
            </ms-table-column>
            <!-- 平台 -->
            <ms-table-column
              :field="item"
              :fields-width="fieldsWidth"
              :filters="getFilterOptions('platform')" 
              :label="$t('test_track.issue.platform')"
              prop="platform">
            </ms-table-column>
            <!-- <ms-table-column
                    :field="item"
                    :fields-width="fieldsWidth"
                    sortable
                    :label="$t('test_track.issue.platform_status') "
                    prop="platformStatus">
              <template v-slot="scope">
                {{ scope.row.platformStatus ? scope.row.platformStatus : '--'}}
              </template>
            </ms-table-column> -->
            <!-- 处理人 -->
            <ms-table-column
              :field="item"
              :fields-width="fieldsWidth"
              :filters="getFilterOptions('assigneeName')" 
              column-key="assigneeName "
              sortable
              min-width="130px"
              :label="$t('test_track.issue.handler')"
              prop="assigneeName">
            </ms-table-column>
            <!-- 缺陷描述 -->
            <issue-description-table-item :fields-width="fieldsWidth" :field="item"/>

  <!--          <ms-table-column-->
  <!--            :field="item"-->
  <!--            :fields-width="fieldsWidth"-->
  <!--            :label="$t('test_track.issue.issue_resource')"-->
  <!--            prop="resourceName">-->
  <!--            <template v-slot="scope">-->
  <!--              <el-link v-if="scope.row.resourceName" @click="$router.push('/track/plan/view/' + scope.row.resourceId)">-->
  <!--                {{ scope.row.resourceName }}-->
  <!--              </el-link>-->
  <!--              <span v-else>-->
  <!--              &#45;&#45;-->
  <!--            </span>-->
  <!--            </template>-->
  <!--          </ms-table-column>-->
            <!-- 用例数 -->
            <ms-table-column
              :field="item"
              :fields-width="fieldsWidth"
              :label="$t('test_track.issue.case_number')"
              prop="caseCount">
                <template v-slot="scope">
                  <router-link :to="scope.row.caseCount > 0 ? {name: 'testCase', params: { projectId: 'all', ids: scope.row.caseIds }} : {}">
                    {{scope.row.caseCount}}
                  </router-link>
                </template>
            </ms-table-column>
            <!-- 创建时间 -->
            <ms-table-column prop="createTime"
                        :field="item"
                        :fields-width="fieldsWidth"
                        :label="$t('commons.create_time')"
                        sortable
                        min-width="180px">
              <template v-slot:default="scope">
                <span>{{ scope.row.createTime | timestampFormatDate }}</span>
              </template>
            </ms-table-column >
            <!-- 缺陷类型 -->
            <ms-table-column
              :field="item"
              :fields-width="fieldsWidth"
              min-width="140px"
              column-key="defectTypeName"
              :filters="getFilterOptions('defectTypeName')" 
              :label="$t('test_track.issue.defect_type')"
              prop="defectTypeName">
            </ms-table-column>
            <!-- 优先级 -->
            <ms-table-column
              :field="item"
              :fields-width="fieldsWidth"
              column-key="creator"
              sortable
              min-width="130px"
              :label="$t('test_track.issue.priority')"
              :filters="[{text:'紧急', value: 3}, {text:'高', value: 2}, {text:'中', value: 1}, {text:'低', value: 0}]" 
              prop="priority">
              <template v-slot:default="scope">
                <span>{{ scope.row.priority | priorityFormat }}</span>
              </template>
            </ms-table-column>
            <!-- 预计工时 -->
            <ms-table-column
              :field="item"
              :fields-width="fieldsWidth"
              column-key="creator"
              :label="$t('test_track.issue.working_hours')"
              prop="workingHours">
               <template v-slot:default="scope">
                <span>{{ scope.row.workingHours }} 小时</span>
              </template>
            </ms-table-column>
            <!-- 所属迭代 -->
            <ms-table-column
              :field="item"
              :fields-width="fieldsWidth"
              :label="$t('test_track.issue.iteration')"
              prop="iterationName">
            </ms-table-column>
            <!-- 工时记录 -->
            <ms-table-column
              :field="item"
              :fields-width="fieldsWidth"
              :label="$t('test_track.issue.working_hours_log')"
              prop="recordedHours">
               <template v-slot:default="scope">
                <span>{{ scope.row.recordedHours }} 小时</span>
              </template>
            </ms-table-column>
            <!-- 关注人 -->
            <ms-table-column
              :field="item"
              :fields-width="fieldsWidth"
              column-key="watcherName"
              :label="$t('test_track.issue.followers')"
              prop="watcherName">
            </ms-table-column>
            <!-- 开始时间 -->
            <ms-table-column prop="startDate"
                        :field="item"
                        :fields-width="fieldsWidth"
                        :label="$t('test_track.issue.start_date')"
                        sortable
                        min-width="180px">
              <template v-slot:default="scope">
                <span>{{ scope.row.startDate | timestampFormatDate }}</span>
              </template>
            </ms-table-column >
            <!-- 截止时间 -->
            <ms-table-column prop="dueDate"
                        :field="item"
                        :fields-width="fieldsWidth"
                        :label="$t('test_track.issue.due_date')"
                        sortable
                        min-width="180px">
              <template v-slot:default="scope">
                <span>{{ scope.row.dueDate | timestampFormatDate }}</span>
              </template>
            </ms-table-column >
             <!-- 创建人 -->
            <ms-table-column
              :field="item"
              :fields-width="fieldsWidth"
              :filters="getFilterOptions('creatorName')" 
              :label="$t('test_track.issue.creator')"
              prop="creatorName">
            </ms-table-column>
            <!-- 关联需求 -->
            <ms-table-column
              :field="item"
              :fields-width="fieldsWidth"
              :filters="getFilterOptions('requirementName')" 
              min-width="140px"
              :label="$t('test_track.issue.related_requirements')"
              prop="requirementName">
            </ms-table-column>
            <!-- 模块 -->
            <ms-table-column
              :field="item"
              :fields-width="fieldsWidth"
              :filters="getFilterOptions('model')" 
              :label="$t('test_track.issue.module')"
              prop="model">
            </ms-table-column>
            <ms-table-column v-for="field in issueTemplate.customFields" :key="field.id"
                            :field="item"
                            :fields-width="fieldsWidth"
                            :label="field.name"
                            :prop="field.name">
                <template v-slot="scope">
                  <span v-if="field.name === '状态'">
                    {{getCustomFieldValue(scope.row, field) ? getCustomFieldValue(scope.row, field) : issueStatusMap[scope.row.status]}}
                  </span>
                  <span v-else>
                    {{getCustomFieldValue(scope.row, field)}}
                  </span>
                </template>
            </ms-table-column>

          </span>
        </ms-table>
        
        <!-- 删除确认框 -->
        <ms-edit-dialog
          :visible.sync="dialogVisible"
          width="30%"
          :title="$t('test_track.issue.delete')"
          :with-footer="true"
          :close-on-click-modal="true"
          @confirm="handleDeLeteConfirm"
          >
          <el-form>
            <el-row>
              <el-col :span="24">
                <el-form-item :label="`请输入删除缺陷的原因, 缺陷ID: ${deleteIssueInfo.num}`">
                  <el-input size="small" v-model="deleteIssueInfo.remark" placeholder="请输入删除原因"/>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </ms-edit-dialog>
        
        <ms-table-pagination :change="getIssues" :current-page.sync="page.currentPage" :page-size.sync="page.pageSize"
                             :total="page.total"/>

        <issue-edit @refresh="getIssues" ref="issueEdit"/>

      </el-card>
    </ms-main-container>
  </ms-container>
</template>

<script>
import MsTable from "@/business/components/common/components/table/MsTable";
import MsTableColumn from "@/business/components/common/components/table/MsTableColumn";
import MsTableOperators from "@/business/components/common/components/MsTableOperators";
import MsTableButton from "@/business/components/common/components/MsTableButton";
import MsTablePagination from "@/business/components/common/pagination/TablePagination";
import MsEditDialog from "@/business/components/common/components/MsEditDialog";
import {
  CUSTOM_FIELD_SCENE_OPTION,
  CUSTOM_FIELD_TYPE_OPTION,
  FIELD_TYPE_MAP, ISSUE_PLATFORM_OPTION,
  ISSUE_STATUS_MAP,
  SYSTEM_FIELD_NAME_MAP
} from "@/common/js/table-constants";
import MsTableHeader from "@/business/components/common/components/MsTableHeader";
import IssueDescriptionTableItem from "@/business/components/track/issue/IssueDescriptionTableItem";
import IssueEdit from "@/business/components/track/issue/IssueEdit";
import {getIssues, syncIssues} from "@/network/Issue";
import {
  getCustomFieldValue,
  getCustomTableWidth,
  getPageInfo, getTableHeaderWithCustomFields,saveLastTableSortField,getLastTableSortField
} from "@/common/js/tableUtils";
import MsContainer from "@/business/components/common/components/MsContainer";
import MsMainContainer from "@/business/components/common/components/MsMainContainer";
import {getCurrentProjectID} from "@/common/js/utils";
import {getIssueTemplate} from "@/network/custom-field-template";
import {getProjectMember} from "@/network/user";

export default {
  name: "IssueList",
  components: {
    MsMainContainer,
    MsContainer,
    IssueEdit,
    IssueDescriptionTableItem,
    MsTableHeader,
    MsTablePagination, MsTableButton, MsTableOperators, MsTableColumn, MsTable,
    MsEditDialog
  },
  data() {
    return {
      linkBaseUrl: "https://jira.mudutv.com/browse/",
      page: getPageInfo(),
      fields: [],
      tableHeaderKey:"ISSUE_LIST",
      fieldsWidth: getCustomTableWidth('ISSUE_LIST'),
      screenHeight: 'calc(100vh - 200px)',
      operators: [
        {
          tip: this.$t('commons.edit'), icon: "el-icon-edit",
          exec: this.handleEdit,
          isDisable: this.btnDisable,
          permissions: ['PROJECT_TRACK_ISSUE:READ+EDIT']
        }, {
          tip: this.$t('commons.copy'), icon: "el-icon-copy-document", type: "success",
          exec: this.handleCopy,
          isDisable: this.btnDisable
        }, {
          tip: this.$t('commons.delete'), icon: "el-icon-delete", type: "danger",
          exec: this.handleDelete,
          isDisable: this.btnDisable,
          permissions: ['PROJECT_TRACK_ISSUE:READ+DELETE']
        }
      ],
      issueTemplate: {},
      members: [],
      isThirdPart: false,
      dialogVisible: false,
      deleteIssueInfo: {
        ids: [],
        projectId: '',
        remark:'',
        num: '',
      },
    };
  },
  filters: {
    priorityFormat(value) {
      return ['低', '中', '高', '紧急'][parseInt(value)]
    }
  },
  activated() {
    getProjectMember((data) => {
      this.members = data;
    });
    this.fields = getTableHeaderWithCustomFields('ISSUE_LIST', []);
    this.$refs.table.reloadTable();
    // getIssueTemplate()
    //   .then((template) => {
    //     this.issueTemplate = template;
    //     if (this.issueTemplate.platform === 'metersphere') {
    //       this.isThirdPart = false;
    //     } else {
    //       this.isThirdPart = true;
    //     }
    //     this.fields = getTableHeaderWithCustomFields('ISSUE_LIST', this.issueTemplate.customFields);
    //     if (!this.isThirdPart) {
    //       for (let i = 0; i < this.fields.length; i++) {
    //         if (this.fields[i].id === 'platformStatus') {
    //           this.fields.splice(i, 1);
    //           break;
    //         }
    //       }
    //     }
    //     this.$refs.table.reloadTable();
    //   });
    this.getIssues();
  },
  computed: {
    fieldFilters() {
      return CUSTOM_FIELD_TYPE_OPTION;
    },
    platformFilters() {
     return ISSUE_PLATFORM_OPTION;
    },
    sceneFilters() {
      return CUSTOM_FIELD_SCENE_OPTION;
    },
    fieldTypeMap() {
      return FIELD_TYPE_MAP;
    },
    issueStatusMap() {
      return ISSUE_STATUS_MAP;
    },
    systemNameMap() {
      return SYSTEM_FIELD_NAME_MAP;
    },
    projectId() {
      return getCurrentProjectID();
    }
  },
  methods: {
    getCustomFieldValue(row, field) {
      return getCustomFieldValue(row, field, this.members);
    },
    getIssues() {
      this.page.condition.projectId = this.projectId;
      let orderArr = this.getSortField();
      if(orderArr){
        this.page.condition.orders = orderArr;
      }
      this.page.result = getIssues(this.page);
    },
    handleEdit(data) {
      // 传了row(data)和index 只用到了data
      data = {
          "title": "默认",
          "descriptions": {
              "preconditions": "缺陷内容",
              "operatingSteps": "操作步骤",
              "expectedResult": "预期结果",
              "actualResult": "实际结果"
          },
          "creator": "0852",
          "fields": {
              "defectTypeId": 30801759,
              "priority": 1,
              "workingHours": "12",
              "iterationCode": 91,
              "assigneeName": "0852",
              "requirementCode": 190,
              "moduleId": "",
              "startDate": "2022-02-28T16:00:00.000Z",
              "dueDate": "2022-03-01T16:00:00.000Z"
          },
          "environment": "dev",
          "repetitionFrequency": "must",
          "projectId": "648d74c1-4973-4d1c-8c22-a68a0357d6c4",
          "organizationId": "d1ab2464-0a3d-11ec-b53d-0c42a1eda428",
          "testCaseIds": []
      }
      data.drawerTitle = '编辑缺陷'
      this.$refs.issueEdit.open(data);
    },
    handleCreate() {
      this.$refs.issueEdit.open();
    },
    handleCopy(data) {
      data = {
          "title": "默认",
          "descriptions": {
              "preconditions": "缺陷内容",
              "operatingSteps": "操作步骤",
              "expectedResult": "预期结果",
              "actualResult": "实际结果"
          },
          "creator": "0852",
          "fields": {
              "defectTypeId": 30801759,
              "priority": 1,
              "workingHours": "12",
              "iterationCode": 91,
              "assigneeName": "0852",
              "requirementCode": 190,
              "moduleId": "",
              "startDate": "2022-02-28T16:00:00.000Z",
              "dueDate": "2022-03-01T16:00:00.000Z"
          },
          "environment": "dev",
          "repetitionFrequency": "must",
          "projectId": "648d74c1-4973-4d1c-8c22-a68a0357d6c4",
          "organizationId": "d1ab2464-0a3d-11ec-b53d-0c42a1eda428",
          "testCaseIds": []
      }      
      let copyData = {};
      Object.assign(copyData, data);
      copyData.id = null;
      copyData.isCreateTitle = true
      copyData.drawerTitle = '复制缺陷'
      this.$refs.issueEdit.open(copyData);
    },    
    // 显示删除确认框
    handleDelete(data) {
      this.deleteIssueInfo.num = data.num
      this.deleteIssueInfo.ids = [data.id]
      this.deleteIssueInfo.projectId = data.projectId
      this.openDialog()
    },
    btnDisable(row) {
      if (row.platform === 'Local') {
        return false;
      }
      return true;
    },
    saveSortField(key,orders){
      saveLastTableSortField(key,JSON.stringify(orders));
    },
    syncIssues() {
      this.page.result = syncIssues(() => {
        this.getIssues();
      });
    },
    getSortField(){
      let orderJsonStr = getLastTableSortField(this.tableHeaderKey);
      let returnObj = null;
      if(orderJsonStr){
        try {
          returnObj = JSON.parse(orderJsonStr);
        }catch (e){
          return null;
        }
      }
      return returnObj;
    },
    // 打开缺陷删除对话框
    openDialog() {
      this.dialogVisible = true;
      this.deleteIssueInfo.remark = ''
    },
    // 确认提交
    handleDeLeteConfirm() {
      this.page.result = this.$post('issue/delete/', this.deleteIssueInfo, () => {
        this.$success(this.$t('commons.delete_success'));
        this.getIssues();
      });
      this.dialogVisible = false;
      this.deleteIssueInfo.remark = ''
    },
    // 根据属性获取筛选列表
    getFilterOptions(prop) {
      const options = []
      const hasExist = {}
      if(this.page.data) {
        this.page.data.forEach(item => {
          let value = item[prop]
          if(!!value && hasExist[value] === undefined) {
            options.push({text: value, value})
            hasExist[value] = true
          }
        })

      }
      return options
    },
  }
};
</script>

<style scoped>
.table-page {
  padding-top: 20px;
  margin-right: -9px;
  float: right;
}

.el-table {
  cursor: pointer;
}
</style>
