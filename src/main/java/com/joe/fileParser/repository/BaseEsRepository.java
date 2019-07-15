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
    private ElasticsearchTemplate elasticsearchTemplate;

    private Class<T> entityClass;

    BaseEsRepository() {
        ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        Type[] actualTypeArguments = (genericSuperclass).getActualTypeArguments();
        entityClass = (Class<T>) actualTypeArguments[0];
    }

    public String insert(BaseModel baseModel) {
        IndexQuery indexQuery = new IndexQueryBuilder()
                .withObject(baseModel)
                .withId(baseModel.getId())
                .build();
        return this.elasticsearchTemplate.index(indexQuery);
    }

    public String deleteById(String id) {
        return this.elasticsearchTemplate.delete(entityClass, id);
    }

    public void deleteByIds(Object[] ids) {
        Criteria criteria = new Criteria().and("_id").in(ids);
        this.elasticsearchTemplate.delete(new CriteriaQuery(criteria), entityClass);
    }


}
