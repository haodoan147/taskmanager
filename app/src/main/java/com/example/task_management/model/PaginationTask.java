package com.example.task_management.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PaginationTask implements Serializable {
    @SerializedName("page")
    private String page;
    @SerializedName("limit")
    private String limit;
    @SerializedName("total")
    private Integer total;
    @SerializedName("data")
    private List<Task> data;

    public PaginationTask(String page, String limit, Integer total, List<Task> data) {
        this.page = page;
        this.limit = limit;
        this.total = total;
        this.data = data;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getPage() {
        return page;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public String getLimit() {
        return limit;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getTotal() {
        return total;
    }

    public void setData(List<Task> data) {
        this.data = data;
    }

    public List<Task> getData() {
        return data;
    }
}
