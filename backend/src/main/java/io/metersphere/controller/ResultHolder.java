package io.metersphere.controller;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ResultHolder {
    public ResultHolder() {
        this.success = true;
    }

    private ResultHolder(Object data) {
        this.data = data;
        this.success = true;
        this.code = 0;
    }

    private ResultHolder(boolean success, String msg) {
        this.success = success;
        this.message = msg;
        if (success){
            this.code = 0;
        }
        else {
            this.code = 1;
        }

    }

    private ResultHolder(boolean success, String msg, Object data) {
        this.success = success;
        this.message = msg;
        this.data = data;
        if (success){
            this.code = 0;
        }
        else {
            this.code = 1;
        }


    }
    private ResultHolder(Integer code, String msg, Object data,Integer count) {
        this.success = true;
        this.code = code;
        this.message = msg;
        this.data = data;
        this.count = count;
    }
    private ResultHolder(Integer code, String msg, Object data) {
        this.code = code;
        this.message = msg;
        this.data = data;
    }
    // 请求是否成功
    private Integer code;
    // 请求是否成功
    private Integer count ;
    // 请求是否成功
    private boolean success;
    // 描述信息
    private String message;
    // 返回数据
    private Object data = "";

    public boolean isSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public static ResultHolder success(Object obj) {
        return new ResultHolder(obj);
    }

    public static ResultHolder error(String message) {
        return new ResultHolder(false, message, null);
    }

    public static ResultHolder error(String message, Object object) {
        return new ResultHolder(false, message, object);
    }
    public static ResultHolder selfInface(Integer code,String message, Object object,Integer count) {
        return new ResultHolder(code, message, object,count);
    }
    public static ResultHolder selfInfaceNew(Integer code,String message, Object object) {
        return new ResultHolder(code, message, object);
    }

    public Integer getCode() {
        return code;
    }
    public boolean getSuccess() {
        return success;
    }
    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
