package io.metersphere.track.request.testcase;

import io.metersphere.base.domain.IssuesWithBLOBs;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class IssuesUpdateRequest extends IssuesWithBLOBs {
    private String content;
    private String testCaseId;
    private String organizationId;
    /**
     * coding 处理字段
     */
    private FieldsRequest fields;


    private List<String> tapdUsers;
    /**
     * zentao bug 处理人
     */
    private String zentaoUser;
    private String zentaoAssigned;
    /**
     * zentao bug 影响版本
     */
    private List<String> zentaoBuilds;
    private List<String> testCaseIds;
    /**
     * 创建者名称
     */
    private String creatorName;

    /**
     * 缺陷 id
     */
    private String issueId;
    /**
     * 缺陷状态 Id
     */
    private Integer statusId;

}
