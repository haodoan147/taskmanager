package com.example.task_management.service;

import com.example.task_management.model.AccessToken;
import com.example.task_management.model.Category;
import com.example.task_management.model.MyProfile;
import com.example.task_management.model.Task;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface APIService {
    @GET("categories.php")
    Call<List<Category>> getCategoriesAll();

    @GET("categoriy.php")
    Call<Category> getCategory();

    @FormUrlEncoded
    @POST("auth/login")
    Call<AccessToken> getAccessTokenLogin(@Field("email") String username, @Field("password") String password);

    @FormUrlEncoded
    @POST("auth/register")
    Call<AccessToken> getAccessTokenRegister(@Field("email") String email, @Field("password") String password, @Field("confirmPassword") String confirmPassword,
    @Field("username") String username,@Field("name") String name);

    @GET("user/me")
    Call<MyProfile> getMyProfile(@Header("Authorization") String accessToken);
    @GET("task")
    Call<List<Task>> getAllTask(@Header("Authorization") String accessToken);


}
