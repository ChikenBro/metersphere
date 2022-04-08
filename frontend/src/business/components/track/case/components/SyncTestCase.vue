<template>
  <el-dialog
    :close-on-click-modal="false"
    :title="$t('test_track.sync_jira_test_case')"
    :visible.sync="visible"
    width="45%"
    :destroy-on-close="true"
  >
    <el-row :gutter="10">
      <el-col :span="12">
        <div class="node-wrap">
          <p class="node-header">
            <label>本地用例</label>
          </p>
          <node-tree
            ref="msNodeTree"
            v-loading="result.loading"
            class="node-tree"
            :is-display="openType"
            :tree-nodes="treeNodes"
            @nodeSelectEvent="nodeChange"
          />
        </div>
      </el-col>
      <el-col :span="12">
        <div class="node-wrap">
          <p class="node-header">
            <label>远程用例</label>
          </p>
          <node-tree
            ref="syncNodeTree"
            v-loading="remoteResult.loading"
            class="node-tree"
            :is-display="openType"
            :tree-nodes="remoteTreeNodes"
            @nodeSelectEvent="syncNodeChange"
          />
        </div>
      </el-col>
    </el-row>
    <template v-slot:footer>
      <div class="dialog-footer">
        <el-button @click="cancel">{{ $t("commons.cancel") }}</el-button>
        <el-button
          v-loading="syncLoading"
          type="primary"
          @click="saveTestCase"
          @keydown.enter.native.prevent
          >{{ $t("commons.confirm") }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script>
import { getCurrentProjectID } from "@/common/js/utils";
import MsDialogFooter from "@/business/components/common/components/MsDialogFooter";
import NodeTree from "@/business/components/track/common/NodeTree";

export default {
  name: "SyncTestCase",
  components: { MsDialogFooter, NodeTree },
  props: {
    treeNodes: {
      type: Array,
    },
  },
  data() {
    return {
      openType: "relevance",
      result: {},
      remoteResult: {},
      visible: false,
      syncLoading: false,
      currentModule: {},
      userOptions: [],
      remoteTreeNodes: [],
      msNodeId: "",
      msNodePath: "",
      remoteNodeId: "-1",
      remoteNodePath: "",
    };
  },
  computed: {
    projectId() {
      return getCurrentProjectID();
    },
  },
  methods: {
    init() {
      this.$nextTick(() => {
        this.$refs["msNodeTree"].init();
        this.$refs["syncNodeTree"].init();
      });
    },
    cancel() {
      this.visible = false;
    },
    getJiraCaseModules() {
      this.remoteResult.loading = true;
      const pId = getCurrentProjectID();
      this.$get("sync/testcase/module/" + pId + "/list", (response) => {
        this.remoteTreeNodes = response.data;
        this.remoteResult.loading = false;
      });
    },
    saveTestCase() {
      this.syncLoading = true;
      const pId = getCurrentProjectID();
      if (this.msNodeId === "root") {
        this.$message.error("请选择本地用例子节点同步！");
        this.syncLoading = false;
        return;
      }
      if (this.remoteNodeId === "root") {
        this.$message.error("请选择远程用例子节点同步！");
        this.syncLoading = false;
        return;
      }
      if (this.msNodeId == null || this.msNodeId === "") {
        this.$message.error("请选择本地用例节点同步！");
        this.syncLoading = false;
        return;
      }
      if (this.remoteNodeId == null || this.remoteNodeId === "-1") {
        this.$message.error("请选择远程用例节点同步！");
        this.syncLoading = false;
        return;
      }
      const reqBody = {
        projectId: pId,
        remoteNodeId: this.remoteNodeId,
        remoteNodePath: this.remoteNodePath,
        msNodeId: this.msNodeId,
        msNodePath: this.msNodePath,
      };
      this.$post("sync/testcase/" + pId, reqBody, (response) => {
        if (!response.data.syncOk) {
          this.$message.error(response.data.message);
        }
        this.$emit("refresh");
        this.syncLoading = false;
      });
    },
    nodeChange(node, nodeIds, nodeNames) {
      this.msNodeId = node.data.id;
      this.msNodePath = node.data.path;
    },
    syncNodeChange(node, nodeIds, nodeNames) {
      this.remoteNodeId = node.data.id;
      this.remoteNodePath = node.data.parentId;
    },
    open(currentModule) {
      this.currentModule = currentModule;
      this.getJiraCaseModules();
      this.visible = true;
      this.init();
    },
  },
};
</script>

<style scoped>
.node-wrap {
  border: 1px solid #ebeef5;
  border-radius: 4px;
}

.node-header {
  height: 40px;
  line-height: 40px;
  background: #f5f7fa;
  margin: 0;
  padding-left: 15px;
  border-bottom: 1px solid #ebeef5;
  box-sizing: border-box;
  color: #000;
}

.node-tree {
  margin-right: 10px;
  height: 52vh;
  overflow-y: auto;
  padding-right: 4px;
}
</style>
