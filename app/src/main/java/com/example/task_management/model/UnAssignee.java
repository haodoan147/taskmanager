package com.example.task_management.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UnAssignee implements Serializable {
    @SerializedName("groupId")
    private int groupId;

    public UnAssignee(int groupId) {
        this.groupId = groupId;
    }

    public int getGroupId() {
        return groupId;
    }
    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
}
