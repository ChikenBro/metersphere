package io.metersphere.track.response;

import lombok.Data;

@Data
public class SyncResult {
    private boolean isSyncOk;
    private String message;
}
