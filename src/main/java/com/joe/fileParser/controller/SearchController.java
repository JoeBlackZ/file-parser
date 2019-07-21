package com.joe.fileParser.controller;

import com.joe.fileParser.common.ResponseResult;
import com.joe.fileParser.model.FileInfoEs;
import com.joe.fileParser.service.FileInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/search")
public class SearchController {

    @Resource
    private FileInfoService fileInfoService;

    @GetMapping("/search/{keyword}")
    public String search(@PathVariable String keyword, HttpServletRequest request) {
        request.setAttribute("keyword", keyword);
        return "search";
    }

    @ResponseBody
    @PostMapping("/doSearch")
    public ResponseResult doSearch(FileInfoEs fileInfoEs) {
        return this.fileInfoService.searchFile(fileInfoEs);
    }

}
