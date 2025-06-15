package com.example.demo.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Controller
public class MainController {
    @GetMapping
    public String index() {
        return "index";
    }

    @GetMapping("/testTable")
    public String testTable(HttpSession session) {
        return "testTable";
    }

    @GetMapping("/hello")
    public String hello(Model model, HttpSession session) {
        model.addAttribute("name", session.getAttribute("name") != null ? session.getAttribute("name") : "World");
        return "hello";
    }


    @GetMapping("/topItem")
    public String showList(Model model,HttpSession session) {
        // add attributes to model
        String token = (String) session.getAttribute("spotifyToken");
        if (token == null) {
            return "redirect:/login";
        }
        return "topItem";
    }


//    ==========dataTable============
    @GetMapping("/component/artistTable")
    public String getArtistTableFragment() {
        return "component/artistTable :: artistTable"; // ชี้ไปที่ fragment
    }

    @GetMapping("/component/trackTable")
    public String getTrackTableFragment() {
        return "component/trackTable :: trackTable"; // ชี้ไปที่ fragment
    }
}




//@GetMapping("/topSong")
//    public String showSong(Model model,HttpSession session) {
//        // add attributes to model
//        String token = (String) session.getAttribute("token");
//        return "topSong";
//    }
