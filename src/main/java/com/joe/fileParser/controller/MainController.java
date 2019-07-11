package com.joe.fileParser.controller;

import com.joe.fileParser.common.ResponseResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/")
public class MainController {

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @ResponseBody
    @PostMapping("/upload")
    public ResponseResult upload(MultipartFile file) {
        System.err.println(file.getName());
        System.err.println(file.getOriginalFilename());
        return ResponseResult.success().message("success.").data(file.getOriginalFilename());
    }

}
