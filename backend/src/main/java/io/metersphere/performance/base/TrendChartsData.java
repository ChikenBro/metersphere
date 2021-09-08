package io.metersphere.performance.base;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TrendChartsData {

    private List<String> legend;

    private List<String> xAxis;

    private List<Map<String, List<Integer>>> seriesAddBug;

    private List<Map<String, List<Integer>>> seriesFixBug;

}
