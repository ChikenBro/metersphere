export default {
  path: "/tools",
  name: "Tools",
  components: {
    content: () => import('@/business/components/tools/MdTools')
  },
  children: [
    {
      path: 'stream',
      component: () => import('@/business/components/tools/StreamTools'),
      meta: {stream: true, title: '流媒体工具', permissions: ['SYSTEM_ORGANIZATION:READ']}
    },
    {
      path: 'trend',
      component: () => import('@/business/components/tools/IssueTrend'),
      meta: {trend: true, title: 'Issue趋势', permissions: ['SYSTEM_ORGANIZATION:READ']}
    }
  ]
}
