<template>
  <el-main
    v-loading="result.loading"
    class="container"
    :style="isPlan ? '' : 'height: calc(100vh - 62px)'"
  >
    <el-scrollbar style="paddingtop: 20px">
      <el-form
        :model="form"
        :rules="rules"
        label-position="right"
        label-width="80px"
        ref="form"
        :disabled="isDisabled"
      >
        <el-row class="custom-field-row">
          <el-col :span="14">
            <el-form-item :label="$t('commons.title')" prop="title">
              <el-input v-model="form.title" autocomplete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="6" v-if="isEdit" :push="2">
            <el-form-item
              :label="$t('test_track.issue.status')"
              :label-width="formLabelWidth"
            >
              <el-select
                v-model="form.statusId"
                :placeholder="$t('commons.default')"
                @change="$forceUpdate()"
              >
                <el-option
                  v-for="(item, index) in statusList"
                  :key="index"
                  :label="item.label"
                  :value="item.value"
                >
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row class="custom-field-row">
          <el-col :span="8">
            <el-form-item
              :label="$t('test_track.issue.defect_type')"
              prop="fields.defectTypeId"
              :label-width="formLabelWidth"
            >
              <el-select
                v-model="form.fields.defectTypeId"
                placeholder="缺陷类型"
                @blur="getList(1, '')"
                @change="$forceUpdate()"
                clearable
                @clear="getList(1, '')"
                filterable
                :filter-method="(query) => getList(1, query)"
              >
                <el-option
                  v-for="(item, index) in defectList"
                  :key="index"
                  @change="$forceUpdate()"
                  :label="item.label"
                  :value="item.value"
                >
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="8">
            <el-form-item
              :label="$t('test_track.issue.priority')"
              prop="fields.priority"
              :label-width="formLabelWidth"
            >
              <el-select
                v-model="form.fields.priority"
                :placeholder="$t('commons.default')"
              >
                <el-option
                  v-for="(item, index) in priorityList"
                  :key="index"
                  :label="item.label"
                  :value="item.value"
                >
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="8">
            <el-form-item
              :label="$t('test_track.issue.related_requirements')"
              prop="fields.requirementCode"
              :label-width="formLabelWidth"
            >
              <el-select
                v-model="form.fields.requirementCode"
                placeholder="未关联需求"
                clearable
                @change="$forceUpdate()"
                @blur="getList(2, '')"
                @clear="getList(2, '')"
                filterable
                :filter-method="(query) => getList(2, query)"
              >
                <el-option
                  v-for="(item, index) in requirementList"
                  :key="index"
                  :label="item.label"
                  :value="item.value"
                >
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="8">
            <el-form-item
              :label="$t('test_track.issue.iteration')"
              prop="fields.iterationCode"
              :label-width="formLabelWidth"
            >
              <el-select
                v-model="form.fields.iterationCode"
                placeholder="请选择迭代"
                @blur="getList(3, '')"
                clearable
                @change="$forceUpdate()"
                @clear="getList(3, '')"
                filterable
                :filter-method="(query) => getList(3, query)"
              >
                <el-option
                  v-for="(item, index) in iterationList"
                  :key="index"
                  :label="item.label"
                  :value="item.value"
                >
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="8">
            <el-form-item
              :label="$t('test_track.issue.handler')"
              prop="fields.assignee"
              :label-width="formLabelWidth"
            >
              <el-select
                filterable
                v-model="form.fields.assignee"
                placeholder="未指定"
              >
                <el-option
                  v-for="(item, index) in assigneeList"
                  :key="index"
                  :label="item.label"
                  :value="item.value"
                >
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="5">
            <el-form-item
              :label="$t('test_track.issue.working_hours')"
              prop="fields.workingHours"
              :label-width="formLabelWidth"
            >
              <el-input
                v-model="form.fields.workingHours"
                @keyup.native="onWorkingHoursInput"
              >
                <i slot="suffix">&emsp;小时</i>
              </el-input>
            </el-form-item>
          </el-col>

          <el-col :span="8">
            <el-form-item
              :label="$t('test_track.issue.module')"
              prop="fields.moduleId"
              :label-width="formLabelWidth"
            >
              <el-select
                filterable
                v-model="form.fields.moduleId"
                placeholder="未指定"
              >
                <el-option
                  v-for="(item, index) in moduleList"
                  :key="index"
                  :label="item.label"
                  :value="item.value"
                >
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="8">
            <el-form-item
              label="开始日期"
              prop="fields.startDate"
              :label-width="formLabelWidth"
            >
              <el-date-picker
                v-model="form.fields.startDate"
                type="date"
                value-format="yyyy-MM-dd"
                :picker-options="startDatePicker"
                placeholder="未指定"
              >
              </el-date-picker>
            </el-form-item>
          </el-col>

          <el-col :span="8">
            <el-form-item
              label="截止日期"
              prop="fields.dueDate"
              :label-width="formLabelWidth"
            >
              <el-date-picker
                v-model="form.fields.dueDate"
                type="date"
                value-format="yyyy-MM-dd"
                :picker-options="endDatePicker"
                placeholder="未指定"
              >
              </el-date-picker>
            </el-form-item>
          </el-col>

          <el-col :span="8">
            <el-form-item
              :label="$t('test_track.issue.repetition_environment')"
              prop="fields.environment"
              :label-width="formLabelWidth"
            >
              <el-select
                v-model="form.fields.environment"
                :placeholder="$t('commons.default')"
              >
                <el-option
                  v-for="(item, index) in environmentList"
                  :key="index"
                  :label="item.label"
                  :value="item.value"
                >
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="8">
            <el-form-item
              :label="$t('test_track.issue.repetition_frequency')"
              prop="fields.repetitionFrequency"
              :label-width="formLabelWidth"
            >
              <el-select
                v-model="form.fields.repetitionFrequency"
                :placeholder="$t('commons.default')"
              >
                <el-option
                  v-for="(item, index) in repetitionFrequencyList"
                  :key="index"
                  :label="item.label"
                  :value="item.value"
                >
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <!-- 自定义字段 -->
        <!-- <el-form v-if="isFormAlive" :model="customFieldForm" :rules="customFieldRules" ref="customFieldForm"
                 class="case-form">
          <el-row class="custom-field-row">
            <el-col
              v-for="(item, index) in issueTemplate.customFields"
              :key="index"
              :span="8"
            >
              <el-form-item
                :label="item.system ? $t(systemNameMap[item.name]) : item.name"
                :prop="item.name"
                :label-width="formLabelWidth"
              >
                <custom-filed-component
                  :data="item"
                  :form="customFieldForm"
                  prop="defaultValue"
                  @reload="reloadForm"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form> -->

        <form-rich-text-item
          title="前置步骤"
          :data="form.descriptions"
          prop="preconditions"
          key="preconditions"
          prefix="descriptions."
          :disabled="isDisabled"
        />
        <form-rich-text-item
          :title="$t('custom_field.operating_steps')"
          :data="form.descriptions"
          prop="operatingSteps"
          key="operatingSteps"
          prefix="descriptions."
          :disabled="isDisabled"
        />
        <form-rich-text-item
          :title="$t('custom_field.expected_result')"
          :data="form.descriptions"
          prop="expectedResult"
          key="expectedResult"
          prefix="descriptions."
          :disabled="isDisabled"
        />
        <form-rich-text-item
          :title="$t('custom_field.actual_result')"
          :data="form.descriptions"
          prop="actualResult"
          key="actualResult"
          prefix="descriptions."
          :disabled="isDisabled"
        />

        <el-row class="custom-field-row">
          <el-col v-if="hasTapdId" :span="8">
            <el-form-item
              :label-width="formLabelWidth"
              :label="$t('test_track.issue.tapd_current_owner')"
              prop="tapdUsers"
            >
              <el-select
                v-model="form.tapdUsers"
                multiple
                filterable
                :placeholder="
                  $t('test_track.issue.please_choose_current_owner')
                "
              >
                <el-option
                  v-for="(userInfo, index) in tapdUsers"
                  :key="index"
                  :label="userInfo.user"
                  :value="userInfo.user"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col v-if="hasZentaoId" :span="8">
            <el-form-item
              :label-width="formLabelWidth"
              :label="$t('test_track.issue.zentao_bug_build')"
              prop="zentaoBuilds"
            >
              <el-select
                v-model="form.zentaoBuilds"
                multiple
                filterable
                :placeholder="$t('test_track.issue.zentao_bug_build')"
              >
                <el-option
                  v-for="(build, index) in Builds"
                  :key="index"
                  :label="build.name"
                  :value="build.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col v-if="hasZentaoId" :span="8">
            <el-form-item
              :label-width="formLabelWidth"
              :label="$t('test_track.issue.zentao_bug_assigned')"
              prop="zentaoAssigned"
            >
              <el-select
                v-model="form.zentaoAssigned"
                filterable
                :placeholder="
                  $t('test_track.issue.please_choose_current_owner')
                "
              >
                <el-option
                  v-for="(userInfo, index) in zentaoUsers"
                  :key="index"
                  :label="userInfo.name"
                  :value="userInfo.user"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item v-if="!isPlan">
          <test-case-issue-list
            ref="testCaseIssueList"
            :test-case-contain-ids="testCaseContainIds"
            :issues-id="form.id"
          />
        </el-form-item>
      </el-form>
    </el-scrollbar>
  </el-main>
