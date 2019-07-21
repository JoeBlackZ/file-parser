package com.joe.fileParser.model;

import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "file_info")
public class FileInfoEs extends BaseModel{

    @Transient
    private String keyword;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word", store = true)
    private String name;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word", store = true)
    private String content;

    public FileInfoEs() {
    }

    public FileInfoEs(String id, String name, String content) {
        super.setId(id);
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String toString() {
        return "FileInfoEs{" +
                "keyword='" + keyword + '\'' +
                ", name='" + name + '\'' +
                ", content='" + content + '\'' +
                "} " + super.toString();
    }
}
