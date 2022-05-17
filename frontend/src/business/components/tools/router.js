export default {
  path: "/tools",
  name: "Tools",
  components: {
    content: () => import("@/business/components/tools/MdTools"),
  },
  children: [
    {
      path: "stream",
      component: () =>
        import("@/business/components/tools/components/StreamTools"),
      meta: {
        stream: true,
        title: "流媒体工具",
        permissions: ["SYSTEM_ORGANIZATION:READ"],
      },
    },
    // {
    //   path: "trend",
    //   component: () => import("@/business/components/tools/components/IssueTrend"),
    //   meta: {
    //     trend: true,
    //     title: "Issue趋势",
    //     permissions: ["SYSTEM_ORGANIZATION:READ"],
    //   },
    // },
    {
      path: "weeklyissue",
      component: () =>
        import("@/business/components/tools/components/WeeklyIssue"),
      meta: {
        issue: true,
        title: "本周Bug统计 ",
        permissions: ["SYSTEM_ORGANIZATION:READ", "ORGANIZATION_USER:READ"],
      },
    },
    {
      path: "varprojectissue",
      component: () =>
        import("@/business/components/tools/components/VarProjectIssue"),
      meta: {
        issue: true,
        title: "各项目bug统计",
        permissions: ["SYSTEM_ORGANIZATION:READ", "ORGANIZATION_USER:READ"],
      },
    },
    {
      path: "abnormalissue",
      component: () =>
        import("@/business/components/tools/components/AbnormalIssue"),
      meta: {
        issue: true,
        title: "异常bug统计",
        permissions: ["SYSTEM_ORGANIZATION:READ", "ORGANIZATION_USER:READ"],
      },
    },
  ],
};
