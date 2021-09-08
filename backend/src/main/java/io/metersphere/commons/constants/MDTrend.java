package io.metersphere.commons.constants;

import java.util.stream.Stream;

public enum MDTrend {

    MDR(1, "MDR", "目睹云"),
    MDW(2, "MDW", "WeLink"),
    MDL(3, "MDL", "直播"),
    MDE(4, "MDE", "老企播"),
    MDN(5, "MDN", "企播");

    private int index;
    private String name;
    private String value;

    private MDTrend(int index, String name, String value) {
        this.index = index;
        this.name = name;
        this.value = value;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    /**
     * 枚举值向枚举类型转换
     *
     * @param name
     * @return MDTrend
     */
    public static MDTrend toTrendEnum(String name) {
        return Stream.of(MDTrend.values())
                .filter(item -> item.name.equals(name))
                .findAny()
                .orElse(null);
    }
}
