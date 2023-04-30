package com.example.task_management.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CreateCategory implements Serializable {

    @SerializedName("name")
    private String name;

    @SerializedName("priority")
    private int priority;

    public CreateCategory(String name, int priority) {

        this.name = name;
        this.priority = priority;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
