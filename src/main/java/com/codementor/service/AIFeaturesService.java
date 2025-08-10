package com.codementor.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.codementor.model.QuizQuestion;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AIFeaturesService {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final ObjectMapper objectMapper;

    public AIFeaturesService(@Value("${gemini.api.key:}") String apiKey) {
        this.apiKey = apiKey;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public String summarizeText(String text) {
        try {
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String prompt = "Please provide a concise summary of the following text. Focus on the main points and key information:\n\n" + text;

            Map<String, Object> requestBody = new HashMap<>();
            List<Map<String, Object>> contents = new ArrayList<>();
            Map<String, String> part = new HashMap<>();
            part.put("text", prompt);
            Map<String, Object> content = new HashMap<>();
            content.put("parts", List.of(part));
            contents.add(content);
            requestBody.put("contents", contents);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            JsonNode root = objectMapper.readTree(response.getBody());
            return root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to summarize text: " + e.getMessage(), e);
        }
    }

    public List<QuizQuestion> generateQuiz(String text) {
        try {
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String prompt = "Generate 3 multiple-choice quiz questions with 4 options each based on the following text. "
                    + "Format the response as JSON with the following structure: "
                    + "[{\"question\":\"question text\",\"options\":[\"option1\",\"option2\",\"option3\",\"option4\"],\"correctAnswer\":0,\"explanation\":\"explanation\"}]\n\n"
                    + text;

            Map<String, Object> requestBody = new HashMap<>();
            List<Map<String, Object>> contents = new ArrayList<>();
            Map<String, String> part = new HashMap<>();
            part.put("text", prompt);
            Map<String, Object> content = new HashMap<>();
            content.put("parts", List.of(part));
            contents.add(content);
            requestBody.put("contents", contents);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            JsonNode root = objectMapper.readTree(response.getBody());
            String quizJson = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
            
            // Clean the JSON response
            quizJson = quizJson.replace("```json", "").replace("```", "").trim();
            
            QuizQuestion[] questions = objectMapper.readValue(quizJson, QuizQuestion[].class);
            return List.of(questions);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate quiz: " + e.getMessage(), e);
        }
    }

    public String askDoubt(String context, String doubt) {
        try {
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String prompt = "Based on the following context, please answer the doubt:\n\n"
                    + "Context: " + context + "\n\n"
                    + "Doubt: " + doubt;

            Map<String, Object> requestBody = new HashMap<>();
            List<Map<String, Object>> contents = new ArrayList<>();
            Map<String, String> part = new HashMap<>();
            part.put("text", prompt);
            Map<String, Object> content = new HashMap<>();
            content.put("parts", List.of(part));
            contents.add(content);
            requestBody.put("contents", contents);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            JsonNode root = objectMapper.readTree(response.getBody());
            return root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to answer doubt: " + e.getMessage(), e);
        }
    }
}
