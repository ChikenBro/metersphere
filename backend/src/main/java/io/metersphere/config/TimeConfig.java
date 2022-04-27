package io.metersphere.config;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TimeConfig {
    private static final List<Long> timeList = new ArrayList();


    /**
     * 获取方法耗时
     *
     * @param method 方法名
     */
    public static void getMethodTime(String method) {
        timeList.add(System.currentTimeMillis());
        if (timeList.size() > 1) {
            int time = (int) (timeList.get(timeList.size() - 1) - timeList.get(timeList.size() - 2));
            log.info("{}用时{}毫秒", method, time);
        }
    }
}
