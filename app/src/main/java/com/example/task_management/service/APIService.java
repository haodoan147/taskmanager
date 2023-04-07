package com.example.task_management.service;

import com.example.task_management.model.Category;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface APIService {
    @GET("categories.php")
    Call<List<Category>> getCategoriesAll();

    @GET("categoriy.php")
    Call<Category> getCategory();

//    @POST("/v1/user/")
//    @FormUrlEncoded
//    Call<User> login(@Field("username") String username, @Field("password") String password);

}
