package com.joe.fileParser.config;

import com.joe.fileParser.model.TikaModel;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Component
public class TikaParser {

    @Resource
    private Tika tika;

    public TikaModel parse(InputStream inputStream) throws IOException, TikaException {
        Metadata metadata = new Metadata();
        String s = this.tika.parseToString(inputStream, metadata);
        Map<String, Object> map = new HashMap<>();
        for (String name : metadata.names()) {
            map.put(name, metadata.get(name));
        }
        return new TikaModel(s, map);
    }

    public TikaModel parse2(InputStream inputStream) throws IOException, TikaException, SAXException {
        Parser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        ParseContext context = new ParseContext();
        parser.parse(inputStream, handler, metadata, context);
        Map<String, Object> map = new HashMap<>();
        for (String name : metadata.names()) {
            map.put(name, metadata.get(name));
        }
        return new TikaModel(handler.toString(), map);
    }

}
