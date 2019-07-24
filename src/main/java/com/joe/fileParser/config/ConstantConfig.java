package com.joe.fileParser.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ConstantConfig {

    /**
     * 文件压缩类型
     */
    public static List<String> compressionFileTypes;

    /**
     * 压缩文件临时存储路径
     */
    public static final String SYSTEM_TEMP_PATH = System.getProperty("java.io.tmpdir");

    /**
     * windows 环境下的WinRAR文件解压命令所在目录
     */
    public static String winRarHome;

//    @Value("${constant.compression-file-types}")
//    public void setCompressionFileTypes(List<String> compressionFileTypes) {
//        ConstantConfig.compressionFileTypes = compressionFileTypes;
//    }

//    @Value("${constant.win-rar-home}")
//    public void setWinRarHome(String winRarHome) {
//        ConstantConfig.winRarHome = winRarHome;
//    }
}
