package com.joe.fileParser.repository;

import cn.hutool.core.io.FileUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.activation.MimetypesFileTypeMap;
import javax.annotation.Resource;
import java.io.*;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GridFSRepositoryTest {

    @Resource
    private GridFSRepository gridFSRepository;

    @Test
    public void store() {

        try (FileInputStream fileInputStream = new FileInputStream("F:\\\\test\\\\video_excel_ppt\\\\trackerd.log")){
            ObjectId store = this.gridFSRepository.store(fileInputStream, "trackerd.log", new Document("author", "joeblackz"));
            System.err.println(store);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getFileMd5() {
        try (FileInputStream fileInputStream = new FileInputStream("F:\\\\test\\\\video_excel_ppt\\\\trackerd.log")){
            String s = DigestUtil.md5Hex(fileInputStream);
            System.err.println(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void delete() {
        this.gridFSRepository.deleteById("5d1b22b40deda338b09518c0");
    }

    @Test
    public void findAll() {
        GridFSFindIterable gridFSFiles = this.gridFSRepository.findAll();
        for (GridFSFile next : gridFSFiles) {
            System.err.println("id:" + next.getObjectId().toString());
            System.err.println("getFilename:" + next.getFilename());
            System.err.println("getLength:" + next.getLength());
            System.err.println("getUploadDate:" + next.getUploadDate());
            System.err.println("getMetadata:" + next.getMetadata());
//            System.err.println("getMetadata:" + next.getMD5());
        }
    }

    @Test
    public void downloadFile() {
        try (InputStream inputStream = this.gridFSRepository.download("5d1b39780deda33ab4ad30af");
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))){

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.err.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void downloadByMetadata(){
        List<GridFsResource> download = this.gridFSRepository.download(new Document("author", "joeblackz"));
        download.forEach(gridFsResource -> {
            try (InputStream inputStream = gridFsResource.getInputStream()){
                FileUtil.writeFromStream(inputStream, "f:\\fileParser\\" + gridFsResource.getFilename());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void getFileContentType () {
        File file = new File("F:\\\\test\\\\video_excel_ppt\\\\新建 Microsoft PowerPoint 演示文稿.pptx");
        String contentType = new MimetypesFileTypeMap().getContentType(file);
        System.err.println(contentType);

    }
}