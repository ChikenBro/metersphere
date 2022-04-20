package io.metersphere.notice.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.metersphere.track.request.testcase.IssuesUpdateRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class HttpUtils {

    /**
     * 发送http请求
     *
     * @param url url路径
     * @param obj 对象
     * @return response返回
     */
    public static String jsonToObjectSendHttpRequest(String url, Object obj) {
        String json = JSON.toJSONString(obj);
        JSONObject jsonObject = JSONObject.parseObject(json);
        return HttpUtils.httpUtils(url, jsonObject);
    }


    public static String httpUtils(String url, JSONObject jsonData) {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(url);
        httppost.setHeader("Content-type", "application/json");
        StringEntity se = new StringEntity(jsonData.toString(), ContentType.create("application/json", "UTF-8"));
        httppost.setEntity(se);
        CloseableHttpResponse response = null;
        String res = "";
        try {
            response = client.execute(httppost);
            res = EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return res;
    }
}
