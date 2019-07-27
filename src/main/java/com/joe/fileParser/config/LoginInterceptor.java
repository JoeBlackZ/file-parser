package com.joe.fileParser.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
public class LoginInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        final String requestURI = request.getRequestURI();
        if (this.ignore(requestURI)) {
            return true;
        } else if (request.getSession().getAttribute("loginUser") == null){
            response.sendRedirect("/");
            return false;
        } else {
            return true;
        }
    }

    private boolean ignore(String uri) {
        if ("/".equals(uri)) return true;
        String[] uris = new String[]{
                "login",
                "plugins",
                "css",
                "js"

        };
        for (String str : uris) {
            if (uri.contains(str))
                return true;
        }
        return false;
    }
}
