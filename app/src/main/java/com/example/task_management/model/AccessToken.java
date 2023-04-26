package com.example.task_management.model;

import java.io.Serializable;

public class AccessToken implements Serializable {
    private String access_token;
    public AccessToken(String access_token){
        this.access_token = access_token;
    }

    public void setAccessToken(String accessToken) {
        this.access_token = accessToken;
    }

    public String getAccessToken() {
        return access_token;
    }
}
