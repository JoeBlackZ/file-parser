package com.joe.fileParser.controller;

import com.joe.fileParser.common.ResponseResult;
import com.joe.fileParser.model.FileInfo;
import com.joe.fileParser.service.FileInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

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


    @GetMapping("/preview/{fileInfoId}")
    public String preview(@PathVariable String fileInfoId, Model model) {
        model.addAttribute("fileInfo", this.fileInfoService.findById(fileInfoId).getData());
        return "preview";
    }

    @GetMapping("/previewData/{fileInfoId}")
    public void previewData(@PathVariable String fileInfoId, HttpServletResponse response) {
        this.fileInfoService.previewFileContent(fileInfoId, response);
    }

    @ResponseBody
    @PostMapping("/deleteBatch")
    public ResponseResult deleteBatch(@RequestParam(value = "ids[]") String[] ids) {
        return this.fileInfoService.deleteFileByIds(ids);
    }

    @GetMapping("/download/{filInfoId}")
    public void download(@PathVariable String filInfoId, HttpServletResponse response) {
        this.fileInfoService.downloadFile(filInfoId, response);
    }

    @ResponseBody
    @PostMapping("/compressFile")
    public ResponseResult compressFile(@RequestParam(value = "ids[]") String[] ids) {
        return this.fileInfoService.compressFile(ids);
    }

    @GetMapping("/downloadBatch/{downloadId}")
    public void downloadBatch(@PathVariable String downloadId, HttpServletResponse response) {
        this.fileInfoService.downloadCompressFile(downloadId, response);
    }
}
