package com.joe.fileParser.model;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "file_info")
public class FileInfoEs extends BaseModel{

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String name;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
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

    @Override
    public String toString() {
        return "EsFIleInfo{" +
                "id='" + super.getId() + '\'' +
                ", name='" + name + '\'' +
                ", content='" + content + '\'' +
                "} " + super.toString();
    }
}
