<template>
  <div>
    <el-dropdown @command="handleCommand" class="scenario-ext-btn">
      <el-link type="primary" :underline="false">
        <el-icon class="el-icon-more"></el-icon>
      </el-link>
      <el-dropdown-menu slot="dropdown">
        <el-dropdown-item command="ref">{{ $t('api_test.automation.view_ref') }}</el-dropdown-item>
        <el-dropdown-item command="schedule" v-permission="['PROJECT_API_SCENARIO:READ+SCHEDULE']">
          {{ $t('api_test.automation.schedule') }}
        </el-dropdown-item>
        <el-dropdown-item command="create_performance" v-permission="['PROJECT_API_SCENARIO:READ+CREATE_PERFORMANCE']"
                          v-modules="['performance']">
          {{ $t('api_test.create_performance_test') }}
        </el-dropdown-item>
      </el-dropdown-menu>
    </el-dropdown>
    <ms-reference-view @openScenario="openScenario" ref="viewRef"/>
    <ms-schedule-maintain ref="scheduleMaintain" @refreshTable="refreshTable" :request="request"/>

  </div>
</template>

<script>
import MsReferenceView from "@/business/components/api/automation/scenario/ReferenceView";
import MsScheduleMaintain from "@/business/components/api/automation/schedule/ScheduleMaintain";
import {getCurrentProjectID, getUUID} from "@/common/js/utils";

export default {
  name: "MsScenarioExtendButtons",
  components: {MsReferenceView, MsScheduleMaintain},
  props: {
    row: Object,
    request: {}
  },
  methods: {
    handleCommand(cmd) {
      switch (cmd) {
        case  "ref":
          this.$refs.viewRef.open(this.row);
          break;
        case "schedule":
          this.$emit('openSchedule');
          this.$refs.scheduleMaintain.open(this.row);
          break;
        case "create_performance":
          this.createPerformance(this.row);
          break;
      }
    },
    createPerformance(row) {
      this.infoDb = false;
      let url = "/api/automation/genPerformanceTestJmx";
      let run = {};
      let scenarioIds = [];
      scenarioIds.push(row.id);
      run.projectId = getCurrentProjectID();
      run.ids = scenarioIds;
      run.id = getUUID();
      run.name = row.name;
      this.$post(url, run, response => {
        let jmxObj = {};
        jmxObj.name = response.data.name;
        jmxObj.xml = response.data.xml;
        jmxObj.attachFiles = response.data.attachFiles;
        jmxObj.attachByteFiles = response.data.attachByteFiles;
        jmxObj.scenarioId = row.id;
        jmxObj.version = row.version;
        this.$store.commit('setTest', {
          name: row.name,
          jmx: jmxObj
        });
        this.$router.push({
          path: "/performance/test/create"
        });
      });
    },
    openScenario(item) {
      this.$emit('openScenario', item);
    },
    refreshTable() {

    },
  }
};
</script>

<style scoped>
.scenario-ext-btn {
  margin-left: 10px;
}
</style>
