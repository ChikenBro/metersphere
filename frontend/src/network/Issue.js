import {
  post,
  get
} from "@/common/js/ajax";
import {
  getPageDate
} from "@/common/js/tableUtils";
import {
  getCurrentProjectID
} from "@/common/js/utils";

export function buildIssues(page) {
  let data = page.data;
  for (let i = 0; i < data.length; i++) {
    if (data[i]) {
      if (data[i].customFields) {
        data[i].customFields = JSON.parse(data[i].customFields);
        data[i] = {
          ...data[i],
          ...data[i].customFields
        }
      }
      data[i] = {
        ...data[i],
        ...{
          "assigneeName": "何鹏翀",
          "defectTypeId": 30801759,
          "defectTypeName": "功能缺陷",
          "dueDate": "2022-03-02",
          "fileIds": [27119519],
          "iterationCode": 1,
          "iterationName": "迭代-未开始",
          "priority": "2",
          "recordedHours": 3.0,
          "requirementCode": 3,
          "requirementName": "需求-已完成",
          "startDate": "2022-05-20",
          "watcherName": "何鹏翀",
          "workingHours": 1.5
        }
      }
      // if (data[i].platform !== 'Local') {
      //   page.result = buildPlatformIssue(data[i]);
      // }
    }
  }
}

export function getIssues(page) {
  return post(
    "issues/list/" + page.currentPage + "/" + page.pageSize,
    page.condition,
    (response) => {
      getPageDate(response, page);
      buildIssues(page);
    }
  );
}

export function getIssuesByCaseId(caseId, page) {
  if (caseId) {
    return get("issues/get/" + caseId, (response) => {
      page.data = response.data;
      buildIssues(page);
    });
  }
}

export function buildPlatformIssue(data) {
  data.customFields = JSON.stringify(data.customFields);
  return post("issues/get/platform/issue", data)
    .then((response) => {
      let issues = response.data.data;
      if (issues) {
        data.title = issues.title ? issues.title : "--";
        data.description = issues.description ? issues.description : "--";
        data.status = issues.status ? issues.status : "delete";
        data.customFields = JSON.parse(data.customFields);
      }
    })
    .catch(() => {
      data.title = "--";
      data.description = "--";
      data.status = "--";
    });
}

export function testCaseIssueRelate(param, success) {
  return post("test/case/issues/relate", param, (response) => {
    if (success) {
      success(response);
    }
  });
}

export function getRelateIssues(page) {
  return post(
    "issues/list/relate/" + page.currentPage + "/" + page.pageSize,
    page.condition,
    (response) => {
      getPageDate(response, page);
      buildIssues(page);
    }
  );
}

export function syncIssues(success) {
  return get('/trend/issue/sync/coding?projectId=' + getCurrentProjectID(), (response) => {
    if (success) {
      success(response);
    }
  });
}
