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

    /**
     * spring data elasticsearch 模板
     */
    @Resource
    ElasticsearchTemplate elasticsearchTemplate;

    /**
     * 子类的class
     */
    private Class<T> entityClass;

    /**
     * 构造方法，在构造方法中获取子类的class
     */
    BaseEsRepository() {
        ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        Type[] actualTypeArguments = (genericSuperclass).getActualTypeArguments();
        entityClass = (Class<T>) actualTypeArguments[0];
    }

    /**
     * 保存记录到elasticsearch中
     * @param baseModel 要保存的参数
     * @return 返回elasticsearch id
     */
    public String insert(BaseModel baseModel) {
        IndexQuery indexQuery = new IndexQueryBuilder()
                .withObject(baseModel)
                .withId(baseModel.getId())
                .build();
        return this.elasticsearchTemplate.index(indexQuery);
    }

    /**
     * 根据文档id查询查询
     * @param id 主键id
     * @return 返回查询结果
     */
    public T findByDocumentId(ID id) {
        GetQuery getQuery = new GetQuery();
        getQuery.setId(id.toString());
        return this.elasticsearchTemplate.queryForObject(getQuery, entityClass);
    }

    /**
     * 根据多个id删除多条数据
     * @param ids 主键id
     */
    public void deleteByIds(Object[] ids) {
        Criteria criteria = new Criteria().and("_id").in(ids);
        this.elasticsearchTemplate.delete(new CriteriaQuery(criteria), entityClass);
    }

}
