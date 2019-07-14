package com.joe.fileParser.repository;

import com.joe.fileParser.model.BaseModel;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import javax.annotation.Resource;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("unchecked")
public abstract class BaseRepository<T, ID extends Serializable> {

    @Resource
    private MongoTemplate mongoTemplate;

    private Class<T> entityClass;

    BaseRepository() {
        ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        Type[] actualTypeArguments = (genericSuperclass).getActualTypeArguments();
        entityClass = (Class<T>) actualTypeArguments[0];
    }

    public T insert(T entity) {
        return this.mongoTemplate.insert(entity);
    }

    public Collection<T> insertAll(Collection<T> list) {
        return this.mongoTemplate.insertAll(list);
    }

    public long updateById(BaseModel baseModel) throws Exception{
        Query query = new Query(Criteria.where("_id").is(baseModel.getId()));
        Update update = new Update();
        Field[] fields = baseModel.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Object o = field.get(baseModel);
            if (o != null) update.set(field.getName(), o);
        }
        UpdateResult updateResult = this.mongoTemplate.updateFirst(query, update, entityClass);
        return updateResult.getModifiedCount();
    }

    public long deleteByIds(ID... ids) {
        DeleteResult deleteResult = this.mongoTemplate.remove(new Query(Criteria.where("_id").in((Object[]) ids)), entityClass);
        return deleteResult.getDeletedCount();
    }

    public long deleteByIds(Collection<? extends Serializable> ids) {
        DeleteResult deleteResult = this.mongoTemplate.remove(new Query(Criteria.where("_id").in(ids)), entityClass);
        return deleteResult.getDeletedCount();
    }

    public long deleteById(Serializable id) {
        DeleteResult deleteResult = this.mongoTemplate.remove(new Query(Criteria.where("_id").is(id)), entityClass);
        return deleteResult.getDeletedCount();
    }

    public List<T> findAll() {
        return this.mongoTemplate.findAll(entityClass);
    }

    public List<T> findAllByPage(int pageNum, int pageSize) {
        Query query = new Query().skip((pageNum - 1) * pageSize).limit(pageSize);
        return this.mongoTemplate.find(query, entityClass);
    }

    public List<T> find(BaseModel baseModel) throws Exception{
        Criteria criteria = this.getCriteria(baseModel);
        return this.mongoTemplate.find(new Query(criteria), entityClass);
    }

    public List<T> findByPage(BaseModel baseModel) throws Exception{
        Criteria criteria = this.getCriteria(baseModel);
        Query query = new Query(criteria).skip((baseModel.getPage() - 1) * baseModel.getLimit()).limit(baseModel.getLimit());
        return this.mongoTemplate.find(query, entityClass);
    }

    public T findOne(BaseModel baseModel) throws Exception{
        return this.mongoTemplate.findOne(new Query(this.getCriteria(baseModel)), entityClass);
    }

    public T findById(ID id) {
        return this.mongoTemplate.findById(id, entityClass);
    }

    public long count() {
        return this.mongoTemplate.count(new Query(), entityClass);
    }

    public long count(BaseModel baseModel) throws Exception{
        return this.mongoTemplate.count(new Query(this.getCriteria(baseModel)), entityClass);
    }

    private Criteria getCriteria(BaseModel baseModel) throws Exception{
        Criteria criteria = new Criteria();
        if (baseModel.getId() != null)
            criteria.and("id").is(baseModel.getId());

        Field[] declaredFields = entityClass.getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            Object o = field.get(baseModel);
            if (o != null) criteria.and(field.getName()).is(o);
        }
        return criteria;
    }

}
