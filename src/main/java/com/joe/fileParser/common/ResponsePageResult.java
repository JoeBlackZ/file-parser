package com.joe.fileParser.common;

import java.io.Serializable;

public class ResponsePageResult implements Serializable {

    private int code;

    private String message;

    private Object data;

    private ResponsePageResult(int code) {
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

    public static ResponsePageResult success() {
        return new ResponsePageResult(0);
    }

    public static ResponsePageResult fail() {
        return new ResponsePageResult(1);
    }

    public ResponsePageResult message(Object object) {
        this.message = object.toString();
        return this;
    }

    public ResponsePageResult data(Object object) {
        this.data = object;
        return this;
    }
}
