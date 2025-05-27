package com.example.demo.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Controller
public class UserController {
    @GetMapping("/profile")
    public String UserData(HttpSession session, Model model) {
        String token = (String) session.getAttribute("spotifyToken");
        model.addAttribute("token", token);

        if(token == null) {
            return "profile";
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        headers.set("Authorization", "Bearer " + token);

        ResponseEntity<Map> response = restTemplate.exchange(
                "https://api.spotify.com/v1/me",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Map.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            Map<String, Object> userData = response.getBody();
            model.addAttribute("user", userData);

            session.setAttribute("userData", userData);
            System.out.println(session.getAttribute("userData"));
        } else {
            model.addAttribute("error", "Cannot get user data");
        }


        return "profile";
    }

//    @PostMapping
//    public String UserGetTopSong(HttpSession session, Model model) {
//        model.addAttribute("limit",limit);
//        return "profile";
//
//    }
}

//
//String token = (String) session.getAttribute("spotifyToken");
//Map<String, String> userData = (Map<String, String>) session.getAttribute("userData");
//String id = userData.get("id");
//
//RestTemplate restTemplate = new RestTemplate();
//HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer " + token);
////ไว้ส่งขอapi
//HttpEntity<String> request = new HttpEntity<>(headers);
//ResponseEntity<Map> response = restTemplate.exchange(
//        "https://api.spotify.com/v1/me/top/{type}?limit={limit}",
//        HttpMethod.GET,
//        request,
//        Map.class
//);
