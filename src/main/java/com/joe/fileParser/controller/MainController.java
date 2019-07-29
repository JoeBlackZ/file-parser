package com.joe.fileParser.controller;

import cn.hutool.core.util.StrUtil;
import com.joe.fileParser.common.ResponseResult;
import com.joe.fileParser.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/")
public class MainController {

    @GetMapping("/")
    public String root() {
        return "login";
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseResult login(String account, String password, HttpServletRequest request) {
        if (StrUtil.isBlank(account))
            return ResponseResult.fail().msg("请输入用户名");
        if (StrUtil.isBlank(password))
            return ResponseResult.fail().msg("请输入密码");
        if ("admin".equals(account) && "123456".equals(password)) {
            request.getSession().setAttribute("loginUser", new User(account, password));
            return ResponseResult.success().msg("登陆成功。");
        } else {
            return ResponseResult.fail().msg("用户名或密码错误");
        }
    }

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/loginOut")
    public String loginOut(HttpSession session) {
        session.invalidate();
        return "/";
    }
}
