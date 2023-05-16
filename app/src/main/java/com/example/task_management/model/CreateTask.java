package com.example.task_management.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class CreateTask implements Serializable {
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
    @SerializedName("dueDate")
    private String dueDate;
    @SerializedName("status")
    private String status;
    @SerializedName("duration")
    private Integer duration;
    @SerializedName("labels")
    private List<Integer> labels;
    @SerializedName("categoryId")
    private Integer categoryId;
    @SerializedName("priority")
    private Integer priority;
    @SerializedName("groupId")
    private Integer groupId;

    public CreateTask(String title, String description, String dueDate, Integer duration, List<Integer> labels, Integer categoryId, Integer priority, Integer groupId) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.duration = duration;
        this.labels = labels;
        this.categoryId = categoryId;
        this.priority = priority;
        this.groupId = groupId;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public List<Integer> getLabels() {
        return labels;
    }

    public void setLabels(List<Integer> labels) {
        this.labels = labels;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }
}
