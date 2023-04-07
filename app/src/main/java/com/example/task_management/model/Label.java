package com.example.task_management.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Label implements Serializable {
    @SerializedName("id")
    private Long id;
    @SerializedName("name")
    private String name;
    @SerializedName("color")
    private String color;

    public Label(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
