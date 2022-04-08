<template>
  <ms-test-plan-common-component>
    <template v-slot:aside>
      <node-tree
        ref="nodeTree"
        v-loading="result.loading"
        class="node-tree"
        :tree-nodes="treeNodes"
        @nodeSelectEvent="nodeChange"
      />
    </template>
    <template v-slot:main>
      <test-plan-load-case-list
        ref="testPlanLoadCaseList"
        class="table-list"
        :review-id="reviewId"
        :click-type="clickType"
        :select-project-id="selectProjectId"
        :select-parent-nodes="selectParentNodes"
        @refresh="refresh"
        @relevanceCase="openTestCaseRelevanceDialog"
      />
    </template>
    <test-case-load-relevance
      ref="testCaseLoadRelevance"
      :review-id="reviewId"
      @refresh="refresh"
    />
  </ms-test-plan-common-component>
</template>

<script>
import MsTestPlanCommonComponent from "@/business/components/track/plan/view/comonents/base/TestPlanCommonComponent";
import NodeTree from "@/business/components/track/common/NodeTree";
import TestPlanLoadCaseList from "@/business/components/track/plan/view/comonents/load/TestPlanLoadCaseList";
import TestCaseLoadRelevance from "@/business/components/track/plan/view/comonents/load/TestCaseLoadRelevance";
export default {
  name: "TestReviewLoad",
  components: {
    MsTestPlanCommonComponent,
    NodeTree,
    TestPlanLoadCaseList,
    TestCaseLoadRelevance,
  },
  props: ["reviewId", "redirectCharType", "clickType"],
  data() {
    return {
      result: {},
      selectNodeIds: [],
      selectParentNodes: [],
      selectProjectId: "",
      treeNodes: [],
    };
  },
  watch: {
    planId() {
      this.initData();
    },
  },
  mounted() {
    this.initData();
  },
  methods: {
    refresh() {
      this.selectProjectId = "";
      this.selectParentNodes = [];
      this.$refs.testPlanLoadCaseList.initTable();
      this.getNodeTreeByPlanId();
    },
    initData() {
      this.getNodeTreeByPlanId();
    },
    openTestCaseRelevanceDialog() {
      this.$refs.testCaseLoadRelevance.open();
    },
    nodeChange(node, nodeIds, pNodes) {
      this.selectProjectId = node.key;
      // 切换node后，重置分页数
      this.$refs.testPlanLoadCaseList.currentPage = 1;
      this.$refs.testPlanLoadCaseList.pageSize = 10;
    },
    getNodeTreeByPlanId() {
      if (this.planId) {
        this.result = this.$get(
          "/case/node/list/plan/" + this.planId,
          (response) => {
            this.treeNodes = response.data;
            // 性能测试与模块无关，过滤项目下模块
            this.treeNodes.map((node) => (node.children = null));
          }
        );
      }
    },
  },
};
</script>

<style scoped></style>
