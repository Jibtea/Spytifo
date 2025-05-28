package com.example.demo.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Controller
public class SpotifyController {
    @Value("${spotify.client_id}")
    private String clientId;

    @Value("${spotify.redirect_uri}")
    private String redirectUri;

    @Value("${spotify.client_secret}")
    private String clientSecret;

    @GetMapping("/login")
    public String login() {
        String state = generateRandomString(16);
        String scope = String.join(" ", List.of(
                "ugc-image-upload",
                "user-read-playback-state",
                "user-modify-playback-state",
                "user-read-currently-playing",
                "app-remote-control",
                "streaming",
                "playlist-read-private",
                "playlist-read-collaborative",
                "playlist-modify-private",
                "playlist-modify-public",
                "user-follow-modify",
                "user-follow-read",
                "user-library-modify",
                "user-library-read",
                "user-read-email",
                "user-read-private",
                "user-top-read",
                "user-read-recently-played"
        ));

        String authorizeURL=  "https://accounts.spotify.com/authorize?" +
                "response_type=code" +
                "&client_id=" + urlEncode(clientId) +
                "&scope=" + urlEncode(scope) +
                "&redirect_uri=" + urlEncode(redirectUri) +
                "&state=" + urlEncode(state);

        return "redirect:" + authorizeURL;
    }

    //เพื่อถอดรหัสไปเอาค่าtoken หดหู่จังๆ
    @GetMapping("/callback")
    public RedirectView callback(
            @RequestParam("code") String code,
            @RequestParam("state") String state,
            HttpSession session
    ) {

        //ไว้เก็บpost request
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders(); // บอกว่าใช้ form
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String url = "https://accounts.spotify.com/api/token";

        String auth = clientId + ":" + clientSecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + encodedAuth);

        //เตรียมbodyไว้ส่งตอนrequest
        String body = UriComponentsBuilder.newInstance()
                .queryParam("grant_type", "authorization_code")
                .queryParam("code", code)
                .queryParam("redirect_uri", redirectUri)
                .build()
                .encode()
                .toUriString()
                .substring(1);

        //ไว้ส่งขอapi
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                Map.class
        );

        Map<String, Object> responseBody = response.getBody();
        if (responseBody != null && responseBody.containsKey("access_token")) {
            String accessToken = (String) responseBody.get("access_token");
            session.setAttribute("spotifyToken", accessToken);
            ResponseEntity.ok("Access Token saved in session.");
        } else {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("cannot get access token naja");
        }
        
        return new RedirectView("/profile");
    }


    private String generateRandomString(int length) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] bytes = new byte[length];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes).substring(0, length);
    }

    private String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
