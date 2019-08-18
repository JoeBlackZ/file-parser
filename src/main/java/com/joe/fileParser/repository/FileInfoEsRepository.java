package com.joe.fileParser.repository;

import com.joe.fileParser.model.FileInfoEs;
import org.apache.commons.lang3.StringUtils;
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

    /**
     * 根据关键字进行文件高亮的搜索
     * @param keyword 关键字
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 返回文件的搜索结果（高亮结果）
     */
    public List<FileInfoEs> search(String keyword, int pageNum, int pageSize) {
        List<FileInfoEs> list = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.queryStringQuery(keyword).analyzer("ik_smart"))
                .withHighlightFields(
                        new HighlightBuilder.Field("name").preTags("<tag>").postTags("</tag>"),
                        new HighlightBuilder.Field("content").preTags("<tag>").postTags("</tag>")
                )
                .withPageable(pageRequest)
                .build();
        this.elasticsearchTemplate.queryForPage(searchQuery, FileInfoEs.class, new SearchResultMapper() {
            @Override
            public <E> AggregatedPage<E> mapResults(SearchResponse response, Class<E> clazz, Pageable pageable) {
                SearchHits hits = response.getHits();
                hits.forEach(hit -> {
                    FileInfoEs fileInfoEs = new FileInfoEs();
                    fileInfoEs.setId(hit.getId());
                    fileInfoEs.setName(hit.getSourceAsMap().get("name").toString());
                    fileInfoEs.setContent(StringUtils.substring(hit.getSourceAsMap().get("content").toString(), 0, 200));
                    hit.getHighlightFields().forEach((fieldName, highlight) -> {
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

    /**
     * 将多个高亮结果拼接
     * @param texts 高亮结果
     * @return 返回拼接的高亮结果
     */
    private String textsToString(Text[] texts) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Text text : texts) {
            stringBuilder.append(text);
        }
        return stringBuilder.toString();
    }

}
