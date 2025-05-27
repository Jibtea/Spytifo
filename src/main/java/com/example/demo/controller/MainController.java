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
}
