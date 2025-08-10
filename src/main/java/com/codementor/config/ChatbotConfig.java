package com.codementor.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatbotConfig {
    
    @Value("${chatbot.system.prompt:}")
    private String systemPrompt;
    
    public String getSystemPrompt() {
        return systemPrompt;
    }
}
