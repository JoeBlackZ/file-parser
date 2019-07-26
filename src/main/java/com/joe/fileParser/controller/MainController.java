package com.joe.fileParser.controller;

import com.joe.fileParser.common.ResponseResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class MainController {

    @GetMapping("/")
    public String index() {
        return "login";
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseResult login(String userName, String password) {
        return ResponseResult.success().msg("登陆成功。");
    }
}
