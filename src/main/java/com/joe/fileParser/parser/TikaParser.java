package com.joe.fileParser.parser;

import com.joe.fileParser.model.TikaModel;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.WriteOutContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class TikaParser {

    @Resource
    private Tika tika;

    @Resource
    private AutoDetectParser autoDetectParser;

    private Logger logger = LoggerFactory.getLogger(TikaParser.class);

    /**
     * parse file content and getting file metadata
     *
     * @param inputStream file inputStream
     * @param closeStream true mean close inputStream after parse, false not close
     * @return file string content and file metadata
     */
    public TikaModel parse(@NotNull InputStream inputStream, boolean closeStream) {
        try {
            if (inputStream == null || inputStream.available() == 0) return null;

            Metadata metadata = new Metadata();
            String content;

            if (closeStream) {
                // use tika bean will close inputStream when after parse
                content = this.tika.parseToString(inputStream, metadata);
            } else {
                // use autoDetectParser will not close
//                BodyContentHandler handler = new BodyContentHandler();
                ParseContext context = new ParseContext();
                // set MaxStringLength , the default length is 100 * 1000
                WriteOutContentHandler handler = new WriteOutContentHandler(tika.getMaxStringLength());
                this.autoDetectParser.parse(inputStream, handler, metadata, context);
                content = handler.toString();
            }

            if (logger.isInfoEnabled())
                logger.info("parse file finished.");
            // parse result
            return new TikaModel(content, this.getMetadata(metadata));
        } catch (IOException | TikaException | SAXException e) {
            if (logger.isErrorEnabled())
                logger.error("parse file error, exception message: {}", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * put all metadata to map
     *
     * @param metadata file metadata
     * @return metadata in map
     */
    private Map<String, Object> getMetadata(Metadata metadata) {
        if (metadata == null || metadata.size() == 0)
            return Collections.emptyMap();

        Map<String, Object> map = new HashMap<>();
        for (String name : metadata.names()) {
            map.put(name, metadata.get(name));
        }
        return map;
    }

    /**
     * parse file
     *
     * @param file file need to be parsed
     * @return parse result
     */
    public TikaModel parse(@NotNull File file) {
        try {
            if (file != null && file.exists())
                return this.parse(new FileInputStream(file), true);
        } catch (FileNotFoundException e) {
            if (logger.isErrorEnabled())
                logger.error("could not find the file when parsing.");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * parse file and do not close inputStream
     *
     * @param inputStream file inputStream
     * @return parse result
     */
    public TikaModel parse(@NotNull InputStream inputStream) {
        return this.parse(inputStream, true);
    }


}
