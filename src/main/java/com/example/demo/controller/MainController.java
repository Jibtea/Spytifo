package com.example.demo.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @GetMapping
    public String index() {
        return "index";
    }

    @GetMapping("/hello")
    public String hello(Model model, HttpSession session) {
        model.addAttribute("name", session.getAttribute("name") != null ? session.getAttribute("name") : "World");
        return "hello";
    }

    @GetMapping("/topSong")
    public String showSong(Model model,HttpSession session) {
        // add attributes to model
        String token = (String) session.getAttribute("token");
        return "topSong";
    }

    @GetMapping("/topItem")
    public String showList(Model model,HttpSession session) {
        // add attributes to model
        String token = (String) session.getAttribute("token");
        return "topItem";
    }

}