</template>

<script>
import TemplateComponentEditHeader from "@/business/components/track/plan/view/comonents/report/TemplateComponentEditHeader";
import MsFormDivider from "@/business/components/common/components/MsFormDivider";
import CustomFieldFormList from "@/business/components/settings/workspace/template/CustomFieldFormList";
import CustomFieldRelateList from "@/business/components/settings/workspace/template/CustomFieldRelateList";
import FormRichTextItem from "@/business/components/track/case/components/FormRichTextItem";
import { SYSTEM_FIELD_NAME_MAP } from "@/common/js/table-constants";
import {
  buildCustomFields,
  getTemplate,
  parseCustomField,
} from "@/common/js/custom_field";
import CustomFiledComponent from "@/business/components/settings/workspace/template/CustomFiledComponent";
import TestCaseIssueList from "@/business/components/track/issue/TestCaseIssueList";
import IssueEditDetail from "@/business/components/track/issue/IssueEditDetail";
import {
  getCurrentOrganizationId,
  getCurrentProjectID,
  getCurrentUserId,
} from "@/common/js/utils";
import { getIssueTemplate } from "@/network/custom-field-template";
import { getProjectMember } from "@/network/user";

export default {
  name: "IssueEditDetail",
  components: {
    IssueEditDetail,
    TestCaseIssueList,
    CustomFiledComponent,
    FormRichTextItem,
    CustomFieldRelateList,
    CustomFieldFormList,
    MsFormDivider,
    TemplateComponentEditHeader,
  },
  data() {
    return {
      result: {},
      relateFields: [],
      isFormAlive: true,
      formLabelWidth: "150px",
      issueTemplate: {},
      customFieldForm: {},
      customFieldRules: {},
      form: {
        id: "",
        title: "",
        descriptions: {
          preconditions: "",
          operatingSteps: "",
          expectedResult: "",
          actualResult: "",
        },
        creator: null,
        fields: {
          defectTypeId: "",
          priority: 1,
          workingHours: undefined,
          iterationCode: undefined,
          assignee: "",
          requirementCode: undefined,
          moduleId: "",
          startDate: "",
          dueDate: "",
          environment: "dev",
          repetitionFrequency: "must",
        },
        statusId: undefined,
        issueId: undefined,
      },
      rules: {
        title: [
          {
            required: true,
            message: this.$t("commons.please_fill_content"),
            trigger: "blur",
          },
          {
            max: 64,
            message: this.$t("test_track.length_less_than") + "64",
            trigger: "blur",
          },
        ],
        "fields.priority": [
          {
            required: true,
            message: this.$t("commons.please_fill_content"),
            trigger: "blur",
          },
        ],
        "fields.assignee": [
          {
            required: true,
            message: this.$t("commons.please_fill_content"),
            trigger: "blur",
          },
        ],
        "fields.workingHours": [
          {
            max: 8,
            message: this.$t("test_track.length_less_than") + "8",
            trigger: "blur",
          },
        ],
        "fields.defectTypeId": [
          {
            required: true,
            message: this.$t("commons.please_fill_content"),
            trigger: "blur",
          },
        ],
        "fields.iterationCode": [
          {
            required: true,
            message: this.$t("commons.please_fill_content"),
            trigger: "blur",
          },
        ],
        "fields.requirementCode": [
          {
            required: true,
            message: this.$t("commons.please_fill_content"),
            trigger: "blur",
          },
        ],
        "fields.environment": [
          {
            required: true,
            message: this.$t("commons.please_fill_content"),
            trigger: "blur",
          },
        ],
        "fields.repetitionFrequency": [
          {
            required: true,
            message: this.$t("commons.please_fill_content"),
            trigger: "blur",
          },
        ],
        "descriptions.preconditions": [
          {
            required: true,
            message: this.$t("commons.please_fill_content"),
            trigger: "blur",
          },
        ],
        "descriptions.operatingSteps": [
          {
            required: true,
            message: this.$t("commons.please_fill_content"),
            trigger: "blur",
          },
        ],
        "descriptions.expectedResult": [
          {
            required: true,
            message: this.$t("commons.please_fill_content"),
            trigger: "blur",
          },
        ],
        "descriptions.actualResult": [
          {
            required: true,
            message: this.$t("commons.please_fill_content"),
            trigger: "blur",
          },
        ],
      },
      testCaseContainIds: new Set(),
      url: "",
      tapdUsers: [],
      zentaoUsers: [],
      Builds: [],
      hasTapdId: false,
      hasZentaoId: false,
      priorityList: [
        { label: "紧急", value: "3" },
        { label: "高", value: "2" },
        { label: "中", value: "1" },
        { label: "低", value: "0" },
      ], // 优先级列表
      assigneeList: [], //处理人列表
      moduleList: [], //模块列表
      startDatePicker: this.beginDate(), // 开始日期限制
      endDatePicker: this.processDate(), // 结束日期限制
      environmentList: [
        { label: "开发环境", value: "dev" },
        { label: "测试环境", value: "test" },
        { label: "预发环境", value: "uat" },
        { label: "线上环境", value: "prod" },
      ], // 复现环境列表
      repetitionFrequencyList: [
        { label: "必现", value: "must" },
        { label: "偶现", value: "sometimes" },
        { label: "高出现率", value: "manyTimes" },
      ], // 复现频率
      statusList: [
        { label: "待处理", value: 1 },
        { label: "重新打开", value: 2 },
        { label: "处理中", value: 3 },
        { label: "待验证", value: 4 },
        { label: "已拒绝", value: 5 },
        { label: "已关闭", value: 6 },
      ],
    };
  },
  props: {
    isPlan: {
      type: Boolean,
      default() {
        return false;
      },
    },
    isEdit: {
      type: Boolean,
      default() {
        return false;
      },
    },
    isDisabled: {
      type: Boolean,
      default() {
        return false;
      },
    },
    caseId: String,
    planId: String,
    iterationList: {
      type: Array,
      default() {
        return [];
      },
    },
    requirementList: {
      type: Array,
      default() {
        return [];
      },
    },
    defectList: {
      type: Array,
      default() {
        return [];
      },
    },
  },
  computed: {
    isSystem() {
      return this.form.system;
    },
    systemNameMap() {
      return SYSTEM_FIELD_NAME_MAP;
    },
    projectId() {
      return getCurrentProjectID();
    },
  },
  methods: {
    open(data) {
      this.initList();
      this.initEdit(data);
      this.getThirdPartyInfo();

      // getIssueTemplate()
      //   .then((template) => {
      //     this.issueTemplate = template;
      //     this.getThirdPartyInfo();
      //     initAddFuc(data);
      //   });
    },
    getThirdPartyInfo() {
      let platform = this.issueTemplate.platform;
      if (platform === "Zentao") {
        this.hasZentaoId = true;
        this.result = this.$post(
          "/issues/zentao/builds",
          {
            projectId: this.projectId,
            organizationId: getCurrentOrganizationId(),
          },
          (response) => {
            if (response.data) {
              this.Builds = response.data;
            }
            this.result = this.$post(
              "/issues/zentao/user",
              {
                projectId: this.projectId,
                organizationId: getCurrentOrganizationId(),
              },
              (response) => {
                this.zentaoUsers = response.data;
              }
            );
          }
        );
      }
      if (platform === "Tapd") {
        this.hasTapdId = true;
        this.result = this.$post(
          "/issues/tapd/user",
          {
            projectId: this.projectId,
            organizationId: getCurrentOrganizationId(),
          },
          (response) => {
            this.tapdUsers = response.data;
          }
        );
      }
    },
    initEdit(data) {
      this.testCaseContainIds = new Set();
      this.$refs["form"].resetFields();
      if (data) {
        if (data.testCaseIds) {
          this.testCaseContainIds = new Set(data.testCaseIds);
        }
        const statusList = [
          { label: "待处理", value: 1 },
          { label: "重新打开", value: 2 },
          { label: "处理中", value: 3 },
          { label: "待验证", value: 4 },
          { label: "已拒绝", value: 5 },
          { label: "已关闭", value: 6 },
        ];
        if (data.statusId !== 6) statusList.splice(1, 1);
        this.statusList = statusList;
        Object.assign(this.form, data);
        // if (!(data.options instanceof Array)) {
        //   this.form.options = data.options ? JSON.parse(data.options) : [];
        // }
        if (data.id) {
          this.url = "issues/update";
        } else {
          //copy
          this.url = "issues/add";
          if (!this.form.creator) {
            this.form.creator = getCurrentUserId();
          }
          this.form.title = data.title + "_copy";
        }
      } else {
        this.form = {
          id: "",
          title: "",
          descriptions: {
            preconditions: "",
            operatingSteps: "",
            expectedResult: "",
            actualResult: "",
          },
          creator: null,
          fields: {
            defectTypeId:
              this.defectList && this.defectList.length > 0
                ? this.defectList[0].value
                : "",
            priority: "1",
            workingHours: undefined,
            iterationCode: undefined,
            assignee: "",
            requirementCode: undefined,
            moduleId: "",
            startDate: "",
            dueDate: "",
            environment: "test",
            repetitionFrequency: "must",
          },
        };
        this.url = "issues/add";
        if (!this.form.creator) {
          this.form.creator = getCurrentUserId();
        }
      }
      // parseCustomField(this.form, this.issueTemplate, this.customFieldForm, this.customFieldRules, null);
      this.$nextTick(() => {
        if (this.$refs.testCaseIssueList) {
          this.$refs.testCaseIssueList.initTableData();
        }
      });
    },
    // 初始化下拉列表
    initList() {
      getProjectMember((data) => {
        this.assigneeList = data.map((item) => ({
          label: item.name,
          value: item.id,
        }));
      });
    },
    getList(type, name = "") {
      let url = "/field/template/issue/templates/list/1/10";
      this.$post(url, { projectId: this.projectId, type, name }, (res) => {
        let {
          data: { options },
        } = res;
        switch (type) {
          case 1:
            this.defectList =
              (options &&
                options.map((item) => ({
                  label: item.name,
                  value: item.id,
                }))) ||
              [];
            break;
          case 2:
            this.requirementList =
              (options &&
                options.map((item) => ({
                  label: item.name,
                  value: item.id,
                }))) ||
              [];
            break;
          case 3:
            this.iterationList =
              (options &&
                options.map((item) => ({
                  label: item.name,
                  value: item.id,
                }))) ||
              [];
            break;
        }
      });
    },
    reloadForm() {
      this.isFormAlive = false;
      this.$nextTick(() => (this.isFormAlive = true));
    },
    save(type) {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          this._save(type);
        }
      });
    },
    buildPram() {
      let param = {};
      Object.assign(param, this.form);
      param.projectId = this.projectId;
      param.organizationId = getCurrentOrganizationId();
      buildCustomFields(this.form, param, this.issueTemplate);
      if (this.isPlan) {
        param.testCaseIds = [this.caseId];
      } else {
        param.testCaseIds = Array.from(this.testCaseContainIds);
      }
      if (this.planId) {
        param.resourceId = this.planId;
      }
      return param;
    },
    _save(type) {
      let param = this.buildPram();
      this.parseOldFields(param);
      this.result = this.$post(this.url, param, () => {
        if (type !== "reCreate") this.$emit("close");
        else this.$emit("openNewDrawer");
        this.$success(this.$t("commons.save_success"));
        this.$emit("refresh");
      });
    },
    parseOldFields(param) {
      let customFieldsStr = param.customFields;
      if (customFieldsStr) {
        let customFields = JSON.parse(customFieldsStr);
        customFields.forEach((item) => {
          if (item.name === "状态") {
            param.status = item.value;
          }
        });
      }
    },
    // 开始限制
    beginDate() {
      const self = this;
      return {
        disabledDate(time) {
          if (self.form.fields.dueDate) {
            //如果结束时间不为空，则小于结束时间
            return (
              new Date(self.form.fields.dueDate).getTime() < time.getTime()
            );
          } else {
            // return time.getTime() > Date.now()//开始时间不选时，结束时间最大值小于等于当天
          }
        },
      };
    },
    // 结束限制
    processDate() {
      const self = this;
      return {
        disabledDate(time) {
          if (self.form.fields.startDate) {
            //如果开始时间不为空，则结束时间大于开始时间
            return (
              new Date(self.form.fields.startDate).getTime() > time.getTime()
            );
          } else {
            // return time.getTime() > Date.now()//开始时间不选时，结束时间最大值小于等于当天
          }
        },
      };
    },
    onWorkingHoursInput({ target }) {
      // 清除"数字"和"."以外的字符
      target.value = target.value.replace(/[^\d.]/g, "");
      // 验证第一个字符是数字
      target.value = target.value.replace(/^\./g, "");
      // 只保留第一个, 清除多余的
      target.value = target.value.replace(/\.{2,}/g, ".");
      target.value = target.value
        .replace(".", "$#$")
        .replace(/\./g, "")
        .replace("$#$", ".");
      // 只能输入两个小数
      target.value = target.value.replace(/^(\-)*(\d+)\.(\d\d).*$/, "$1$2.$3");

      //如果有小数点，不能为类似 1.10的数
      if (target.value.indexOf(".") > 0 && target.value.indexOf("0") > 2) {
        target.value = parseFloat(target.value);
      }
      //如果有小数点，不能为类似 0.00的数
      if (target.value.indexOf(".") > 0 && target.value.lastIndexOf("0") > 2) {
        target.value = parseFloat(target.value);
      }
      //以上已经过滤，此处控制的是如果没有小数点，首位不能为类似于 01、02的数
      if (target.value.indexOf(".") <= 0 && target.value != "") {
        target.value = parseFloat(target.value);
      }
      this.$nextTick(() => (this.form.fields.workingHours = target.value));
    },
  },
};
</script>

<style scoped>
.filed-list {
  margin-top: 30px;
}

.custom-field-row {
  padding-left: 18px;
}
</style>
