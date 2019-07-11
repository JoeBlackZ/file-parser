package com.joe.fileParser.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "file_info")
public class FileInfo extends BaseModel{

    @Field
    private String name;

    @Field
    private String extName;

    @Field
    private Long length;

    @Field
    private String contentType;

    @Field
    @Indexed
    private String fileId;

    @Field
    @Indexed
    private String md5;

    @Field
    private String uploadDateTime;

    public FileInfo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExtName() {
        return extName;
    }

    public void setExtName(String extName) {
        this.extName = extName;
    }

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getUploadDateTime() {
        return uploadDateTime;
    }

    public void setUploadDateTime(String uploadDateTime) {
        this.uploadDateTime = uploadDateTime;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "name='" + name + '\'' +
                ", extName='" + extName + '\'' +
                ", length=" + length +
                ", contentType='" + contentType + '\'' +
                ", fileId='" + fileId + '\'' +
                ", md5='" + md5 + '\'' +
                ", uploadDateTime='" + uploadDateTime + '\'' +
                '}';
    }
}
