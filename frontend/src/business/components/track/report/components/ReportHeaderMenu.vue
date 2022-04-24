<template>
  <div id="menu-bar">
    <el-row type="flex">
      <el-col :span="14">
        <el-menu
          class="header-menu"
          :unique-opened="true"
          mode="horizontal"
          router
          :default-active="path"
        >
          <el-menu-item index="/track/report/testPlan"> 测试报告 </el-menu-item>
          <el-menu-item index="/track/report/iteration">
            迭代报告
          </el-menu-item>
        </el-menu>
      </el-col>
    </el-row>
  </div>
</template>
<script>
export default {
  name: "ReportHeaderMenu",
  props: {
    pathName: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      path: this.pathName,
    };
  },
  watch: {
    $route: {
      immediate: true,
      handler(to, from) {
        this.path = to.path;
      },
    },
  },
};
</script>

<style scoped>
.el-divider--horizontal {
  margin: 0;
}

#menu-bar {
  background-color: #fff;
}

.blank_item {
  display: none;
}

.el-divider--horizontal {
  margin: 0;
}

.deactivation >>> .el-submenu__title {
  border-bottom: white !important;
}
</style>
