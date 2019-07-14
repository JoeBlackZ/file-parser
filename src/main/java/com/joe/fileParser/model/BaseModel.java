package com.joe.fileParser.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;

import java.io.Serializable;

public class BaseModel implements Serializable {

    @Id
    @Indexed
    private String id;

    @Transient
    private int page;

    @Transient
    private int limit;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Override
    public String toString() {
        return "BaseModel{" +
                "id=" + id +
                ", page=" + page +
                ", limit=" + limit +
                '}';
    }
}
