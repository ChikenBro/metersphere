package io.metersphere.dto;

import lombok.Data;

import java.util.List;

@Data
public class CustomFieldList {
    private Integer type;
    private String name;
    private String message;
    private List<Options> options;
}
