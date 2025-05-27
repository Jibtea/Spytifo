package com.example.demo.api;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MainApi {
    @PostMapping("/hello")
    public void hello(HttpSession session, @RequestParam String name) {
        session.setAttribute("name", name);
    }
}
