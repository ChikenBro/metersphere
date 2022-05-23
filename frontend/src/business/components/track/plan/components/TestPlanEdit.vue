<template>
  <div>
    <el-dialog
      :close-on-click-modal="false"
      :title="
        operationType === 'edit'
          ? $t('test_track.plan.edit_plan')
          : $t('test_track.plan.create_plan')
      "
      :visible.sync="dialogFormVisible"
      @close="close"
      width="70%"
    >
      <el-form
        :model="form"
        :rules="rules"
        ref="planFrom"
        v-if="isStepTableAlive"
      >
        <el-row>
          <el-col :span="8" :offset="1">
            <el-form-item
              :label="$t('test_track.plan.plan_name')"
              :label-width="formLabelWidth"
              prop="name"
            >
              <el-input
                v-model="form.name"
                :placeholder="$t('test_track.plan.input_plan_name')"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="10" :offset="1">
            <el-form-item
              :label="$t('commons.tag')"
              :label-width="formLabelWidth"
              prop="tag"
            >
              <ms-input-tag :currentScenario="form" ref="tag" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="10" :offset="1">
            <el-form-item
              :label="$t('test_track.plan.plan_principal')"
              :label-width="formLabelWidth"
              prop="principal"
            >
              <el-select
                v-model="form.principal"
                :placeholder="$t('test_track.plan.input_plan_principal')"
                filterable
              >
                <el-option
                  v-for="item in principalOptions"
                  :key="item.id"
                  :label="item.name + '(' + item.id + ')'"
                  :value="item.id"
                >
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item
              :label="$t('test_track.plan.plan_stage')"
              :label-width="formLabelWidth"
              prop="stage"
            >
              <el-select
                v-model="form.stage"
                clearable
                :placeholder="$t('test_track.plan.input_plan_stage')"
              >
                <el-option
                  :label="$t('test_track.plan.smoke_test')"
                  value="smoke"
                ></el-option>
                <el-option
                  :label="$t('test_track.plan.system_test')"
                  value="system"
                ></el-option>
                <el-option
                  :label="$t('test_track.plan.regression_test')"
                  value="regression"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <!--start:xuxm增加自定义‘计划开始’，‘计划结束’时间字段-->
        <el-row>
          <el-col :span="8" :offset="1">
            <el-form-item
              :label="$t('test_track.plan.planned_start_time')"
              :label-width="formLabelWidth"
              prop="plannedStartTime"
            >
              <el-date-picker
                :placeholder="$t('test_track.plan.planned_start_time')"
                v-model="form.plannedStartTime"
                type="datetime"
                value-format="timestamp"
              ></el-date-picker>
            </el-form-item>
          </el-col>

          <el-col :span="11" :offset="2">
            <el-form-item
              :label="$t('test_track.plan.planned_end_time')"
              :label-width="formLabelWidth"
              prop="plannedEndTime"
            >
              <el-date-picker
                :placeholder="$t('test_track.plan.planned_end_time')"
                v-model="form.plannedEndTime"
                type="datetime"
                value-format="timestamp"
              ></el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <!--end:xuxm增加自定义‘计划开始’，‘计划结束’时间字段-->

        <el-row>
          <el-col :span="8" :offset="1">
            <el-form-item
              :label="$t('自动更新状态')"
              :label-width="formLabelWidth"
              prop="automaticStatusUpdate"
            >
              <el-switch v-model="form.automaticStatusUpdate" />
              <ms-instructions-icon
                :content="'当功能用例关联的接口或性能用例在测试计划执行后，自动更新功能用例的状态'"
              />
            </el-form-item>
          </el-col>

          <!-- 所属迭代 -->
          <el-col :span="8" :push="2">
            <el-form-item :label="$t('所属迭代')" :label-width="formLabelWidth">
              <el-select
                v-model="form.iterationId"
                clearable
                :disabled="isDisable"
                placeholder="请选择迭代"
                @clear="getIterationOptions('')"
                @blur="handleBlur"
                filterable
                :filter-method="getIterationOptions"
                @change="handleIterationChange"
              >
                <el-option
                  v-for="(item, index) in iterationOptions"
                  :key="index"
                  :label="item.label"
                  :value="item.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="10" :offset="1">
            <el-form-item
              :label="$t('环境')"
              :label-width="formLabelWidth"
              prop="environment"
            >
              <el-select
                v-model="form.environment"
                clearable
                placeholder="请选择环境"
              >
                <el-option
                  v-for="(item, index) in environmentOptions"
                  :key="index"
                  :label="item.label"
                  :value="item.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="13">
            <el-form-item
              :label="$t('计划继承')"
              :label-width="formLabelWidth"
              prop="testPlanInherit"
            >
              <el-select
                v-model="form.testPlanInherit"
                :disabled="isDisable"
                clearable
                placeholder="选择迭代中计划"
                @clear="resetRetain"
              >
                <el-option
                  v-for="(item, index) in planInheritOptions"
                  :key="index"
                  :label="item.label"
                  :value="item.value"
                ></el-option>
              </el-select>
              <ms-instructions-icon content="选择继承则复制该计划" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <!-- 所属需求 -->
          <el-col :span="10" :offset="1">
            <el-form-item label="所属需求" :label-width="formLabelWidth">
              <el-select
                v-model="form.requirementId"
                clearable
                :disabled="isDisable"
                placeholder="请选择需求"
                filterable
              >
                <el-option
                  v-for="(item, index) in requirementOptions"
                  :key="index"
                  :label="item.label"
                  :value="item.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8" v-if="form.testPlanInherit !== ''">
            <el-form-item
              label="保留用例状态"
              :label-width="formLabelWidth"
              prop="ifRetain"
            >
              <el-switch v-model="form.ifRetain" :disabled="isDisable" />
              <ms-instructions-icon
                content='选择是否保留用例"阻塞"状态,重置其它用例状态'
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row type="flex" justify="left" style="margin-top: 10px">
          <el-col :span="23" :offset="1">
            <el-form-item
              :label="$t('commons.description')"
              :label-width="formLabelWidth"
              prop="description"
            >
              <el-input
                v-model="form.description"
                type="textarea"
                :autosize="{ minRows: 2, maxRows: 4 }"
                :rows="2"
                :placeholder="$t('commons.input_content')"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row
          v-if="operationType === 'edit'"
          type="flex"
          justify="left"
          style="margin-top: 10px"
        >
          <el-col :span="19" :offset="1">
            <el-form-item
              :label="$t('test_track.plan.plan_status')"
              :label-width="formLabelWidth"
              prop="status"
            >
              <test-plan-status-button
                :status="form.status"
                @statusChange="statusChange"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <template v-slot:footer>
        <div class="dialog-footer">
          <el-button @click="dialogFormVisible = false">
            {{ $t("test_track.cancel") }}
          </el-button>
          <el-button type="primary" @click="savePlan">
            {{ $t("test_track.confirm") }}
          </el-button>
          <el-button type="primary" @click="testPlanInfo">
            {{ $t("test_track.planning_execution") }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import TestPlanStatusButton from "../common/TestPlanStatusButton";
import {
  getCurrentProjectID,
  getCurrentWorkspaceId,
  listenGoBack,
  removeGoBackListener,
  getCurrentUserId,
} from "@/common/js/utils";
import MsInputTag from "@/business/components/api/automation/scenario/MsInputTag";
import MsInstructionsIcon from "@/business/components/common/components/MsInstructionsIcon";

export default {
  name: "TestPlanEdit",
  components: { MsInstructionsIcon, TestPlanStatusButton, MsInputTag },
  data() {
    return {
      isStepTableAlive: true,
      dialogFormVisible: false,
      form: {
        name: "",
        projectIds: [],
        principal: "",
        stage: "",
        description: "",
        plannedStartTime: "",
        plannedEndTime: "",
        automaticStatusUpdate: false,
        iterationId: "",
        environment: "",
        testPlanInherit: "",
        ifRetain: false,
        requirementId: "",
        projectId: getCurrentProjectID(),
        creator: getCurrentUserId(),
      },
      rules: {
        name: [
          {
            required: true,
            message: this.$t("test_track.plan.input_plan_name"),
            trigger: "blur",
          },
          {
            max: 30,
            message: this.$t("test_track.length_less_than") + "30",
            trigger: "blur",
          },
        ],
        principal: [
          {
            required: true,
            message: this.$t("test_track.plan.input_plan_principal"),
            trigger: "change",
          },
        ],
        stage: [
          {
            required: true,
            message: this.$t("test_track.plan.input_plan_stage"),
            trigger: "change",
          },
        ],
        description: [
          {
            max: 200,
            message: this.$t("test_track.length_less_than") + "200",
            trigger: "blur",
          },
        ],
        environment: [
          { required: true, message: "请选择环境", trigger: "blur" },
        ],
      },
      formLabelWidth: "100px",
      operationType: "",
      principalOptions: [],
      iterationOptions: [],
      requirementOptions: [],
      environmentOptions: [
        { label: "开发环境", value: "dev" },
        { label: "测试环境", value: "test" },
        { label: "预发环境", value: "uat" },
        { label: "线上环境", value: "pord" },
      ],
      planInheritOptions: [],
      isDisable: false,
    };
  },
  watch: {
    "form.iterationId": {
      handler() {
        this.form.requirementId = this.form.testPlanInherit = "";
        this.requirementOptions = this.planInheritOptions = [];
        this.form.ifRetain = false;
      },
    },
  },
  created() {
    //设置“测试阶段”和“负责人”的默认值
    this.form.stage = "smoke";
    const adminToken = JSON.parse(localStorage.getItem("Admin-Token"));
    this.form.principal = adminToken.id;
    this.getIterationOptions();
  },
  methods: {
    reload() {
      this.isStepTableAlive = false;
      this.$nextTick(() => (this.isStepTableAlive = true));
    },
    openTestPlanEditDialog(testPlan) {
      this.resetForm();
      this.setPrincipalOptions();
      this.operationType = "add";
      this.isDisable = !!testPlan?.isEdit;
      if (testPlan) {
        //修改
        this.operationType = "edit";
        let tmp = {};
        Object.assign(tmp, testPlan);
        Object.assign(this.form, tmp);
      } else {
        this.form.tags = [];
      }
      listenGoBack(this.close);
      this.dialogFormVisible = true;
      this.reload();
    },
    testPlanInfo() {
      this.$refs["planFrom"].validate((valid) => {
        if (valid) {
          let param = {};
          Object.assign(param, this.form);
          param.name = param.name.trim();
          if (param.name === "") {
            this.$warning(this.$t("test_track.plan.input_plan_name"));
            return;
          }
          param.workspaceId = getCurrentWorkspaceId();
          if (this.form.tags instanceof Array) {
            this.form.tags = JSON.stringify(this.form.tags);
          }
          param.tags = this.form.tags;
          this.$post("/test/plan/" + this.operationType, param, (response) => {
            this.$success(this.$t("commons.save_success"));
            this.dialogFormVisible = false;
            this.$router.push("/track/plan/view/" + response.data);
          });
        } else {
          return false;
        }
      });
    },
    savePlan() {
      this.$refs["planFrom"].validate((valid) => {
        if (valid) {
          let param = {};
          Object.assign(param, this.form);
          param.name = param.name.trim();
          if (param.name === "") {
            this.$warning(this.$t("test_track.plan.input_plan_name"));
            return;
          }
          param.workspaceId = getCurrentWorkspaceId();
          if (this.form.tags instanceof Array) {
            this.form.tags = JSON.stringify(this.form.tags);
          }
          param.tags = this.form.tags;
          this.$post("/test/plan/" + this.operationType, param, () => {
            this.$success(this.$t("commons.save_success"));
            this.dialogFormVisible = false;
            this.$emit("refresh");
          });
        } else {
          return false;
        }
      });
    },
    validate(param) {
      if (param.name === "") {
        this.$warning(this.$t("test_track.plan.input_plan_name"));
        return false;
      }
      if (param.plannedStartTime > param.plannedEndTime) {
        this.$warning(this.$t("commons.date.data_time_error"));
        return false;
      }
      return true;
    },
    setPrincipalOptions() {
      this.$post(
        "/user/project/member/tester/list",
        { projectId: getCurrentProjectID() },
        (response) => {
          this.principalOptions = response.data;
        }
      );
    },
    statusChange(status) {
      this.form.status = status;
      this.$forceUpdate();
    },
    close() {
      removeGoBackListener(this.close);
      this.dialogFormVisible = false;
    },
    resetForm() {
      //防止点击修改后，点击新建触发校验
      if (this.$refs["planFrom"]) {
        this.$refs["planFrom"].validate(() => {
          this.$refs["planFrom"].resetFields();
          this.form.name = "";
          this.form.projectIds = [];
          const adminToken = JSON.parse(localStorage.getItem("Admin-Token"));
          this.form.principal = adminToken.id;
          this.form.stage = "smoke";
          this.form.description = "";
          this.form.status = null;
          this.form.plannedStartTime = null;
          this.form.plannedEndTime = null;
          return true;
        });
      }
    },
    // 获取迭代列表
    getIterationOptions(name = "") {
      const url = "/iteration/list/1/30";
      this.form.iterationId = name;
      this.$post(
        url,
        { projectId: getCurrentProjectID(), name },
        (response) => {
          let tempArr = response?.data?.listObject || [];
          const iterationOptions = [];
          tempArr.forEach((item) => {
            iterationOptions.push({
              label: item.name,
              value: item.id,
            });
          });
          this.iterationOptions = iterationOptions;
        }
      );
    },
    // 获取计划继承列表
    getPlanInheritOptions() {
      const url = `/test/plan/iteration/plan`;
      this.planInheritOptions = [];
      this.form.testPlanInherit = "";
      this.form.ifRetain = false;
      const { iterationId } = this.form;
      this.$post(
        url,
        { iterationId, projectId: getCurrentProjectID() },
        (response) => {
          let tempArr = response?.data || [];
          const planInheritOptions = [];
          tempArr.forEach((item) => {
            planInheritOptions.push({
              label: item.name,
              value: item.id,
            });
          });
          this.planInheritOptions = planInheritOptions;
        }
      );
    },
    // 获取需求列表
    getRequirementOptions() {
      const url = "/iteration/requirement/1/100";
      const { iterationId } = this.form;
      this.$post(
        url,
        { projectId: getCurrentProjectID(), iterationId },
        (response) => {
          let tempArr = response?.data?.listObject || [];
          const requirementOptions = [];
          tempArr.forEach((item) => {
            requirementOptions.push({
              label: item.name,
              value: item.id,
            });
          });
          this.requirementOptions = requirementOptions;
        }
      );
    },
    // 重置保留缺陷状态
    resetRetain() {
      this.form.ifRetain = false;
    },
    handleIterationChange() {
      this.getPlanInheritOptions();
      this.getRequirementOptions();
    },
    handleBlur() {
      const keywords = this.form.iterationId;
      const obj = this.iterationOptions.find(
        (item) => item.label === keywords || item.value === keywords
      );
      if (obj !== undefined) {
        this.form.iterationId = obj.value;
      } else {
        this.form.iterationId = "";
        this.getIterationOptions("");
      }
    },
  },
};
</script>

<style scoped>
.instructions-icon {
  margin-left: 10px;
}
</style>
