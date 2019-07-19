package com.joe.fileParser.repository;

import com.joe.fileParser.model.FileInfoEs;
import org.elasticsearch.action.admin.indices.get.GetIndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FileInfoEsRepositoryTest {

    @Resource
    private FileInfoEsRepository fileInfoEsRepository;

    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;

    @Test
    public void getIndex() {
        GetIndexResponse response = this.elasticsearchTemplate.getClient().admin().indices().prepareGetIndex().execute().actionGet();
        System.out.println(response.getIndices().length);
        String[] indexes = response.getIndices();
        for (String index : indexes) {
            System.err.println(index);
        }
    }

    @Test
    public void insert() {
        FileInfoEs fileInfoEs = new FileInfoEs();
        fileInfoEs.setId("1");
        fileInfoEs.setName("test_file");
        fileInfoEs.setContent("content");
        String insert = this.fileInfoEsRepository.insert(fileInfoEs);
        System.err.println(insert);
    }

    @Test
    public void deleteById() {
        String s = this.fileInfoEsRepository.deleteById("Ulia72sBA2kq0qnzKWwp");
        System.err.println(s);
    }

    @Test
    public void deleteByIds() {
        String[] ids = {"5d2b2f6d4163d82478118fe4", "VFjI72sBA2kq0qnz-mxu", "5d2afa5a4163d82478118fe3"};
        this.fileInfoEsRepository.deleteByIds(ids);
    }

    @Test
    public void search() {
        String word = "附件3：";
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(queryStringQuery(word)).build();
        List<FileInfoEs> fileInfoEs = elasticsearchTemplate.queryForList(searchQuery, FileInfoEs.class);
        System.err.println(fileInfoEs);
    }

    @Test
    public void highlight() {
        String word = "admin";
        PageRequest pageRequest = PageRequest.of(0, 10);
//        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", word);
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
//                .withQuery(termQueryBuilder)
                .withQuery(QueryBuilders.queryStringQuery(word).analyzer("standard"))
                .withHighlightFields(
                        new HighlightBuilder.Field("name").fragmentOffset(20).preTags("<tag>").postTags("</tag>"),
                        new HighlightBuilder.Field("content").fragmentOffset(20).preTags("<tag>").postTags("</tag>")
                )
                .withPageable(pageRequest)
                .build();
        this.elasticsearchTemplate.queryForPage(searchQuery, FileInfoEs.class, new SearchResultMapper(){
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
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