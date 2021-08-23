package io.metersphere.track.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class RemoteTestCaseModuleDTO {
    private String projectId;
    private String msNodeId;
    private String msNodePath;
    private String remoteNodeId;
    private String remoteNodePath;
}
