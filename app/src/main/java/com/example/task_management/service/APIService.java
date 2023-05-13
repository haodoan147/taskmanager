package com.example.task_management.service;

import com.example.task_management.model.AccessToken;
import com.example.task_management.model.Category;
import com.example.task_management.model.Comment;
import com.example.task_management.model.CreateCategory;
import com.example.task_management.model.CreateTask;
import com.example.task_management.model.Group;
import com.example.task_management.model.JoinRequest;
import com.example.task_management.model.Label;
import com.example.task_management.model.User;
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
    Call<User> getMyProfile(@Header("Authorization") String accessToken);
    @GET("task")
    Call<PaginationTask> getAllTask(@Header("Authorization") String accessToken, @Query("page") int page , @Query("limit") int limit,
                                    @Query("order") String order,@Query("groupId") int groupId, @Query("status") String status,
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
    @FormUrlEncoded
    @POST("comment/{taskId}")
    Call<Comment> createComment(@Header("Authorization") String accessToken, @Path("taskId") int id, @Field("content") String content);

    @GET("comment/{taskId}")
    Call<List<Comment>> getAllComment(@Header("Authorization") String accessToken, @Path("taskId") int id);
    @GET("group/my-groups")
    Call<List<Group>> getMyGroups(@Header("Authorization") String accessToken);

    @FormUrlEncoded
    @POST("group")
    Call<Group> createGroup(@Header("Authorization") String accessToken, @Field("name") String name);
    @DELETE("delete/{groupId}")
    Call<Group> deleteGroup(@Header("Authorization") String accessToken, @Path("groupId") int groupId);
    @FormUrlEncoded
    @PATCH("group/{groupId}")
    Call<Group> updateGroup(@Header("Authorization") String accessToken, @Path("groupId") int groupId, @Field("name") String name);
    @GET("group/{groupId}/members")
    Call<List<User>> getAllMembers(@Header("Authorization") String accessToken, @Path("groupId") int id);
    @POST("group/{groupId}/kick/{userId}")
    Call<User> kickAMember(@Header("Authorization") String accessToken, @Path("groupId") int groupId,@Path("userId") int userId);

    @GET("/group-join-request/{groupId}")
    Call<List<JoinRequest>> getGroupRequest(@Header("Authorization") String accessToken, @Path("groupId") int groupId);
    @POST("/group-join-request/{id}/reject")
    Call<JoinRequest> rejectRequest(@Header("Authorization") String accessToken, @Path("id") int id);
    @POST("/group-join-request/{id}/accept")
    Call<JoinRequest> acceptRequest(@Header("Authorization") String accessToken, @Path("id") int id);
    @POST("/group-join-request/request/{groupId}")
    Call<JoinRequest> requestGroup(@Header("Authorization") String accessToken, @Path("groupId") int groupId);
}
