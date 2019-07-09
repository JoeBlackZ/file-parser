package com.joe.fileParser.repository;

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
import java.util.Collections;
import java.util.List;

import static javax.swing.UIManager.get;

@SuppressWarnings("unchecked")
public abstract class BaseRepository<T, ID extends Serializable> {

    @Resource
    protected MongoTemplate mongoTemplate;

    private Class<T> entityClass;

    public BaseRepository() {
        ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        Type[] actualTypeArguments = (genericSuperclass).getActualTypeArguments();
        entityClass = (Class<T>) actualTypeArguments[0];
    }

    public T insert(T entity) {
        return this.mongoTemplate.insert(entity);
    }

    public List<T> insertAll(List<T> list) {
        return (List<T>) this.mongoTemplate.insertAll(list);
    }

    public boolean updateById(ID id, T entity) {
        try {
            Query query = new Query(Criteria.where("_id").is(id));
            Update update = new Update();
            Field[] fields = entity.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Object o = field.get(entity);
                if (o != null) update.set(field.getName(), o);
            }
            UpdateResult updateResult = this.mongoTemplate.updateFirst(query, update, entityClass);
            return updateResult.getModifiedCount() > 0;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    public long deleteByIds(Serializable... ids) {
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

    public List<T> find(T entity) {
        Criteria criteria = this.getCriteria(entity);
        return this.mongoTemplate.find(new Query(criteria), entityClass);
    }

    public List<T> findByPage(T entity) {
        try {
            Criteria criteria = this.getCriteria(entity);
            Field pageNum_field = entityClass.getSuperclass().getDeclaredField("pageNum");
            pageNum_field.setAccessible(true);
            Field pageSize_filed = entityClass.getSuperclass().getDeclaredField("pageSize");
            pageSize_filed.setAccessible(true);
            int pageNum = (int) pageNum_field.get(entity);
            int pageSize = (int) pageSize_filed.get(entity);
            Query query = new Query(criteria).skip((pageNum - 1) * pageSize).limit(pageSize);
            return this.mongoTemplate.find(query, entityClass);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public T findOne(T entity) {
        return this.mongoTemplate.findOne(new Query(this.getCriteria(entity)), entityClass);
    }

    public T findById(ID id) {
        return this.mongoTemplate.findById(id, entityClass);
    }

    public long count() {
        return this.mongoTemplate.count(new Query(), entityClass);
    }

    public long count(T entity) {
        return this.mongoTemplate.count(new Query(this.getCriteria(entity)), entityClass);
    }

    private Criteria getCriteria(T entity) {
        Criteria criteria = new Criteria();
        try {
            Field id_field = entity.getClass().getSuperclass().getDeclaredField("id");
            id_field.setAccessible(true);
            if (id_field.get(entity) != null) criteria.and(id_field.getName()).is(id_field.get(entity));
            Field[] declaredFields = entityClass.getDeclaredFields();
            for (Field field : declaredFields) {
                field.setAccessible(true);
                Object o = field.get(entity);
                if (o != null) criteria.and(field.getName()).is(o);
            }
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return criteria;
    }

}
