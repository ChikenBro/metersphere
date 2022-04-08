<template>
  <ms-test-plan-common-component>
    <template v-slot:aside>
      <ms-api-module
        v-if="model === 'api'"
        ref="apiNodeTree"
        :plan-id="planId"
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
        :plan-id="planId"
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
      <!--测试用例列表-->
      <test-plan-api-case-list
        v-if="model === 'api'"
        ref="apiCaseList"
        :current-protocol="currentProtocol"
        :current-row="currentRow"
        :select-node-ids="selectNodeIds"
        :trash-enable="trashEnable"
        :is-case-relevance="true"
        :model="'plan'"
        :plan-id="planId"
        :click-type="clickType"
        @refresh="refreshTree"
        @relevanceCase="openTestCaseRelevanceDialog"
      />

      <ms-test-plan-api-scenario-list
        v-if="model === 'scenario'"
        ref="apiScenarioList"
        :select-node-ids="selectNodeIds"
        :trash-enable="trashEnable"
        :plan-id="planId"
        :click-type="clickType"
        @refresh="refreshTree"
        @relevanceCase="openTestCaseRelevanceDialog"
      />
    </template>

    <test-case-api-relevance
      ref="apiCaseRelevance"
      :plan-id="planId"
      :model="model"
      @refresh="refresh"
    />

    <test-case-scenario-relevance
      ref="scenarioCaseRelevance"
      :plan-id="planId"
      :model="model"
      @refresh="refresh"
    />
  </ms-test-plan-common-component>
</template>

<script>
import NodeTree from "../../../../common/NodeTree";
import TestCaseRelevance from "../functional/TestCaseFunctionalRelevance";
import MsTestPlanCommonComponent from "../base/TestPlanCommonComponent";
import TestPlanApiCaseList from "./TestPlanApiCaseList";
import TestCaseApiRelevance from "./TestCaseApiRelevance";
import ApiCaseSimpleList from "../../../../../api/definition/components/list/ApiCaseSimpleList";
import MsApiModule from "../../../../../api/definition/components/module/ApiModule";
import MsApiScenarioModule from "../../../../../api/automation/scenario/ApiScenarioModule";
import MsTestPlanApiScenarioList from "./TestPlanApiScenarioList";
import TestCaseScenarioRelevance from "./TestCaseScenarioRelevance";

export default {
  name: "TestPlanApi",
  components: {
    TestCaseScenarioRelevance,
    MsTestPlanApiScenarioList,
    MsApiScenarioModule,
    MsApiModule,
    ApiCaseSimpleList,
    TestCaseApiRelevance,
    TestPlanApiCaseList,
    MsTestPlanCommonComponent,
    TestCaseRelevance,
    NodeTree,
  },
  props: ["planId", "redirectCharType", "clickType"],
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
