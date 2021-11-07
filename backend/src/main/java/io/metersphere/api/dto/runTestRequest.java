package io.metersphere.api.dto;


import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class runTestRequest {

    //环境
    private String env;

    //业务线
    private String product;

    //服务名称
    private String serverName;


}
