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

    /**
     * 校验coding返回
     *
     * @param result 返回内容
     * @param result 操作类型
     * @return
     */
    public static void checkCodingResult(String result, String operatorType) {
        //coding返回错误信息
        if (result.contains("Error")) {
            MSException.throwException(result);
        } else if (result.contains(String.format("平台%s失败", operatorType))) {
            MSException.throwException(String.format("coding%s成功，平台%s失败，重试%s终止", operatorType));
        } else if ("ms-coding服务异常".equals(result)) {
            MSException.throwException("ms-coding服务异常");
        }
    }
}
