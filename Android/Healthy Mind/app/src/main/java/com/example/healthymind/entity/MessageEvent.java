package com.example.healthymind.entity;

public class MessageEvent {
    public final String message;
    public final int currentItem;

    public MessageEvent(String message, int currentItem) {
        this.message = message;
        this.currentItem = currentItem;
    }

    public int getCurrentItem() {
        return currentItem;
    }

    public String getMessage() {
        return message;
    }
}
