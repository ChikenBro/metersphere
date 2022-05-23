import { post, get } from "@/common/js/ajax";
import { getPageDate } from "@/common/js/tableUtils";
import { getCurrentProjectID } from "@/common/js/utils";

/**
 * @param {string} projectId
 * @param {string} name
 */
export function getIterationList(page) {
  return post(
    "/iteration/list/" + page.currentPage + "/" + page.pageSize,
    {
      ...page.condition,
      projectId: getCurrentProjectID(),
      iterationName: "",
      orders: [
        {
          name: "start_time",
          type: "desc",
        },
      ],
    },
    (response) => {
      getPageDate(response, page);
    }
  );
}

/**
 * 同步coding迭代
 * @param {string} projectId
 */
export function syncCoding(success) {
  return post(
    "/iteration/sync/coding",
    {
      projectId: getCurrentProjectID(),
    },
    (response) => {
      if (success) {
        success(response);
      }
    }
  );
}

/**
 * 查询迭代需求
 * @param {int} iterationCode
 * @param projectId
 */
export function getIterationRequirements(data, success) {
  const { goPage, pageSize, iterationId } = data;
  return post(
    `/iteration/requirement/${goPage}/${pageSize}`,
    {
      projectId: getCurrentProjectID(),
      iterationId,
    },
    (response) => {
      if (success) {
        success(response);
      }
    }
  );
}

/**
 * 查询迭代报告
 */
export function getIterationReport(iterationId, success) {
  return post(`/iteration/report/${iterationId}`, {}, (response) => {
    if (success) {
      success(response);
    }
  });
}
