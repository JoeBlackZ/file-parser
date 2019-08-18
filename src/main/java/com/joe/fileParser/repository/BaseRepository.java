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

    /**
     * spring data mongodb 模板
     */
    @Resource
    private MongoTemplate mongoTemplate;

    /**
     * 子类class
     */
    private Class<T> entityClass;

    /**
     * BaseRepository构造方法
     * 在构造方法红获取子类的class
     */
    BaseRepository() {
        ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        Type[] actualTypeArguments = (genericSuperclass).getActualTypeArguments();
        entityClass = (Class<T>) actualTypeArguments[0];
    }

    /**
     * 保存一条数据，返回保存后的数据
     * @param entity 要保存的数据
     * @return 返回带有id的数据
     */
    public T insert(T entity) {
        return this.mongoTemplate.insert(entity);
    }

    /**
     * 批量保存数据
     * @param list 要保存的数据
     * @return 返回保存的多个数据，带有id属性
     */
    public Collection<T> insertAll(Collection<T> list) {
        return this.mongoTemplate.insertAll(list);
    }

    /**
     * 根据id更新数据
     * 只要实体参数中的属性不为null就更新该属性
     * @param baseModel 更新参数
     * @return 返回更新条数
     * @throws IllegalAccessException 非法访问异常
     */
    public long updateById(BaseModel baseModel) throws IllegalAccessException {
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

    /**
     * 根据多个id删除数据
     * @param ids 一个或多个id（可变参数）
     * @return 返回删除的数量
     */
    public long deleteByIds(ID... ids) {
        DeleteResult deleteResult = this.mongoTemplate.remove(new Query(Criteria.where("_id").in((Object[]) ids)), entityClass);
        return deleteResult.getDeletedCount();
    }

    /**
     * 根据多个id删除数据
     * @param ids 集合参数id
     * @return 返回删除的数量
     */
    public long deleteByIds(Collection<? extends Serializable> ids) {
        DeleteResult deleteResult = this.mongoTemplate.remove(new Query(Criteria.where("_id").in(ids)), entityClass);
        return deleteResult.getDeletedCount();
    }

    /**
     * 根据id删除单条数据
     * @param id 主键id
     * @return 返回删除数量
     */
    public long deleteById(Serializable id) {
        DeleteResult deleteResult = this.mongoTemplate.remove(new Query(Criteria.where("_id").is(id)), entityClass);
        return deleteResult.getDeletedCount();
    }

    /**
     * 查询所有数据
     * @return 返回所有的数据
     */
    public List<T> findAll() {
        return this.mongoTemplate.findAll(entityClass);
    }

    /**
     * 分页查询数据
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 返回该页数据
     */
    public List<T> findAllByPage(int pageNum, int pageSize) {
        Query query = new Query().skip((pageNum - 1) * pageSize).limit(pageSize);
        return this.mongoTemplate.find(query, entityClass);
    }

    /**
     * 根据实体对象的参数查询结果集
     * 只要实体对象中的属性部位null即为查询条件
     * @param baseModel 查询条件
     * @return 返回匹配的结果
     * @throws IllegalAccessException 非法访问异常
     */
    public List<T> find(BaseModel baseModel) throws IllegalAccessException {
        Criteria criteria = this.getCriteria(baseModel);
        return this.mongoTemplate.find(new Query(criteria), entityClass);
    }

    /**
     * 根据实体对象中的属性以及其中的分页信息查询数据
     * @param baseModel 查询参数（包含分页参数）
     * @return 返回匹配的结果
     * @throws IllegalAccessException 非法访问异常
     */
    public List<T> findByPage(BaseModel baseModel) throws IllegalAccessException {
        Criteria criteria = this.getCriteria(baseModel);
        Query query = new Query(criteria).skip((baseModel.getPage() - 1) * baseModel.getLimit()).limit(baseModel.getLimit());
        return this.mongoTemplate.find(query, entityClass);
    }

    /**
     * 查询一条数据，既是匹配到多个结果也只返回第一条
     * @param baseModel 查询参数
     * @return 返回匹配结果
     * @throws IllegalAccessException 非法访问异常
     */
    public T findOne(BaseModel baseModel) throws IllegalAccessException {
        return this.mongoTemplate.findOne(new Query(this.getCriteria(baseModel)), entityClass);
    }

    /**
     * 根据id查询一条结果
     * @param id 主键id
     * @return 返回一条数据
     */
    public T findById(ID id) {
        return this.mongoTemplate.findById(id, entityClass);
    }

    /**
     * 统计该集合中的数据量
     * @return 数据量
     */
    public long count() {
        return this.mongoTemplate.count(new Query(), entityClass);
    }

    /**
     * 根据条件查询数据量
     * @param baseModel 查询参数
     * @return 数据量
     * @throws IllegalAccessException 非法访问异常
     */
    public long count(BaseModel baseModel) throws IllegalAccessException {
        return this.mongoTemplate.count(new Query(this.getCriteria(baseModel)), entityClass);
    }

    /**
     * 根据实体参数利用反射获取其中的值组成查询条件
     * @param baseModel 查询参数
     * @return 返回查询条件
     * @throws IllegalAccessException 非法访问异常
     */
    private Criteria getCriteria(BaseModel baseModel) throws IllegalAccessException {
        Criteria criteria = new Criteria();
        if (baseModel.getId() != null)
            criteria.and("_id").is(baseModel.getId());

        Field[] declaredFields = entityClass.getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            Object o = field.get(baseModel);
            if (o != null) criteria.and(field.getName()).is(o);
        }
        return criteria;
    }

}
