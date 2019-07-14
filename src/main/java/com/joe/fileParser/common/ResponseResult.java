package com.joe.fileParser.common;

import java.io.Serializable;

public class ResponseResult implements Serializable {

    private int code;

    private String msg;

    private Object data;

    private long count;

    private ResponseResult(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "ResponseResult{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                ", count=" + count +
                '}';
    }

    public static ResponseResult success() {
        return new ResponseResult(0);
    }

    public static ResponseResult fail() {
        return new ResponseResult(1);
    }

    public ResponseResult msg(Object object) {
        this.msg = object.toString();
        return this;
    }

    public ResponseResult data(Object object) {
        this.data = object;
        return this;
    }

    public ResponseResult count(long count) {
        this.count = count;
        return this;
    }
}
