package com.example.final_project.data.model;

public class ChatMessage {
    private String characterName;
    private String message;
    private boolean isUserMessage;
    private long timestamp;

    public ChatMessage(String characterName, String message, boolean isUserMessage, long timestamp) {
        this.characterName = characterName;
        this.message = message;
        this.isUserMessage = isUserMessage;
        this.timestamp = timestamp;
    }

    public String getCharacterName() {
        return characterName;
    }

    public String getMessage() {
        return message;
    }

    public boolean isUserMessage() {
        return isUserMessage;
    }

    public long getTimestamp() {
        return timestamp;
    }
} 