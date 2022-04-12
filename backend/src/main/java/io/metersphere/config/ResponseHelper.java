package io.metersphere.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Data
public class ResponseHelper<T> {
    private int code;
    private boolean success;
    private String message;
    private T data;

    private ResponseHelper(T data) {
        this.code = CodeMsg.SUCCESS.getCode();
        this.success = true;
        this.message = CodeMsg.SUCCESS.getMessage();
        this.data = data;
    }

    private ResponseHelper(String message) {
        this.code = CodeMsg.SUCCESS.getCode();
        this.success = true;
        this.message = message;
        this.data = null;
    }

    private ResponseHelper(int code, String message) {
        this.code = code;
        this.success = false;
        this.message = message;
        this.data = null;
    }


    /**
     * 成功，提示正确信息
     *
     * @param data 返回数据
     * @param <T>
     * @return
     */
    public static <T> ResponseHelper<T> genResponse(T data) {
        return new ResponseHelper<T>(data);
    }

    /**
     * 成功，提示正确信息
     *
     * @param message 自定义信息
     * @param <T>
     * @return
     */
    public static <T> ResponseHelper<T> genResponse(String message) {
        return new ResponseHelper<T>(message);
    }

    /**
     * 错误返回，提示对应信息
     *
     * @param message 自定义code ,自定义信息
     * @param <T>
     * @return
     */
    public static <T> ResponseHelper<T> genResponse(int code, String message) {
        return new ResponseHelper<T>(code, message);
    }
}
