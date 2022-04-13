package io.metersphere.iteration;

import lombok.Data;

@Data
public class RequirementRequest extends TokenRequest {
    private String projectId;
    private Integer iterationCode;
    private String name;
}
