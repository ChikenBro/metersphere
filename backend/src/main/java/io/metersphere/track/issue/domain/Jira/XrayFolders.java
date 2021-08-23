package io.metersphere.track.issue.domain.Jira;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class XrayFolders {
    private List<XrayTreeNode> folders;

}
