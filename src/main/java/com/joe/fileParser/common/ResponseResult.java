package com.joe.fileParser.common;

import java.io.Serializable;

public class ResponseResult implements Serializable {

    private int code;

    private String message;

    private Object data;

    private ResponseResult(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
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

    @Override
    public String toString() {
        return "ResponseResult{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

    public static ResponseResult success() {
        return new ResponseResult(0);
    }

    public static ResponseResult fail() {
        return new ResponseResult(1);
    }

    public ResponseResult message(String message) {
        this.message = message;
        return this;
    }

    public ResponseResult data(Object object) {
        this.data = object;
        return this;
    }
}
