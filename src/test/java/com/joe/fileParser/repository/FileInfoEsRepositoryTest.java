package com.joe.fileParser.repository;

import cn.hutool.core.io.FileUtil;
import com.joe.fileParser.model.FileInfoEs;
import org.elasticsearch.action.admin.indices.get.GetIndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.nio.charset.Charset;
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
    public void findById() {
        String id = "5d3336037991e831287b9239";
        FileInfoEs byId = this.fileInfoEsRepository.findByDocumentId(id);
        FileUtil.writeString(byId.getContent(), "C:\\Users\\JoezBlackZ\\Desktop\\index2.html", Charset.defaultCharset());
    }
    @Test
    public void findByFieldId() {
        String id = "5d3670bda7c4bc3e30fb61da";
        FileInfoEs byId = this.fileInfoEsRepository.findByDocumentId(id);
        System.err.println(byId);
    }

    @Test
    public void highlight() {
        String word = "admin";
        final List<FileInfoEs> sql = this.fileInfoEsRepository.search(word, 0, 10);
        System.err.println(sql);
    }

    @Test
    public void testWildcardQuery() {
        String keyword = "新建*";
        WildcardQueryBuilder wildcardQueryBuilder = QueryBuilders.wildcardQuery("name", keyword);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().query(wildcardQueryBuilder);
        SearchRequest request = new SearchRequest("file_info").types("fileInfo").source(searchSourceBuilder);
        SearchResponse searchResponse = this.elasticsearchTemplate.getClient().search(request).actionGet();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            System.err.println(hit.getSourceAsMap().get("name"));
        }
    }

    @Test
    public void testTermQuery() {
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.termsQuery("name", "trackerd.log", "app.properties"));
        List<FileInfoEs> list = this.elasticsearchTemplate.queryForList(nativeSearchQueryBuilder.build(), FileInfoEs.class);
        list.forEach(fileInfoEs -> System.err.println(fileInfoEs.getName()));
    }

    @Test
    public void testMatchAll() {
        NativeSearchQuery build = new NativeSearchQueryBuilder()
                .withQuery(
                        QueryBuilders.matchQuery("name", "新建 Microsoft")
                        .operator(Operator.OR)
                )
                .build();
        FileInfoEs query = this.elasticsearchTemplate.query(build, response -> {
            for (SearchHit hit : response.getHits().getHits()) {
                System.err.println(hit.getSourceAsMap().get("name"));
            }
            return null;
        });
    }

    @Test
    public void testMultiMatchQuery() {
        NativeSearchQuery build = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("新建 Microsoft", "name", "content"))
                .build();
        FileInfoEs query = this.elasticsearchTemplate.query(build, response -> {
            for (SearchHit hit : response.getHits().getHits()) {
                System.err.println(hit.getSourceAsMap().get("name"));
            }
            return null;
        });
    }

    @Test
    public void testBoolQuery() {
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "新建");
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("name", "Microsoft").operator(Operator.AND);
        QueryStringQueryBuilder powerPoint = queryStringQuery("PowerPoint");
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().must(termQueryBuilder).should(matchQueryBuilder).mustNot(powerPoint);
        NativeSearchQuery build = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build();
        this.elasticsearchTemplate.query(build, response -> {
            for (SearchHit hit : response.getHits().getHits()) {
                System.err.println(hit.getSourceAsMap().get("name"));
            }
            return null;
        });
    }

    @Test
    public void testSQuery() {


    }
}