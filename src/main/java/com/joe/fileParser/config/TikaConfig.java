package com.joe.fileParser.config;

import org.apache.tika.Tika;
import org.apache.tika.parser.AutoDetectParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 将解析器交由spring管理
*/
@Configuration
public class TikaConfig {

    @Bean
    public Tika tika() {
        Tika tika = new Tika();
        // set MaxStringLength , the default length is 100 * 1000
        tika.setMaxStringLength(1000 * 1000);
        return tika;
    }

    @Bean
    public AutoDetectParser autoDetectParser() {
        return new AutoDetectParser();
    }

}
