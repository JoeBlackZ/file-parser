package com.joe.fileParser.repository;

import com.joe.fileParser.model.FileInfoEs;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class FileInfoEsRepository extends BaseEsRepository<FileInfoEs, String> {

    public List<FileInfoEs> search(String keyword, int pageNum, int pageSize) {
        List<FileInfoEs> list = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.queryStringQuery(keyword))
                .withHighlightFields(
                        new HighlightBuilder.Field("name").fragmentOffset(50).preTags("<tag>").postTags("</tag>"),
                        new HighlightBuilder.Field("content").fragmentOffset(50).preTags("<tag>").postTags("</tag>")
                )
                .withPageable(pageRequest)
                .build();
        this.elasticsearchTemplate.queryForPage(searchQuery, FileInfoEs.class, new SearchResultMapper() {
            @Override
            public <E> AggregatedPage<E> mapResults(SearchResponse response, Class<E> clazz, Pageable pageable) {
                SearchHits hits = response.getHits();
                final long totalHits = hits.getTotalHits();
                hits.forEach(hit -> {
                    FileInfoEs fileInfoEs = new FileInfoEs();
                    fileInfoEs.setId(hit.getId());
                    fileInfoEs.setName(hit.getSourceAsMap().get("name").toString());
                    fileInfoEs.setContent(hit.getSourceAsMap().get("content").toString());
                    hit.getHighlightFields().forEach((fieldName, highlight) -> {
//                        System.err.println("fieldName: " + fieldName);
//                        System.err.println("highlight name: " + highlight.getName());
//                        System.err.println("fragments: " + Arrays.toString(highlight.getFragments()));
                        switch (fieldName) {
                            case "name":
                                fileInfoEs.setName(textsToString(highlight.getFragments()));
                                break;
                            case "content":
                                fileInfoEs.setContent(textsToString(highlight.getFragments()));
                                break;
                        }
                    });
                    list.add(fileInfoEs);
                });
                return null;
            }
        });
        return list;
    }

    private String textsToString(Text[] texts) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Text text : texts) {
            stringBuilder.append(text);
        }
        return stringBuilder.toString();
    }

}
