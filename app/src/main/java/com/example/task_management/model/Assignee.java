package com.example.task_management.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Assignee implements Serializable {
    @SerializedName("groupId")
    private int groupId;
    @SerializedName("userId")
    private int userId;

    public Assignee(int groupId, int userId) {
        this.groupId = groupId;
        this.userId = userId;
    }

    public int getGroupId() {
        return groupId;
    }

    public int getUserId() {
        return userId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
