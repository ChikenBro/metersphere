package io.metersphere.track.domain;

import lombok.Data;

@Data
public class FolderInfo {
    private String name;
    private Integer id;
    private boolean isExist;
    private String msNodeId;
    private String msParentId;
    private String msNodePath;
}
