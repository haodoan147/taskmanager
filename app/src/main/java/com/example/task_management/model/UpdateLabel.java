package com.example.task_management.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UpdateLabel implements Serializable {
    @SerializedName("name")
    private String name;
    @SerializedName("color")
    private String color;

    public UpdateLabel(String name, String color) {
        this.name = name;
        this.color = color;
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
}
