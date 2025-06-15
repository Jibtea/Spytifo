package com.example.demo.api;

import jakarta.servlet.http.HttpSession;
import org.springframework.boot.web.client.RestTemplateBuilder;
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
    private final RestTemplateBuilder restTemplateBuilder;

    public MainApi(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplateBuilder = restTemplateBuilder;
    }

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
            @RequestBody Map<String, String> body) {
        try {
            String type = body.get("type");
            int limit = Integer.parseInt(body.get("limit"));

            String token = (String) session.getAttribute("spotifyToken");

            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Missing Spotify token"));
            }

            String url = "https://api.spotify.com/v1/me/top/" + type + "?time_range=short_term&limit=" + limit;

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<String> request = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    Map.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                Map bodyMap = response.getBody();
                if (bodyMap != null && bodyMap.containsKey("items")) {
                    List<Map<String, Object>> items = (List<Map<String, Object>>) bodyMap.get("items");
                    return ResponseEntity.ok(items);
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Map.of("error", "Missing 'items' in response"));
                }
            } else {
                return ResponseEntity.status(response.getStatusCode())
                        .body(Map.of("error", "Spotify API returned " + response.getStatusCode()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
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


    @GetMapping("/playlistItem")
    public ResponseEntity<?> top50(HttpSession session) {
        String token = (String) session.getAttribute("spotifyToken");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> request = new HttpEntity<>(headers);
/*
        Map<String, Object> userData = (Map<String, Object>) session.getAttribute("userData");
//        System.out.println("token : "+userData);
        String country = (String) userData.get("country");
//        System.out.println("country : "+country);

        Map<String, String> playlistMap = Map.of(
                "TH", "37i9dQZEVXbMnz8KIWsvf9",     // Thailand Top 50
                "US", "37i9dQZEVXbLRQDuF5jeBp",     // USA Top 50
                "JP", "37i9dQZEVXbKXQ4mDTEBXq"      // Japan Top 50
        );

        String playlistId = playlistMap.getOrDefault(country, "37i9dQZEVXbMDoHDwVN2tF"); // Global Top 50

 */
        String playlistUrl = "https://api.spotify.com/v1/playlists/6Yqe2tMaSLDeJh1Ek4qWYH/tracks";

        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<Map> playlist = restTemplate.exchange(
                    playlistUrl,
                    HttpMethod.GET,
                    request,
                    Map.class
            );
//            System.out.println(playlist.getBody().get("items"));
            return ResponseEntity.ok(playlist.getBody().get("items"));
        } catch (HttpClientErrorException e) {
            System.out.println("Error status: " + e.getStatusCode());
            System.out.println("Error body: " + e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }


//        return ResponseEntity.ok(playlist.getBody().get("items"));
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