<template>
  <div>
    <ms-tag v-if="value == '3'" type="danger" :content="text" />
    <ms-tag v-if="value == '2'" type="warning" :content="text" />
    <ms-tag v-if="value == '1'" effect="light" type="danger" :content="text" />
    <ms-tag v-if="value == '0'" effect="light" type="warning" :content="text" />
  </div>
</template>

<script>
import MsTag from "@/business/components/common/components/MsTag";
export default {
  name: "RequiremnetPriorityItem",
  components: { MsTag },
  props: {
    value: {
      type: String,
    },
    text: {
      type: String,
    },
  },
};
</script>

<style scoped></style>
