<template>
  <el-dialog
    v-loading="result.loading"
    :title="title"
    :visible.sync="dialogVisible"
    width="75%"
    :close-on-click-modal="false"
    top="50px"
    append-to-body
    @close="close"
  >
    <el-container class="main-content">
      <el-aside class="tree-aside" width="250px">
        <slot name="aside"></slot>
      </el-aside>

      <el-container>
        <el-main class="case-content">
          <slot></slot>
        </el-main>
      </el-container>
    </el-container>

    <template v-slot:footer>
      <slot name="footer"></slot>
    </template>
  </el-dialog>
</template>

<script>
import MsDialogFooter from "../../../../../common/components/MsDialogFooter";
import SelectMenu from "../../../../common/SelectMenu";

export default {
  name: "RelevanceDialog",
  components: {
    SelectMenu,
    MsDialogFooter,
  },
  props: ["title"],
  data() {
    return {
      result: {},
      dialogVisible: false,
    };
  },
  methods: {
    open() {
      this.dialogVisible = true;
    },
    close() {
      this.dialogVisible = false;
    },
  },
};
</script>

<style scoped>
.el-dialog {
  min-height: 600px;
}

.tree-aside {
  max-height: 600px;
}

.el-dialog >>> .el-dialog__body {
  padding: 10px 20px;
}
</style>
