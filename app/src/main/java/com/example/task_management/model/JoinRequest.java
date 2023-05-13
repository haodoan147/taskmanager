package com.example.task_management.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class JoinRequest implements Serializable {
    @SerializedName("id")
    private int id;
    @SerializedName("groupId")
    private int groupId;
    @SerializedName("status")
    private String status;
    @SerializedName("requestBy")
    private RequestBy requestBy;

    public JoinRequest(RequestBy requestBy, int id, int groupId, String status) {
        this.requestBy = requestBy;
        this.id = id;
        this.groupId = groupId;
        this.status = status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setRequestBy(RequestBy requestBy) {
        this.requestBy = requestBy;
    }

    public int getId() {
        return id;
    }

    public int getGroupId() {
        return groupId;
    }

    public String getStatus() {
        return status;
    }

    public RequestBy getRequestBy() {
        return requestBy;
    }
}
