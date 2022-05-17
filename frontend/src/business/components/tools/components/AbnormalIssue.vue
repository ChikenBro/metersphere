<template>
  <div class="trend-wrapper">
    <AbnormalIssueTable
      v-for="item in abnormalIssues"
      :key="item.type"
      :bug-info="item"
    />
  </div>
</template>
<script>
import AbnormalIssueTable from "../common/AbnormalIssueTable.vue";
export default {
  name: "AbnormalIssue",
  components: { AbnormalIssueTable },
  data() {
    return {
      abnormalIssues: [
        {
          type: "dev",
          baseUrl: "/trend/issue/total/projectquality/lastday/list",
        },
        {
          type: "test",
          baseUrl: "/trend/issue/total/projectquality/duedate/overtime/list",
        },
        {
          type: "unplan",
          baseUrl: "/trend/issue/total/projectquality/duedate/null/list",
        },
      ],
    };
  },
};
</script>

<style scoped>
.trend-wrapper {
  width: 100%;
}
</style>
