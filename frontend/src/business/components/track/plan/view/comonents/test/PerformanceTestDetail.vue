<template>
  <ms-container>
    <ms-main-container>
      <el-card>
        <!--<el-card v-loading="result.loading">-->
        <el-row>
          <el-col :span="10">
            <el-input
              v-model="test.name"
              :disabled="true"
              :placeholder="$t('load_test.input_name')"
              class="input-with-select"
            >
              <template v-slot:prepend>
                <el-select
                  slot="prepend"
                  v-model="project.name"
                  :disabled="true"
                  :placeholder="$t('load_test.select_project')"
                >
                </el-select>
              </template>
            </el-input>
          </el-col>
          <el-col :span="12" :offset="2">
            <el-button
              :disabled="isReadOnly"
              type="primary"
              plain
              @click="runTest"
              >执行</el-button
            >
          </el-col>
        </el-row>

        <el-tabs v-model="active" class="test-config">
          <el-tab-pane :label="$t('load_test.basic_config')">
            <performance-basic-config
              ref="basicConfig"
              :is-read-only="true"
              :test="test"
            />
          </el-tab-pane>
          <el-tab-pane :label="$t('load_test.pressure_config')">
            <performance-pressure-config
              ref="pressureConfig"
              :is-read-only="true"
              :test="test"
              :test-id="id"
            />
          </el-tab-pane>
          <el-tab-pane
            :label="$t('load_test.advanced_config')"
            class="advanced-config"
          >
            <performance-advanced-config
              ref="advancedConfig"
              :read-only="true"
              :test-id="id"
            />
          </el-tab-pane>
        </el-tabs>
      </el-card>
    </ms-main-container>
  </ms-container>
</template>

<script>
import MsContainer from "../../../../../common/components/MsContainer";
import MsMainContainer from "../../../../../common/components/MsMainContainer";
import PerformanceBasicConfig from "../../../../../performance/test/components/PerformanceBasicConfig";
import PerformancePressureConfig from "../../../../../performance/test/components/PerformancePressureConfig";
import PerformanceAdvancedConfig from "../../../../../performance/test/components/PerformanceAdvancedConfig";

export default {
  name: "PerformanceTestDetail",
  components: {
    PerformanceAdvancedConfig,
    PerformancePressureConfig,
    PerformanceBasicConfig,
    MsMainContainer,
    MsContainer,
  },
  props: {
    id: String,
    isReadOnly: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      result: {},
      test: {},
      savePath: "/performance/save",
      editPath: "/performance/edit",
      runPath: "/performance/run",
      project: {},
      projectId: "",
      active: "0",
      tabs: [
        {
          title: this.$t("load_test.basic_config"),
          id: "0",
          component: "PerformanceBasicConfig",
        },
        {
          title: this.$t("load_test.pressure_config"),
          id: "1",
          component: "PerformancePressureConfig",
        },
        {
          title: this.$t("load_test.advanced_config"),
          id: "2",
          component: "PerformanceAdvancedConfig",
        },
      ],
    };
  },
  methods: {
    init() {
      this.getTest();
    },
    getProject(projectId) {
      this.$get("/project/get/" + projectId, (response) => {
        this.project = response.data;
      });
    },
    getTest() {
      if (this.id) {
        this.result = this.$get("/performance/get/" + this.id, (response) => {
          if (response.data) {
            this.test = response.data;
            this.getProject(this.test.projectId);
          } else {
            this.test = {};
          }
        });
      }
    },
    runTest() {
      this.result = this.$post(
        this.runPath,
        { id: this.test.id, triggerMode: "MANUAL" },
        (response) => {
          this.$success(this.$t("load_test.is_running"));
          this.$emit("runTest", response.data);
        }
      );
    },
  },
};
</script>

<style scoped>
.test-config {
  margin-top: 15px;
  text-align: center;
}

.el-select {
  min-width: 130px;
}

.edit-test-container .input-with-select .el-input-group__prepend {
  background-color: #fff;
}

.advanced-config {
  height: calc(100vh - 280px);
  overflow: auto;
}
</style>
