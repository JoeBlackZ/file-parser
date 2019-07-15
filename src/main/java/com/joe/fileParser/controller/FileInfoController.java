package com.joe.fileParser.controller;

import com.joe.fileParser.common.ResponseResult;
import com.joe.fileParser.model.FileInfo;
import com.joe.fileParser.service.FileInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@Controller
@RequestMapping("/fileInfo")
public class FileInfoController {

    @Resource
    private FileInfoService fileInfoService;

    @ResponseBody
    @GetMapping("/queryList")
    public ResponseResult queryList(FileInfo fileInfo) {
        return this.fileInfoService.findByPage(fileInfo);
    }

    @ResponseBody
    @PostMapping("/upload")
    public ResponseResult upload(MultipartFile file) {
        return this.fileInfoService.uploadFile(file);
    }

    @ResponseBody
    @RequestMapping("/deleteBatch")
    public ResponseResult deleteBatch(@RequestParam(value = "ids[]") String[] ids) {
        return this.fileInfoService.deleteFileByIds(ids);
    }
}
