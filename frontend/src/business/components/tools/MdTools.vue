<template>
  <ms-container>
    <ms-aside-container>
      <ms-setting-menu />
    </ms-aside-container>

    <ms-main-container>
      <keep-alive>
        <router-view />
      </keep-alive>
    </ms-main-container>
  </ms-container>
</template>

<script>
import MsSettingMenu from "./MdToolsMenu";
import MsAsideContainer from "../common/components/MsAsideContainer";
import MsContainer from "../common/components/MsContainer";
import MsMainContainer from "../common/components/MsMainContainer";
import { getCurrentOrganizationId, getCurrentUserId } from "@/common/js/utils";
export default {
  name: "MdTools",
  components: { MsMainContainer, MsContainer, MsAsideContainer, MsSettingMenu },
  created() {
    this.getToken();
  },
  methods: {
    getToken() {
      this.$get("/user/info/" + getCurrentUserId(), (response) => {
        const platformInfo = JSON.parse(response.data.platformInfo);
        const token =
          platformInfo[getCurrentOrganizationId()]?.codingToken || "";
        sessionStorage.setItem("codingToken", token);
      });
    },
  },
};
</script>

<style scoped>
.ms-aside-container {
  height: calc(100vh - 40px);
  padding: 20px;
}

.ms-main-container {
  height: calc(100vh - 40px);
}

h1 {
  font-size: 20px;
  font-weight: 500;
}
</style>
