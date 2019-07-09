package com.joe.fileParser.parser;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.ToTextContentHandler;
import org.springframework.stereotype.Component;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.*;

@Component
public class FileParser {

    public String parse(InputStream inputStream) {
        AutoDetectParser parser = new AutoDetectParser();
        ContentHandler handler = new ToTextContentHandler();
        Metadata metadata = new Metadata();
        try {
            parser.parse(inputStream, handler, metadata);
        } catch (IOException | TikaException | SAXException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return handler.toString();
    }

    public String parse(File file) {
        try (FileInputStream fileInputStream = new FileInputStream(file)){
            return this.parse(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String parse(String filepath) {
        return this.parse(new File(filepath));
    }

    public String getFileContentType (InputStream inputStream) {
        try {
            return new Tika().detect(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String getFileContentType (File file) {
        try (FileInputStream fileInputStream = new FileInputStream(file)){
            return new Tika().detect(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getFileContentType (String filepath) {
        try (FileInputStream fileInputStream = new FileInputStream(new File(filepath))){
            return new Tika().detect(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
