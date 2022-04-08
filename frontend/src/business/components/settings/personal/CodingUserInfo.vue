<template>
  <el-form label-position="right" label-width="100px" size="small">
    <el-form-item :label="'Coding 信息'">
      <ms-instructions-icon
        size="10"
        :content="'该信息为通过Coding提交缺陷的用户认证信息，若未填写，则使用组织中配置的默认信息'"
      />
    </el-form-item>
    <el-form-item :label="'个人访问令牌'" prop="password">
      <el-input
        v-model="data.codingToken"
        :placeholder="'请输入个人访问令牌'"
        show-password
      />
    </el-form-item>
    <div class="auth-btn">
      <el-button
        type="primary"
        style="float: right"
        size="mini"
        @click="$emit('auth', 'Coding')"
      >
        {{ $t("commons.validate") }}
      </el-button>
    </div>
  </el-form>
</template>

<script>
import MsInstructionsIcon from "@/business/components/common/components/MsInstructionsIcon";
export default {
  name: "CodingUserInfo",
  components: { MsInstructionsIcon },
  props: ["data"],
};
</script>

<style scoped>
.instructions-icon {
  margin-left: -5px;
}
.auth-btn {
  height: 28px;
}
</style>
