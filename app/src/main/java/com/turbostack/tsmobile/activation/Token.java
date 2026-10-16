package com.example.app.activation;

public class Token {
    private String subscription;

    public Token() {}

    public Token(String subscription) {
        this.subscription = subscription;
    }

    public String getSubscription() {
        return subscription;
    }

    public void setSubscription(String subscription) {
        this.subscription = subscription;
    }
}