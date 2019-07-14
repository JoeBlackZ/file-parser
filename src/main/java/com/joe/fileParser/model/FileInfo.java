package com.joe.fileParser.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "file_info")
public class FileInfo extends BaseModel{

    /**
     * 文件名称
     */
    @Field
    private String name;

    /**
     * 文件扩展名
     */
    @Field
    private String extName;

    /**
     * 文件长度
     */
    @Field
    private Long length;

    /**
     * content-type
     */
    @Field
    private String contentType;

    /**
     * 文件id
     */
    @Field
    @Indexed
    private String fileId;

    /**
     * 文件md5 主要用于验证文件的重复
     */
    @Field
    @Indexed
    private String md5;

    /**
     * 文件上传时间
     */
    @Field
    private String uploadDateTime;

    public FileInfo() {
    }

    public FileInfo(String id, String name, String extName, Long length, String contentType, String fileId, String uploadDateTime) {
        this.setId(id);
        this.name = name;
        this.extName = extName;
        this.length = length;
        this.contentType = contentType;
        this.fileId = fileId;
        this.uploadDateTime = uploadDateTime;
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
                "} " + super.toString();
    }
}
