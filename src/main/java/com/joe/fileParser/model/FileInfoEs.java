package com.joe.fileParser.model;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "file_info")
public class FileInfoEs extends BaseModel{

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String fileName;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String content;

    public FileInfoEs() {
    }

    public FileInfoEs(String id, String fileName, String content) {
        super.setId(id);
        this.fileName = fileName;
        this.content = content;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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
                ", fileName='" + fileName + '\'' +
                ", content='" + content + '\'' +
                "} " + super.toString();
    }
}
