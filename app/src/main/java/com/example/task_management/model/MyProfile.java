package com.example.task_management.model;

import java.io.Serializable;

public class MyProfile implements Serializable {
    private int id;
    private String dateOfBirth;
    private String username;
    private String email;
    private String name;
    public MyProfile(int id, String username,String email,String dateOfBirth, String name){
        this.username = username;
        this.email = email;
        this.name = name;
        this.id = id;
        this.dateOfBirth = dateOfBirth;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }
}
