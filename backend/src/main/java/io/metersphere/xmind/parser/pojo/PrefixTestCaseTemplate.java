package io.metersphere.xmind.parser.pojo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class PrefixTestCaseTemplate {

    private String treeId;

    private String modulePath;

    private String caseTitle;

    private String precondition;

    private String priority;

    private List<Map> caseStep;
}
