<template>
  <el-select v-model="data[prop]" filterable>
    <el-option
      v-for="item in options"
      :key="item.id"
      :label="item.displayName"
      :value="item.value"
    >
    </el-option>
  </el-select>
</template>

<script>
export default {
  name: "ProjectCodingSelector",
  props: {
    prop: String,
    data: {
      type: Object,
      default() {
        return {
          codingInfo: {},
        };
      },
    },
  },
  data() {
    return {
      options: [],
    };
  },
  mounted() {
    this.getCodingProjects();
  },
  methods: {
    getCodingProjects() {
      this.$get("/trend/issue/total/getAllProject", (response) => {
        this.options = response.data.map((item) => {
          return {
            ...item,
            value: { codingId: item.id, codingName: item.displayName },
          };
        });
      });
    },
  },
};
</script>

<style scoped></style>
