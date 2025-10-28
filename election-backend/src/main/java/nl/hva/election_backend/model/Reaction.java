package com.example.election.model;

public class Reaction {
    private String author;
    private String message;
    private String createdAt;

    public Reaction(String author, String message, String createdAt) {
        this.author = author;
        this.message = message;
        this.createdAt = createdAt;
    }

    public String getAuthor() { return author; }
    public String getMessage() { return message; }
    public String getCreatedAt() { return createdAt; }
}
