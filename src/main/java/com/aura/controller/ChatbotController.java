package com.aura.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.MediaType;
import java.util.HashMap;
import java.util.Map;

import com.aura.service.GeminiApiService;

@Controller
public class ChatbotController {

    private final GeminiApiService geminiApiService;

    @Autowired
    public ChatbotController(GeminiApiService geminiApiService) {
        this.geminiApiService = geminiApiService;
    }

    @GetMapping("/")
    public String index() {
        return "index"; // This will serve templates/index.html
    }

    @PostMapping(value = "/chat", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    public Map<String, String> chat(@RequestParam("user_input") String userInput) {
        String botResponse = geminiApiService.getResponse(userInput);

        Map<String, String> response = new HashMap<>();
        response.put("response", botResponse);
        return response;
    }
}
