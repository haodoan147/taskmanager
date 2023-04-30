package com.example.task_management.service;

import androidx.annotation.Nullable;

import com.example.task_management.model.AccessToken;
import com.example.task_management.model.Category;
import com.example.task_management.model.CreateCategory;
import com.example.task_management.model.CreateTask;
import com.example.task_management.model.Label;
import com.example.task_management.model.MyProfile;
import com.example.task_management.model.PaginationTask;
import com.example.task_management.model.Task;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIService {
    @GET("categories.php")
    Call<List<Category>> getCategoriesAll();

    @GET("category.php")
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
    Call<PaginationTask> getAllTask(@Header("Authorization") String accessToken, @Query("page") int page , @Query("limit") int limit,
                                    @Query("order") String order, @Query("status") String status,
                                    @Query("orderBy") String orderBy, @Query("search") String search);
    @GET("task/{id}")
    Call<Task> getDetailTask(@Header("Authorization") String accessToken, @Path("id") int id);
    @POST("task")
    Call<Task> getCreateNewTask(@Header("Authorization") String accessToken, @Body CreateTask task);

    @DELETE("task/{id}")
    Call<Task> deleteTask(@Header("Authorization") String accessToken, @Path("id") int id);
    @GET("label")
    Call<List<Label>> getAllLabel(@Header("Authorization") String accessToken);
    @GET("category")
    Call<List<Category>> getAllCategory(@Header("Authorization") String accessToken);
    @FormUrlEncoded
    @PATCH("task/{id}/status")
    Call<Task> updateStatusTask(@Header("Authorization") String accessToken, @Path("id") int id,@Field("status") String status);
    @POST("category")
    Call<Category> getCreateNewCategory(@Header("Authorization") String accessToken, @Body CreateCategory category);
    @DELETE("category/{id}")
    Call<Category> deleteCate(@Header("Authorization") String accessToken, @Path("id") int id);
}
