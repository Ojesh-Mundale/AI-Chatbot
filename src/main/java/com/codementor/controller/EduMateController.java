package com.codementor.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.codementor.model.QuizQuestion;
import com.codementor.service.AIFeaturesService;
import com.codementor.service.DocumentProcessingService;

@RestController
@RequestMapping("/edumate")
public class EduMateController {

    private static final Logger logger = LoggerFactory.getLogger(EduMateController.class);
    
    private final DocumentProcessingService documentProcessingService;
    private final AIFeaturesService aiFeaturesService;

    @Autowired
    public EduMateController(DocumentProcessingService documentProcessingService, AIFeaturesService aiFeaturesService) {
        this.documentProcessingService = documentProcessingService;
        this.aiFeaturesService = aiFeaturesService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, String> uploadDocument(@RequestParam("file") MultipartFile file) {
        Map<String, String> response = new HashMap<>();
        
        try {
            // Validate file type
            if (!documentProcessingService.isValidFileType(file.getOriginalFilename())) {
                response.put("error", "Unsupported file type. Please upload PDF (.pdf) or text (.txt) files.");
                return response;
            }
            
            // Validate file size
            if (!documentProcessingService.isValidFileSize(file.getSize())) {
                response.put("error", "File too large. Maximum size is 10MB.");
                return response;
            }
            
            // Process the document
            String extractedText = documentProcessingService.extractTextFromDocument(file);
            response.put("extractedText", extractedText);
            
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            logger.warn("Upload validation failed: {}", e.getMessage());
            
        } catch (IOException e) {
            response.put("error", "Failed to read file: " + e.getMessage());
            logger.error("File processing error", e);
            
        } catch (Exception e) {
            response.put("error", "An unexpected error occurred while processing the document");
            logger.error("Unexpected error during file upload", e);
        }
        
        return response;
    }

    @PostMapping("/summarize")
    public Map<String, String> summarizeText(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        String summary = aiFeaturesService.summarizeText(text);
        Map<String, String> response = new HashMap<>();
        response.put("summary", summary);
        return response;
    }

    @PostMapping("/generateQuiz")
    public Map<String, Object> generateQuiz(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        List<QuizQuestion> questions = aiFeaturesService.generateQuiz(text);
        Map<String, Object> response = new HashMap<>();
        response.put("quiz", questions);
        return response;
    }

    @PostMapping("/askDoubt")
    public Map<String, String> askDoubt(@RequestBody Map<String, String> request) {
        String doubt = request.get("doubt");
        String context = request.getOrDefault("context", "");
        String answer = aiFeaturesService.askDoubt(context, doubt);
        Map<String, String> response = new HashMap<>();
        response.put("answer", answer);
        return response;
    }
}
