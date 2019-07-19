package com.joe.fileParser.repository;

import com.joe.fileParser.model.BaseModel;
import com.joe.fileParser.model.FileInfoEs;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;

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

    public String deleteById(ID id) {
        return this.elasticsearchTemplate.delete(entityClass, id.toString());
    }

    public void deleteByIds(Object[] ids) {
        Criteria criteria = new Criteria().and("_id").in(ids);
        this.elasticsearchTemplate.delete(new CriteriaQuery(criteria), entityClass);
    }

    public void search(String keyword, int pageNum, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.queryStringQuery(keyword).analyzer("standard"))
                .withHighlightFields(
                        new HighlightBuilder.Field("name").fragmentOffset(20).preTags("<tag>").postTags("</tag>"),
                        new HighlightBuilder.Field("content").fragmentOffset(20).preTags("<tag>").postTags("</tag>")
                )
                .withPageable(pageRequest)
                .build();
        this.elasticsearchTemplate.queryForPage(searchQuery, entityClass, new SearchResultMapper(){
            @Override
            public <E> AggregatedPage<E> mapResults(SearchResponse response, Class<E> clazz, Pageable pageable) {
                SearchHits hits = response.getHits();
                hits.forEach(hit -> {
                    Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                    highlightFields.forEach((fieldName, highlith) -> {
                        System.err.println("fieldName: " + fieldName);
                        System.err.println("highlith name: " + highlith.getName());
                        System.err.println("fragments: " + Arrays.toString(highlith.getFragments()));
                    });
//                    String id = hit.getId();
//                    Map<String, Object> sourceAsMap = hit.getSourceAsMap();
//                    sourceAsMap.forEach((key, value) -> {
//                        System.err.println(key + ": " + value);
//                    });
                });
                return null;
            }
        });
    }
}
