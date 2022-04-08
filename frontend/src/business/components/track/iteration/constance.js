export const IterationStatusEnum = {
  PROCESSING: "进行中",
  COMPLETED: "已完成",
  WAIT_PROCESS: "未开始",
};

export const StatusTagMap = {
  PROCESSING: {
    type: "info",
  },
  COMPLETED: {
    type: "success",
  },
  WAIT_PROCESS: {
    type: "warning",
  },
};

export const requiremnetPriority = {
  0: "低",
  1: "中",
  2: "高",
  3: "紧急",
};
