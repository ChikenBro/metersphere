<template>
  <ms-test-plan-common-component>
    <template v-slot:aside>
      <ms-node-tree
        ref="nodeTree"
        v-loading="result.loading"
        class="node-tree"
        :all-label="$t('commons.all_label.review')"
        :tree-nodes="treeNodes"
        @nodeSelectEvent="nodeChange"
      />
    </template>
    <template v-slot:main>
      <ms-tab-button
        :active-dom.sync="activeDom"
        :left-tip="$t('test_track.case.list')"
        :left-content="$t('test_track.case.list')"
        :right-tip="$t('test_track.case.minder')"
        :right-content="$t('test_track.case.minder')"
        :middle-button-enable="false"
      >
        <test-review-test-case-list
          v-if="activeDom === 'left'"
          ref="testPlanTestCaseList"
          class="table-list"
          :review-id="reviewId"
          :click-type="clickType"
          @openTestReviewRelevanceDialog="openTestReviewRelevanceDialog"
          @refresh="refresh"
        />
        <test-review-minder
          v-if="activeDom === 'right'"
          :tree-nodes="treeNodes"
          :project-id="projectId"
          :review-id="reviewId"
        />
      </ms-tab-button>
    </template>
    <test-review-relevance
      ref="testReviewRelevance"
      :review-id="reviewId"
      @refresh="refresh"
    />
  </ms-test-plan-common-component>
</template>

<script>
import MsTestPlanCommonComponent from "@/business/components/track/plan/view/comonents/base/TestPlanCommonComponent";
import MsNodeTree from "@/business/components/track/common/NodeTree";
import TestReviewRelevance from "@/business/components/track/review/view/components/TestReviewRelevance";
import TestReviewTestCaseList from "@/business/components/track/review/view/components/TestReviewTestCaseList";
import MsTabButton from "@/business/components/common/components/MsTabButton";
import TestReviewMinder from "@/business/components/track/common/minder/TestReviewMinder";
import { getCurrentProjectID } from "@/common/js/utils";

export default {
  name: "TestReviewFunction",
  components: {
    TestReviewMinder,
    MsTabButton,
    TestReviewTestCaseList,
    TestReviewRelevance,
    MsNodeTree,
    MsTestPlanCommonComponent,
  },
  props: ["reviewId", "redirectCharType", "clickType"],
  data() {
    return {
      result: {},
      testReviews: [],
      currentReview: {},
      // selectNodeIds: [],
      // selectParentNodes: [],
      treeNodes: [],
      isMenuShow: true,
      activeDom: "left",
    };
  },
  computed: {
    projectId() {
      return getCurrentProjectID();
    },
  },
  mounted() {
    this.getNodeTreeByReviewId();
  },
  activated() {
    this.getNodeTreeByReviewId();
  },
  methods: {
    refresh() {
      this.$store.commit("setTestReviewSelectNode", {});
      this.$store.commit("setTestReviewSelectNodeIds", []);
      this.$refs.testReviewRelevance.search();
      this.getNodeTreeByReviewId();
    },
    nodeChange(node, nodeIds, pNodes) {
      this.$store.commit("setTestReviewSelectNode", node);
      this.$store.commit("setTestReviewSelectNodeIds", nodeIds);
    },
    getNodeTreeByReviewId() {
      if (this.reviewId) {
        this.result = this.$get(
          "/case/node/list/review/" + this.reviewId,
          (response) => {
            this.treeNodes = response.data;
          }
        );
      }
    },
    openTestReviewRelevanceDialog() {
      this.$refs.testReviewRelevance.openTestReviewRelevanceDialog();
    },
  },
};
</script>

<style scoped>
/deep/ .el-button-group > .el-button:first-child {
  padding: 4px 1px !important;
}
</style>
