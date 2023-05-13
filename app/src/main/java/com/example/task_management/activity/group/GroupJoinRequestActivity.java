package com.example.task_management.activity.group;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task_management.R;
import com.example.task_management.adapter.GroupAdapter;
import com.example.task_management.adapter.JoinRequestAdapter;
import com.example.task_management.model.Group;
import com.example.task_management.model.JoinRequest;
import com.example.task_management.service.APIService;
import com.example.task_management.utils.RetrofitClient;
import com.example.task_management.utils.SharedPrefManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupJoinRequestActivity extends AppCompatActivity {
    APIService apiService;
    RecyclerView recyclerView;
    List<JoinRequest> joinRequestList= new ArrayList<>();
    int groupId;
    JoinRequestAdapter joinRequestAdapter;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_group_join_request);
        Intent intent = getIntent();
        groupId = intent.getIntExtra("idGroup", 1);
        initView();
    }
    private void initView() {
        recyclerView = findViewById(R.id.rcv);
        getJoinRequest();
    }
    private void getJoinRequest(){
        String accessToken = (SharedPrefManager.getInstance(getApplicationContext().getApplicationContext()).getAccessToken()).getAccessToken();
        String authHeader = "Bearer " + accessToken;
        apiService = RetrofitClient.getInstance().create(APIService.class);
        apiService.getGroupRequest(authHeader,groupId).enqueue(new Callback<List<JoinRequest>>() {
            @Override
            public void onResponse(Call<List<JoinRequest>> call, Response<List<JoinRequest>> response) {
                if (response.isSuccessful()) {
                    joinRequestList = response.body();
                    joinRequestAdapter = new JoinRequestAdapter(GroupJoinRequestActivity.this, joinRequestList);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),
                            LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(joinRequestAdapter);
                    joinRequestAdapter.notifyDataSetChanged();
                }else{
                    try {
                        Log.v("Error code 400",response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            @Override
            public void onFailure(Call<List<JoinRequest>> call, Throwable t) {
                Log.e("TAG", "onFailure: " + t.getMessage());
            }
        });
    }
}
