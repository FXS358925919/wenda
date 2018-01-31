package com.fxs.wenda.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Controller
public class IndexController {
    @RequestMapping("/index")
    @ResponseBody
    public String index(){
        return "Hello NowCoder";
    }

    @RequestMapping("/home")
    public String template(Model model) {
        model.addAttribute("name", "world");
        List<String> colors = Arrays.asList(new String[]{"RED", "GREEN", "BLUE"});
        model.addAttribute("colors", colors);
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < 4; ++i) {
            map.put(String.valueOf(i), String.valueOf(i * i));
        }
        //model.addAttribute("map", map);
        return "home";
    }

    @RequestMapping("/request")
    @ResponseBody
    public String request(HttpServletRequest request){
        StringBuilder builder = new StringBuilder();
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                builder.append("Cookie:" + cookie.getName() + " value:" + cookie.getValue());
            }
        }

        builder.append("a");
        return builder.toString();
    }
}
