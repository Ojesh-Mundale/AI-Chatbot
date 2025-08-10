package com.codementor.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.codementor.config.ChatbotConfig;

@Service
public class GeminiApiService {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final ObjectMapper objectMapper;
    private final ChatbotConfig chatbotConfig;

    public GeminiApiService(@Value("${gemini.api.key:}") String apiKey, ChatbotConfig chatbotConfig) {
        this.apiKey = apiKey;
        this.chatbotConfig = chatbotConfig;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public String getResponse(String userInput) {
        try {
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String systemPrompt = chatbotConfig.getSystemPrompt();
            String fullPrompt = systemPrompt + "\n\nUser Question: " + userInput;
            
            String requestBody = "{\n" +
                    "  \"contents\": [\n" +
                    "    {\n" +
                    "      \"parts\": [\n" +
                    "        {\n" +
                    "          \"text\": \"" + fullPrompt.replace("\"", "\\\"") + "\"\n" +
                    "        }\n" +
                    "      ]\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            // Parse JSON and extract bot response text
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode candidates = root.path("candidates");

            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode content = candidates.get(0).path("content");
                JsonNode parts = content.path("parts");
                if (parts.isArray() && parts.size() > 0) {
                    String text = parts.get(0).path("text").asText();
                    return text;
                }
            }

            return "No response found";

        } catch (Exception e) {
            return "Error calling Gemini API: " + e.getMessage();
        }
    }
}
