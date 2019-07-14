package com.joe.fileParser.model;

import java.util.Map;

public class TikaModel {

    private String content;

    private Map<String, Object> metaData;

    public TikaModel() {
    }

    public TikaModel(String content, Map<String, Object> metaData) {
        this.content = content;
        this.metaData = metaData;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, Object> getMetaData() {
        return metaData;
    }

    public void setMetaData(Map<String, Object> metaData) {
        this.metaData = metaData;
    }

    @Override
    public String toString() {
        return "TikaModel{" +
                "content='" + content + '\'' +
                ", metaData=" + metaData +
                '}';
    }
}
