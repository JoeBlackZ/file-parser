package com.joe.fileParser.repository;

import com.joe.fileParser.model.FileInfoEs;
import org.elasticsearch.action.admin.indices.get.GetIndexResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

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
        for(String index : indexes){
            System.err.println(index);
        }
    }

    @Test
    public void insert() {
        FileInfoEs fileInfoEs = new FileInfoEs();
        fileInfoEs.setId("1");
        fileInfoEs.setFileName("test_file");
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


}