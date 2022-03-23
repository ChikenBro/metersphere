package io.metersphere.dto;

import lombok.Data;

import java.util.List;

@Data
public class CustomFieldList {
    private String type;
    private List<Options> options;

}
