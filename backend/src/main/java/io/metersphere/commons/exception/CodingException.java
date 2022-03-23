package io.metersphere.commons.exception;

import com.alibaba.fastjson.JSONObject;
import io.metersphere.commons.utils.LogUtil;
import io.metersphere.notice.util.HttpUtils;

public class CodingException {

    public static String checkCodingException(String url, Object obj) {
        String response = null;
        try {
            if (obj.getClass().getName().equals("java.lang.String")) {
                response = HttpUtils.jsonToObjectSendHttpRequest(url, JSONObject.parseObject("{}"));
            } else {
                response = HttpUtils.jsonToObjectSendHttpRequest(url, obj);
            }
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            if (e.toString().contains("java.lang.NullPointerException")) {
                return "ms-coding服务异常";
            }
            MSException.throwException(e.getMessage());
        }
        return response;
    }
}
