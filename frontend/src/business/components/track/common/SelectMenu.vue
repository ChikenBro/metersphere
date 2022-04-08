<template>
  <div class="select-menu">
    <span class="menu-title">{{ "[" + title + "]" }}</span>
    <el-select
      slot="prepend"
      v-model="value"
      filterable
      :style="{ width: width }"
      size="small"
      @change="changeData"
    >
      <el-option
        v-for="(item, index) in data"
        :key="index"
        :label="item.name"
        :value="index"
      />
    </el-select>
  </div>
</template>

<script>
export default {
  name: "SelectMenu",
  props: {
    data: {
      type: Array,
    },
    currentData: {
      type: Object,
    },
    title: {
      type: String,
    },
    width: {
      type: String,
      default() {
        return "214px";
      },
    },
  },
  data() {
    return {
      value: "",
    };
  },
  watch: {
    currentData(data) {
      if (data != undefined && data != null) {
        this.value = data.name;
      }
    },
  },
  methods: {
    changeData(index) {
      this.$emit("dataChange", this.data[index]);
    },
  },
};
</script>

<style scoped>
.menu-title {
  color: darkgrey;
  margin-left: 10px;
  margin-right: 10px;
}
</style>
