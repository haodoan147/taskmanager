package com.example.task_management.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class CreateLabel implements Serializable {
    @SerializedName("name")
    private String name;
    @SerializedName("color")
    private String color;
    @SerializedName("groupId")
    private Integer groupId;

    public CreateLabel(String name, String color,Integer groupId) {
        this.name = name;
        this.color = color;
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }
}
