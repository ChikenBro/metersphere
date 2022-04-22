<template>
  <el-col>
    <div>
      <transition>
        <keep-alive>
          <router-view :base-url="baseUrl" />
        </keep-alive>
      </transition>
    </div>
  </el-col>
</template>

<script>
export default {
  name: "ReportIndex",
  data() {
    return {
      baseUrl: "report",
    };
  },
  activated() {
    this.$refs.table.doLayout();
  },
};
</script>

<style>
.node-tree {
  margin: 3%;
}
</style>

<style scoped></style>
