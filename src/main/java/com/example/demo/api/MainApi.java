package com.example.demo.api;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MainApi {
    @PostMapping("/hello")
    public void hello(HttpSession session, @RequestParam String name) {
        session.setAttribute("name", name);
    }

    /*
    @PostMapping("/topItem")
    public ResponseEntity<?> topItem(HttpSession session, @RequestBody Map<String, String> map) {
        String type = map.get("type");
        int limit = Integer.parseInt(map.get("limit"));

        String token = (String) session.getAttribute("spotifyToken");

        return ResponseEntity.ok(token);
    }*/

    @PostMapping("/topItem")
    public ResponseEntity<?> topItem(
            HttpSession session,
            @RequestBody Map<String,String> body
            ) {
        String type = (String) body.get("type");
        int limit = Integer.parseInt(body.get("limit"));


        String token = (String) session.getAttribute("spotifyToken");


        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        String Url = "https://api.spotify.com/v1/me/top/" + type + "?limit=" + limit;
        //ไว้ส่งขอapi
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(
                Url,
                HttpMethod.GET,
                request,
                Map.class
        );

        List<Map<String, Object>> items = (List<Map<String, Object>>) response.getBody().get("items");
        return ResponseEntity.ok(items);
    }
}