package com.joe.fileParser.repository;

import com.joe.fileParser.model.BaseModel;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@SuppressWarnings("unchecked")
public abstract class BaseEsRepository<T, ID extends Serializable> {

    @Resource
    ElasticsearchTemplate elasticsearchTemplate;

    private Class<T> entityClass;

    BaseEsRepository() {
        ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        Type[] actualTypeArguments = (genericSuperclass).getActualTypeArguments();
        entityClass = (Class<T>) actualTypeArguments[0];
    }

    public String insert(BaseModel baseModel) {
        this.elasticsearchTemplate.createIndex(entityClass);
        IndexQuery indexQuery = new IndexQueryBuilder()
                .withObject(baseModel)
                .withId(baseModel.getId())
                .build();
        return this.elasticsearchTemplate.index(indexQuery);
    }

    public T findByDocumentId(ID id) {
        GetQuery getQuery = new GetQuery();
        getQuery.setId(id.toString());
        return this.elasticsearchTemplate.queryForObject(getQuery, entityClass);
    }

    public T findByFieldId(ID id) {
        Criteria criteria = new Criteria().and("id").is(id);
        return this.elasticsearchTemplate.queryForObject(new CriteriaQuery(criteria), entityClass);
    }

    public String deleteById(ID id) {
        return this.elasticsearchTemplate.delete(entityClass, id.toString());
    }

    public void deleteByIds(Object[] ids) {
        Criteria criteria = new Criteria().and("_id").in(ids);
        this.elasticsearchTemplate.delete(new CriteriaQuery(criteria), entityClass);
    }

}
