<template>
  <el-drawer
    ref="drawer"
    class="field-template-edit"
    :before-close="handleClose"
    :visible.sync="visible"
    :with-header="false"
    size="100%"
    :modal-append-to-body="false"
  >
    <template v-slot:default="scope">
      <template-component-edit-header
        :show-edit="false"
        :template="{}"
        prop="title"
        :showCreateNext="showCreateNext"
        @cancel="handleClose"
        @save="save"
        @createNext="handleCreateNext"
        :title="title"
        :isCreateTitle="isCreateTitle"
        :isDisabled="isDisabled"
      />
      <issue-edit-detail
        @refresh="$emit('refresh')"
        @close="handleClose"
        @openNewDrawer="openNewDrawer"
        ref="issueEditDetail"
        :isDisabled="isDisabled"
        :is-edit="isEdit"
        :iteration-list="iterationOptions"
        :requirement-list="requirementOptions"
        :defect-list="defectTypeOptions"
      />
    </template>
  </el-drawer>
</template>
<script>
import TemplateComponentEditHeader from "@/business/components/track/plan/view/comonents/report/TemplateComponentEditHeader";
import IssueEditDetail from "@/business/components/track/issue/IssueEditDetail";
export default {
  name: "IssueEdit",
  components: { IssueEditDetail, TemplateComponentEditHeader },
  data() {
    return {
      visible: false,
      showCreateNext: true,
      isCreateTitle: false,
      title: "",
      isDisabled: false,
      isEdit: false,
    };
  },
  props: {
    iterationOptions: {
      type: Array,
      default() {
        return [];
      },
    },
    requirementOptions: {
      type: Array,
      default() {
        return [];
      },
    },
    defectTypeOptions: {
      type: Array,
      default() {
        return [];
      },
    },
  },
  methods: {
    open(data) {
      this.visible = true;
      this.showCreateNext = data === undefined;
      this.isCreateTitle = data === undefined || data?.isCreateTitle;
      this.title =
        data?.drawerTitle || this.$t("test_track.issue.create_issue");
      this.isDisabled = !!data?.isOnlyRead;
      this.isEdit = !!data?.isEdit;
      this.$nextTick(() => {
        this.$refs.issueEditDetail.open(data);
        this.$refs.issueEditDetail.$el.scrollTop = 0;
      });
    },
    handleClose() {
      this.visible = false;
    },
    save() {
      this.$refs.issueEditDetail.save();
    },
    // 保存并清空
    handleCreateNext() {
      this.$refs.issueEditDetail.save("reCreate");
    },
    openNewDrawer() {
      this.$refs.issueEditDetail.open();
    },
  },
};
</script>

<style scoped></style>
