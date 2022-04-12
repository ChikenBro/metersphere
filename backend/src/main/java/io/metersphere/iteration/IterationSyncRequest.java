package io.metersphere.iteration;

import io.metersphere.controller.request.OrderRequest;
import lombok.Data;

import java.util.List;

@Data
public class IterationSyncRequest extends TokenRequest {
    private String projectId;
    private String iterationName;
    private List<OrderRequest> orders;
}
