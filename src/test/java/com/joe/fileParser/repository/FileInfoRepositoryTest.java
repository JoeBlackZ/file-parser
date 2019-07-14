package com.joe.fileParser.repository;

import com.joe.fileParser.model.FileInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FileInfoRepositoryTest {

    @Resource
    private FileInfoRepository fileInfoRepository;

    @Test
    public void insert() {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setName("test1");
        fileInfo.setContentType("application/json");
        FileInfo insert = this.fileInfoRepository.insert(fileInfo);
        System.err.println(insert);
    }

    @Test
    public void insertAll() {
        List<FileInfo> list = new ArrayList<>();
        for (int i = 0; i < 90; i ++) {
            FileInfo fileInfo = new FileInfo();
            fileInfo.setName("test" + (i + 1));
            fileInfo.setContentType("application/json");
            list.add(fileInfo);
        }
        Collection<FileInfo> fileInfos = this.fileInfoRepository.insertAll(list);
        System.err.println(fileInfos);
    }

    @Test
    public void updateById() throws Exception{
        FileInfo fileInfo = new FileInfo();
        fileInfo.setId("5d28ac781c9f040278be6c6b");
        fileInfo.setName("test");
        fileInfo.setContentType("text/plain");
        long l = this.fileInfoRepository.updateById(fileInfo);
        System.err.println(l);
    }

    @Test
    public void deleteByIds() {
    }

    @Test
    public void deleteById() {
    }

    @Test
    public void findAll() {
        List<FileInfo> all = this.fileInfoRepository.findAll();
        System.err.println(all);
    }

    @Test
    public void findAllByPage() {
        List<FileInfo> allByPage = this.fileInfoRepository.findAllByPage(1, 2);
        System.err.println(allByPage);
    }

    @Test
    public void find() throws Exception{
        FileInfo fileInfo = new FileInfo();
        fileInfo.setId("5d28ac781c9f040278be6c6b");
        List<FileInfo> fileInfoList = this.fileInfoRepository.find(fileInfo);
        System.err.println(fileInfoList);
    }

    @Test
    public void findByPage() throws Exception{
        FileInfo fileInfo = new FileInfo();
        fileInfo.setPage(1);
        fileInfo.setLimit(10);
        fileInfo.setId("5d28ac781c9f040278be6c6b");
        List<FileInfo> byPage = this.fileInfoRepository.findByPage(fileInfo);
        System.err.println(byPage);
    }

    @Test
    public void findOne() {
    }

    @Test
    public void findById() {
    }

    @Test
    public void count() {
    }

    @Test
    public void count1() {
    }
}