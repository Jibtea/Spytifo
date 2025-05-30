package com.example.demo.api;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
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


    @PostMapping("/collectTrack")
    public ResponseEntity<?> collectTrack(
            HttpSession session,
            @RequestParam String trackUrl,
            @RequestParam String deviceId) {

        String token = (String) session.getAttribute("spotifyToken");

        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Spotify token missing.");
        }

        session.setAttribute("trackUrl", trackUrl); // เก็บไว้เผื่อยังอยากใช้ภายหลัง

        // เตรียมเรียก Spotify API
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String body = "{\"uris\": [\"" + trackUrl + "\"]}";
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        String url = "https://api.spotify.com/v1/me/player/play?device_id=" + deviceId;

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    request,
                    String.class
            );
            return ResponseEntity.ok("Track changed and playing.");
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getRawStatusCode()).body(e.getResponseBodyAsString());
        }
    }


}

/*
    @PostMapping("/playTrack")
    public ResponseEntity<?> playTrack(
            HttpSession session,
            @RequestParam String deviceId) {

        String token = (String) session.getAttribute("spotifyToken");
        String trackUrl = (String) session.getAttribute("trackUrl");

        if (token == null || trackUrl == null) {
            return ResponseEntity.badRequest().body("Missing token or track URL in session.");
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // trackUrl ต้องอยู่ใน array
        String body = "{\"uris\": [\"" + trackUrl + "\"]}";

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        String url = "https://api.spotify.com/v1/me/player/play?device_id=" + deviceId;

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    request,
                    String.class
            );
            return ResponseEntity.ok("Track is playing.");
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getRawStatusCode()).body(e.getResponseBodyAsString());
        }
    }
*/