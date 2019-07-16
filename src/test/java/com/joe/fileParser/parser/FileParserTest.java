package com.joe.fileParser.parser;

import com.joe.fileParser.model.TikaModel;
import org.apache.tika.exception.TikaException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FileParserTest {

    @Resource
    private TikaParser tikaParser;

    private File xml = new File("F:test\\video_excel_ppt\\activemq.xml");
    private File properties = new File("F:\\test\\video_excel_ppt\\app.properties");
    private File jpg = new File("F:\\test\\video_excel_ppt\\d36e46f5.jpg");
    private File xlsx = new File("F:\\test\\video_excel_ppt\\file_info.xlsx");
    private File mp4 = new File("F:\\test\\video_excel_ppt\\movie.mp4");
    private File ogv = new File("F:\\test\\video_excel_ppt\\movie.ogv");
    private File pdf = new File("F:\\test\\video_excel_ppt\\mycat-definitive-guide.pdf");
    private File html = new File("F:\\test\\video_excel_ppt\\Spring Data MongoDB - Reference Documentation1.html");
    private File pdf2 = new File("F:\\test\\video_excel_ppt\\technology-radar-vol-19-cn.pdf");
    private File log = new File("F:\\test\\video_excel_ppt\\trackerd.log");
    private File png = new File("F:\\test\\video_excel_ppt\\Untitled Diagram.png");
    private File pptx = new File("F:\\test\\video_excel_ppt\\工作总结计划.pptx");
    private File xlsx2 = new File("F:\\test\\video_excel_ppt\\新建 Microsoft Excel 工作表.xlsx");
    private File pptx2 = new File("F:\\test\\video_excel_ppt\\新建 Microsoft PowerPoint 演示文稿.pptx");
    private File txt = new File("F:\\test\\video_excel_ppt\\新建 文本文档.txt");
    private File docx = new File("F:\\test\\video_excel_ppt\\测试word.docx");
    private File rar = new File("F:\\test\\邮件\\新建数据组.rar");

    @Test
    public void parse1() {
        try (FileInputStream fileInputStream = new FileInputStream(txt)){
            TikaModel parse = this.tikaParser.parse(fileInputStream);
            System.err.println(parse);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}