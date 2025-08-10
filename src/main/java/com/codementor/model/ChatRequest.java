package com.codementor.model;

public class ChatRequest {
    private String userInput;
    
    public ChatRequest() {}
    
    public ChatRequest(String userInput) {
        this.userInput = userInput;
    }
    
    public String getUserInput() {
        return userInput;
    }
    
    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }
}
