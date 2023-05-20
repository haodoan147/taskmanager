package com.example.task_management.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Notification implements Serializable {
    @SerializedName("id")
    private int id;
    @SerializedName("type")
    private String type;
    @SerializedName("message")
    private String message;
    @SerializedName("isRead")
    private Boolean isRead;
    @SerializedName("createdAt")
    private String createdAt;

    public Notification(int id, String type, String message, boolean isRead, String createdAt) {
        this.id = id;
        this.type = type;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
