package io.metersphere.track.service.utils;

public enum TestCasePriority {
    High("High", "P1"), Medium("Medium", "P2"), Low("Low", "P3"),
    ;

    private String name;
    private String priority;

    TestCasePriority(String name, String priority) {
        this.name = name;
        this.priority = priority;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public static String getPriority(String name) {
        for (TestCasePriority p : TestCasePriority.values()) {
            if (p.name.equals(name)) {
                return p.priority;
            }
        }
        return "P2";
    }
}
