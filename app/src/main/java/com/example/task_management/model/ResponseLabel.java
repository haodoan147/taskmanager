package com.example.task_management.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ResponseLabel implements Serializable {
    @SerializedName("page")
    private String page;
    @SerializedName("limit")
    private String limit;
    @SerializedName("total")
    private Integer total;
    @SerializedName("data")
    private List<Label> data;

    public ResponseLabel(String page, String limit, Integer total, List<Label> data) {
        this.page = page;
        this.limit = limit;
        this.total = total;
        this.data = data;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<Label> getData() {
        return data;
    }

    public void setData(List<Label> data) {
        this.data = data;
    }
}
