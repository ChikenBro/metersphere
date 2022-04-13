<template>
  <el-select v-model="data[prop]" filterable @change="$forceUpdate()">
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
          codingInfo: "",
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
            value: JSON.stringify({
              codingProjectId: item.id,
              codingProjectName: item.projectName,
              codingDisplayName: item.displayName,
            }),
          };
        });
      });
    },
  },
};
</script>

<style scoped></style>
