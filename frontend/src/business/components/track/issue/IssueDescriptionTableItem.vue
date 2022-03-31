<template>
  <ms-table-column
    :label="$t('test_track.issue.description')"
    prop="description"
    :field="field"
    :fields-width="fieldsWidth"
  >
    <template v-slot:default="scope">
      <el-popover
        placement="right"
        width="500"
        trigger="hover"
        popper-class="issues-popover"
      >
        <mavon-editor
          ref="md"
          v-model="scope.row.description"
          :editable="false"
          default-open="preview"
          class="mavon-editor"
          :subfield="false"
          :toolbars-flag="false"
        />
        <el-button slot="reference" type="text">{{
          $t("test_track.issue.preview")
        }}</el-button>
      </el-popover>
    </template>
  </ms-table-column>
</template>

<script>
import MsTableColumn from "@/business/components/common/components/table/MsTableColumn";
export default {
  name: "IssueDescriptionTableItem",
  components: { MsTableColumn },
  props: {
    field: Object,
    fieldsWidth: Object,
  },
  data() {
    return {
      readConfig: { toolbar: [] },
    };
  },
};
</script>

<style scoped></style>
