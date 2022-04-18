package io.metersphere.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class PlatformResponseConfig {

    /**
     * 解析第三方返回内容
     *
     * @param result
     * @return
     */
    public static JSONObject parsePlatformDate(String result) {
        JSONObject jsonObject = JSON.parseObject(result);
        if (null != jsonObject.get("code") && jsonObject.get("code").equals("10000000")) {
            return (JSONObject) jsonObject.get("data");
        }
        return jsonObject;
    }
}