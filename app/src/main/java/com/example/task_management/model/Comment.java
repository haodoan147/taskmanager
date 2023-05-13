package com.example.task_management.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Comment implements Serializable {
    @SerializedName("id")
    private int id;
    @SerializedName("content")
    private String content;

    @SerializedName("createdAt")
    private String createdAt;
    @SerializedName("owner")
    private Owner owner;
    @SerializedName("taskId")
    private int taskId;

    public Comment(int id, String content, String createdAt, Owner owner, int taskId) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
        this.owner = owner;
        this.taskId = taskId;
    }

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public Owner getOwner() {
        return owner;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }
}
