<template>
  <el-menu menu-trigger="click" :default-active="$route.path" router class="setting">
    <el-submenu index="1" v-permission="systemPermission">
      <template v-slot:title>
        <font-awesome-icon class="icon account" :icon="['far', 'chart-bar']" size="lg"/>
        <span>{{issueTrend}}</span>
      </template>
      <el-menu-item v-for="menu in trends" :key="menu.index" v-permission="menu.permissions" :index="menu.index"
                    class="setting-item">
        {{ $t(menu.title) }}
      </el-menu-item>
    </el-submenu>

    <el-submenu index="2"
                v-permission="['ORGANIZATION_USER:READ', 'ORGANIZATION_WORKSPACE:READ','ORGANIZATION_SERVICE:READ','ORGANIZATION_MESSAGE:READ', 'ORGANIZATION_OPERATING_LOG:READ']">
      <template v-slot:title>
        <font-awesome-icon class="icon organization" :icon="['far', 'eye']" size="lg"/>
        <span>{{ streamTools }}</span>
      </template>
      <el-menu-item v-for="menu in streams" v-permission="menu.permissions" :key="menu.index" :index="menu.index"
                    class="setting-item">
        {{ $t(menu.title) }}
      </el-menu-item>
    </el-submenu>
  </el-menu>
</template>

<script>
    import Tools from "@/business/components/tools/router";

    export default {
        name: "MdToolsMenu.vue",
        data() {
            let getMenus = function (group) {
                let menus = [];
                Tools.children.forEach(child => {
                    if (child.meta[group] === true) {
                        let menu = {index: Tools.path + "/" + child.path};
                        menu.title = child.meta.title;
                        menu.roles = child.meta.roles;
                        menu.permissions = child.meta.permissions;
                        menu.valid = child.meta.valid;
                        menus.push(menu);
                    }
                });
                return menus;
            };
            return {
                trends: getMenus('trend'),
                streams: getMenus('stream'),
                issueTrend: "报告图表",
                streamTools: "流媒体工具",
                systemPermission: [
                    'SYSTEM_USER:READ', 'SYSTEM_ORGANIZATION:READ', 'SYSTEM_GROUP:READ',
                    'ORGANIZATION_GROUP:READ', 'SYSTEM_WORKSPACE:READ', 'SYSTEM_TEST_POOL:READ',
                    'SYSTEM_SETTING:READ', 'SYSTEM_QUOTA:READ', 'SYSTEM_AUTH:READ'
                ]
            }
        },
        methods: {},
    }
</script>

<style scoped>
  .setting {
    border-right: 0;
  }

  .setting .setting-item {
    height: 40px;
    line-height: 40px;
  }

  .icon {
    width: 24px;
    margin-right: 10px;
  }

  .account {
    color: #5a78f0;
  }

  .organization {
    color: #743ab3;
  }

  .workspace {
    color: #44b349;
  }

</style>
