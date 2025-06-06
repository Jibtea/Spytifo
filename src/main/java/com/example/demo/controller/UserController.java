package com.example.demo.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;


import java.util.List;
import java.util.Map;

@Controller
public class UserController {
    @GetMapping("/profile")
    public String UserData(HttpSession session, Model model) {
        String token = (String) session.getAttribute("spotifyToken");
        model.addAttribute("token", token);

        if (token == null) {
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

        ResponseEntity<Map> response2 = restTemplate.exchange(
                "https://api.spotify.com/v1/me/following?type=artist&limit=5",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Map.class
        );

        if (response.getStatusCode().is2xxSuccessful()&&response2.getStatusCode().is2xxSuccessful()) {
            //=========response1============
            Map<String, Object> userData = response.getBody();
            model.addAttribute("user", userData);

            //=========response2=========
            Map<String, Object>artist_following = response2.getBody();


            model.addAttribute("artist_following", artist_following);
            session.setAttribute("userData", userData);
//           System.out.println(session.getAttribute("userData"));
            System.out.println(response2.getBody());
        } else {
            model.addAttribute("error", "Cannot get user data");
        }


        return "profile";
    }


}

/*
@PostMapping("/topSong")
public String UserGetTopSong(
        HttpSession session,
        Model model,
        @RequestParam("limit") int limit,
        @RequestParam("type") String type
) {
    String token = (String) session.getAttribute("spotifyToken");
    Map<String, String> userData = (Map<String, String>) session.getAttribute("userData");
    String id = userData.get("id");

    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + token);

    String Url = "https://api.spotify.com/v1/me/top/"+type+"?limit="+limit;
//ไว้ส่งขอapi
    HttpEntity<String> request = new HttpEntity<>(headers);
    ResponseEntity<Map> response = restTemplate.exchange(
//                "https://api.spotify.com/v1/me/top/artists?limit=5",
            Url,
            HttpMethod.GET,
            request,
            Map.class
    );

    List<Map<String,Object>> items = (List<Map<String, Object>>) response.getBody().get("items");
    model.addAttribute("Items", items);
    model.addAttribute("type", (String)type);

//        model.addAttribute("TopSong", response.getBody());
//        System.out.println(response.getBody());

    return "topSong";

}*/



