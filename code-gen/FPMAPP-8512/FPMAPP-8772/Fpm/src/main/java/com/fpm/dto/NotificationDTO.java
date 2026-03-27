package com.fpm.dto;

public class NotificationDTO {

    private String id;
    private String message;
    private String type;
    private boolean read;

    public NotificationDTO() {
    }

    public NotificationDTO(String id, String message, String type, boolean read) {
        this.id = id;
        this.message = message;
        this.type = type;
        this.read = read;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}