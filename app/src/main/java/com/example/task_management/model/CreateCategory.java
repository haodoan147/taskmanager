package com.example.task_management.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CreateCategory implements Serializable {

    @SerializedName("name")
    private String name;

    @SerializedName("priority")
    private int priority;
    @SerializedName("groupId")
    private int groupId;

    public CreateCategory(String name, int priority, int groupId) {

        this.name = name;
        this.priority = priority;
        this.groupId = groupId;
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

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
}
