package io.metersphere.iteration.response;

import lombok.Data;

@Data
public class Issues {
    private String id;
    private Integer num;
    private String title;
    private String description;
    private String module;
    private String status;
    private String handler;
    private String createTime;
}
