package io.metersphere.track.issue.domain.Jira;

import lombok.Data;

import java.util.List;

@Data
public class XrayTreeNode {
    private Integer rank;
    private String name;
    private Integer id;
    private Integer testCount;
    private Integer totalTestCount;
    private String testRepositoryPath;
    private List<XrayTreeNode> folders;
}
