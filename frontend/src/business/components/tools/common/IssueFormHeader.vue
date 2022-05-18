<template>
  <div class="issue-form-header">
    <el-row>
      <el-col :span="3" class="title">{{ title }}</el-col>
      <el-col :span="21">
        <el-form
          ref="form"
          :inline="true"
          :model="form"
          label-width="80px"
          size="mini"
          class="header-form"
        >
          <el-form-item
            v-for="(attr, index) in attrs"
            :key="index"
            :label="attr.label"
          >
            <el-select
              v-if="attr.type === 'select'"
              v-model="form[attr.prop]"
              style="width: 150px"
            >
              <el-option
                v-for="opt in attr.options"
                :key="opt.value"
                :label="opt.label"
                :value="opt.value"
              />
            </el-select>
            <el-date-picker
              v-if="attr.type === 'dateRange'"
              v-model="form[attr.prop]"
              type="daterange"
              range-separator=" - "
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="yyyy-MM-dd"
              style="width: 200px"
            >
            </el-date-picker>
            <el-input
              v-if="attr.type === 'inputNumber'"
              v-model="form[attr.prop]"
              style="width: 120px"
              @keyup.native="fixToNumber"
            >
              <i slot="suffix">&emsp;天</i>
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="mini" @click="onSearch">
              查询
            </el-button>
          </el-form-item>
        </el-form>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: "IssueFormHeader",
  props: {
    form: {
      type: Object,
      default() {
        return {};
      },
    },
    attrs: {
      type: Array,
      default() {
        return [];
      },
    },
    title: {
      type: String,
      default: "",
    },
  },
  data() {
    return {};
  },
  methods: {
    onSearch() {
      this.$emit("onSearch");
    },
    fixToNumber({ target }) {
      target.value = target.value.replace(/[^\d]/g, "");
      this.$nextTick(() => (this.form.duation = target.value));
    },
  },
};
</script>

<style scoped>
.issue-form-header {
  border-bottom: 1px solid #ccc;
  height: 50px;
}
.issue-form-header .title {
  line-height: 50px;
  padding-left: 5px;
}
.issue-form-header >>> .el-form-item {
  padding-top: 12px;
}
</style>
