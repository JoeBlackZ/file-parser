package com.joe.fileParser.repository;

import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class GridFSRepository {

    @Resource
    private GridFsTemplate gridFsTemplate;

    public ObjectId store(InputStream inputStream, String filename, String contentType, Document metadata) {
        return this.gridFsTemplate.store(inputStream, filename, contentType, metadata);
    }

    public ObjectId store(InputStream inputStream, String filename, Document metadata) {
        return this.gridFsTemplate.store(inputStream, filename, metadata);
    }

    public ObjectId store(InputStream inputStream, String filename) {
        return this.gridFsTemplate.store(inputStream, filename);
    }

    public void deleteById(Object id) {
        this.gridFsTemplate.delete(new Query(Criteria.where("_id").is(id)));
    }

    public void deleteByIds(Object[] ids) {
        this.gridFsTemplate.delete(new Query(Criteria.where("_id").in(ids)));
    }

    public GridFSFindIterable findAll() {
        return this.gridFsTemplate.find(new Query());
    }

    public InputStream download(Object objectId) throws IOException {
        GridFSFile gridFSFile = this.gridFsTemplate.findOne(new Query(Criteria.where("_id").is(objectId)));
        if (gridFSFile != null) {
            GridFsResource resource = this.gridFsTemplate.getResource(gridFSFile);
            return resource.getInputStream();
        }
        return null;
    }

    public List<GridFsResource> download(Document metadata) {
        if (metadata.isEmpty()) return null;
        Criteria criteria = new Criteria();
        metadata.forEach((s, o) -> criteria.and("metadata.".concat(s)).is(o.toString()));
        List<GridFsResource> list = new ArrayList<>();
        GridFSFindIterable gridFSFiles = this.gridFsTemplate.find(new Query(criteria));
        for (GridFSFile gridFSFile : gridFSFiles) {
            GridFsResource resource = this.gridFsTemplate.getResource(gridFSFile);
            list.add(resource);
        }
        return list;
    }
}
