package com.joe.fileParser.parser;

import cn.hutool.core.io.FileUtil;
import com.joe.fileParser.model.TikaModel;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.ExpandedTitleContentHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.xml.sax.SAXException;

import javax.annotation.Resource;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.StandardCharsets;

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
        try (FileInputStream fileInputStream = new FileInputStream(txt)) {
            TikaModel parse = this.tikaParser.parse(fileInputStream);
            System.err.println(parse);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void parseToHtml() throws TikaException, SAXException, IOException, TransformerConfigurationException {
        byte[] bytes = FileUtil.readBytes("E:\\dev\\document\\book\\持续集成工具Jenkins.pdf");
        AutoDetectParser tikaParser = new AutoDetectParser();
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BufferedWriter bufferedWriter = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream("C:\\Users\\JoezBlackZ\\Desktop\\index.html")));
        SAXTransformerFactory factory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
        TransformerHandler handler = factory.newTransformerHandler();
        handler.getTransformer().setOutputProperty(OutputKeys.METHOD, "html");
        handler.getTransformer().setOutputProperty(OutputKeys.INDENT, "yes");
        handler.getTransformer().setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        handler.setResult(new StreamResult(bufferedWriter));
        ExpandedTitleContentHandler handler1 = new ExpandedTitleContentHandler(handler);
        tikaParser.parse(new ByteArrayInputStream(bytes), handler1, new Metadata());
    }
}