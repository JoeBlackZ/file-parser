package com.joe.fileParser.model;

import java.util.Arrays;

public class FileInfoEsHighlight {

    private String id;

    private String[] name;

    private String[] content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String[] getName() {
        return name;
    }

    public void setName(String[] name) {
        this.name = name;
    }

    public String[] getContent() {
        return content;
    }

    public void setContent(String[] content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "FileInfoEsHighlight{" +
                "id='" + id + '\'' +
                ", name=" + Arrays.toString(name) +
                ", content=" + Arrays.toString(content) +
                '}';
    }
}
