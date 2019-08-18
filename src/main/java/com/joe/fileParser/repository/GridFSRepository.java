package com.joe.fileParser.repository;

import cn.hutool.core.util.ArrayUtil;
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
import java.util.List;

@Repository
public class GridFSRepository {

    /**
     * spring data mongodb gridFS 模板
     */
    @Resource
    private GridFsTemplate gridFsTemplate;

    /**
     * 保存文件到GridFS中
     * @param inputStream 要保存的文件流
     * @param filename 文件名称
     * @param metadata 文件的元数据
     * @return 返回文件的ObjectId
     */
    public ObjectId store(InputStream inputStream, String filename, Document metadata) {
        return this.gridFsTemplate.store(inputStream, filename, metadata);
    }

    /**
     * 根据多个删除多个文件
     * @param ids 多个文件id
     */
    public void deleteByIds(Object[] ids) {
        this.gridFsTemplate.delete(new Query(Criteria.where("_id").in(ids)));
    }

    /**
     * 根据文件id下载文件
     * @param objectId 文件id
     * @return 返回文件流
     * @throws IOException 文件读写异常
     */
    public InputStream download(Object objectId) throws IOException {
        GridFSFile gridFSFile = this.gridFsTemplate.findOne(new Query(Criteria.where("_id").is(objectId)));
        if (gridFSFile != null) {
            GridFsResource resource = this.gridFsTemplate.getResource(gridFSFile);
            return resource.getInputStream();
        }
        return null;
    }

    /**
     * 根据多个文件id下载多个文件
     * @param ids 文件id
     * @return 返回多个资源
     */
    public List<GridFsResource> download(Object[] ids) {
        if (ArrayUtil.isEmpty(ids)) return null;
        Criteria criteria = new Criteria().and("_id").in(ids);
        List<GridFsResource> list = new ArrayList<>();
        GridFSFindIterable gridFSFiles = this.gridFsTemplate.find(new Query(criteria));
        for (GridFSFile gridFSFile : gridFSFiles) {
            GridFsResource resource = this.gridFsTemplate.getResource(gridFSFile);
            list.add(resource);
        }
        return list;
    }
}
