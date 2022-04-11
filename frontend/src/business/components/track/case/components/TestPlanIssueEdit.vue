<template>
  <ms-edit-dialog
    ref="msEditDialog"
    width="60%"
    :visible.sync="visible"
    :title="$t('test_track.issue.create_issue')"
    append-to-body
    @confirm="confirm"
  >
    <template v-slot:default="scope">
      <issue-edit-detail
        ref="issueEditDetail"
        :plan-id="planId"
        :case-id="caseId"
        :is-plan="true"
        @refresh="$emit('refresh')"
        @close="handleClose"
      />
    </template>
  </ms-edit-dialog>
</template>
<script>
import TemplateComponentEditHeader from "@/business/components/track/plan/view/comonents/report/TemplateComponentEditHeader";
import IssueEditDetail from "@/business/components/track/issue/IssueEditDetail";
import MsEditDialog from "@/business/components/common/components/MsEditDialog";
import { getCurrentProjectID } from "@/common/js/utils";
export default {
  name: "TestPlanIssueEdit",
  components: { MsEditDialog, IssueEditDetail, TemplateComponentEditHeader },
  props: ["caseId", "planId"],
  data() {
    return {
      visible: false,
    };
  },
  computed: {
    projectId() {
      return getCurrentProjectID();
    },
  },
  methods: {
    open(data) {
      this.visible = true;
      this.$nextTick(() => {
        this.$refs.issueEditDetail.open(data);
      });
    },
    handleClose() {
      this.visible = false;
    },
    confirm() {
      this.$refs.issueEditDetail.save();
    },
  },
};
</script>

<style scoped></style>
