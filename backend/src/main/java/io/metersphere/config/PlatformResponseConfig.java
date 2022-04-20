package io.metersphere.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.metersphere.commons.exception.MSException;

public class PlatformResponseConfig {

    /**
     * 解析第三方返回内容
     *
     * @param result coding服务返回内容
     * @return json对象
     */
    public static JSONObject parsePlatformDate(String result) {
        JSONObject jsonObject = JSON.parseObject(result);
        if (null != jsonObject.get("code") && jsonObject.get("code").equals(10000000)) {
            return (JSONObject) jsonObject.get("data");
        } else if (null == jsonObject.get("data") && null != jsonObject.get("message")) {
            MSException.throwException(jsonObject.get("message").toString());
        }
        return jsonObject;
    }
}