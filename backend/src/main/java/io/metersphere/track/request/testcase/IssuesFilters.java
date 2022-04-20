package io.metersphere.track.request.testcase;

import lombok.Data;

import java.util.List;

@Data
public class IssuesFilters {
    private List<String> assignee_name;
    private List<String> creator_name;
    private List<String> iteration_name;
    private List<Integer> platform_status;
    private List<String> requirement_name;
}
