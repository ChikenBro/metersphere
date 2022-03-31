<template>
  <ms-test-plan-common-component>
    <template v-slot:aside>
      <ms-api-module
        v-if="model === 'api'"
        ref="apiNodeTree"
        :review-id="reviewId"
        :is-read-only="true"
        :redirect-char-type="redirectCharType"
        @nodeSelectEvent="nodeChange"
        @protocolChange="handleProtocolChange"
        @refreshTable="refreshTable"
        @setModuleOptions="setModuleOptions"
      >
        <template v-slot:header>
          <div class="model-change-radio">
            <el-radio v-model="model" label="api">接口用例</el-radio>
            <el-radio v-model="model" label="scenario">场景用例</el-radio>
          </div>
        </template>
      </ms-api-module>

      <ms-api-scenario-module
        v-if="model === 'scenario'"
        ref="scenarioNodeTree"
        :is-read-only="true"
        :review-id="reviewId"
        @nodeSelectEvent="nodeChange"
        @refreshTable="refreshTable"
        @setModuleOptions="setModuleOptions"
      >
        <template v-slot:header>
          <div class="model-change-radio">
            <el-radio v-model="model" label="api">接口用例</el-radio>
            <el-radio v-model="model" label="scenario">场景用例</el-radio>
          </div>
        </template>
      </ms-api-scenario-module>
    </template>
    <template v-slot:main>
      <test-plan-api-case-list
        v-if="model === 'api'"
        ref="apiCaseList"
        :current-protocol="currentProtocol"
        :current-row="currentRow"
        :select-node-ids="selectNodeIds"
        :trash-enable="trashEnable"
        :is-case-relevance="true"
        :model="'plan'"
        :review-id="reviewId"
        :click-type="clickType"
        @refresh="refreshTree"
        @relevanceCase="openTestCaseRelevanceDialog"
      />

      <ms-test-plan-api-scenario-list
        v-if="model === 'scenario'"
        ref="apiScenarioList"
        :select-node-ids="selectNodeIds"
        :trash-enable="trashEnable"
        :review-id="reviewId"
        :click-type="clickType"
        @refresh="refreshTree"
        @relevanceCase="openTestCaseRelevanceDialog"
      />
    </template>
    <test-review-relevance-api
      ref="apiCaseRelevance"
      :review-id="reviewId"
      :model="model"
      @refresh="refresh"
    />
    <test-review-relevance-scenario
      ref="scenarioCaseRelevance"
      :review-id="reviewId"
      :model="model"
      @refresh="refresh"
    />
  </ms-test-plan-common-component>
</template>

<script>
import MsTestPlanCommonComponent from "@/business/components/track/plan/view/comonents/base/TestPlanCommonComponent";
import MsTestPlanApiScenarioList from "@/business/components/track/plan/view/comonents/api/TestPlanApiScenarioList";
import MsApiScenarioModule from "@/business/components/api/automation/scenario/ApiScenarioModule";
import ApiCaseSimpleList from "@/business/components/api/definition/components/list/ApiCaseSimpleList";
import TestPlanApiCaseList from "@/business/components/track/plan/view/comonents/api/TestPlanApiCaseList";
import TestCaseRelevance from "@/business/components/track/plan/view/comonents/functional/TestCaseFunctionalRelevance";
import NodeTree from "@/business/components/track/common/NodeTree";
import MsApiModule from "../../../../api/definition/components/module/ApiModule";
import TestReviewRelevanceApi from "@/business/components/track/review/view/components/TestReviewRelevanceApi";
import TestReviewRelevanceScenario from "@/business/components/track/review/view/components/TestReviewRelevanceScenario";

export default {
  name: "TestReviewApi",
  components: {
    TestReviewRelevanceScenario,
    TestReviewRelevanceApi,
    MsTestPlanApiScenarioList,
    MsApiScenarioModule,
    ApiCaseSimpleList,
    TestPlanApiCaseList,
    MsTestPlanCommonComponent,
    TestCaseRelevance,
    NodeTree,
    MsApiModule,
  },
  props: ["reviewId", "redirectCharType", "clickType"],
  data() {
    return {
      result: {},
      treeNodes: [],
      currentRow: "",
      trashEnable: false,
      currentProtocol: null,
      currentModule: null,
      selectNodeIds: [],
      moduleOptions: {},
      model: "api",
    };
  },
  watch: {
    model() {
      this.selectNodeIds = [];
      this.moduleOptions = {};
    },
    redirectCharType() {
      if (this.redirectCharType == "scenario") {
        this.model = "scenario";
      } else {
        this.model = "api";
      }
    },
  },
  mounted() {
    this.checkRedirectCharType();
  },
  methods: {
    checkRedirectCharType() {
      if (this.redirectCharType == "scenario") {
        this.model = "scenario";
      } else {
        this.model = "api";
      }
    },
    refresh() {
      this.refreshTree();
      this.refreshTable();
    },
    refreshTable() {
      if (this.$refs.apiCaseList) {
        this.$refs.apiCaseList.initTable();
      }
      if (this.$refs.apiScenarioList) {
        this.$refs.apiScenarioList.search();
      }
    },
    refreshTree() {
      if (this.$refs.apiNodeTree) {
        this.$refs.apiNodeTree.list();
      }
      if (this.$refs.scenarioNodeTree) {
        this.$refs.scenarioNodeTree.list();
      }
    },

    nodeChange(node, nodeIds, pNodes) {
      this.selectNodeIds = nodeIds;
    },
    handleProtocolChange(protocol) {
      this.currentProtocol = protocol;
    },
    setModuleOptions(data) {
      this.moduleOptions = data;
    },

    openTestCaseRelevanceDialog(model) {
      if (model === "scenario") {
        this.$refs.scenarioCaseRelevance.open();
      } else {
        this.$refs.apiCaseRelevance.open();
      }
    },
  },
};
</script>

<style scoped>
.model-change-radio {
  height: 25px;
  line-height: 25px;
  margin: 5px 10px;
}
</style>
