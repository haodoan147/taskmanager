package com.example.task_management.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task_management.R;
import com.example.task_management.adapter.CategoryAdapter;
import com.example.task_management.adapter.TaskAdapter;
import com.example.task_management.model.Category;
import com.example.task_management.model.Task;
import com.example.task_management.service.APIService;
import com.example.task_management.utils.RetrofitClient;
import com.example.task_management.utils.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TaskAdapter taskAdapter;
    APIService apiService;
    List<Task> taskList;
    TextView appHeader;
    ImageView profileBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task);
        anhXa();
        if(appHeader.getText() =="Home"){
            ImageView left_icon = findViewById(R.id.left_icon);
            left_icon.setVisibility(View.INVISIBLE);
        }
        getAllTask();
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TaskActivity.this, "Test Clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(TaskActivity.this, MyProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    private void anhXa (){
        recyclerView = findViewById(R.id.recyclerView);
        appHeader = findViewById((R.id.app_header));
        appHeader.setText("Home task");
        profileBtn = findViewById(R.id.right_icon);

    }


    private void getAllTask(){
        String accessToken = (SharedPrefManager.getInstance(getApplicationContext()).getAccessToken()).getAccessToken();
        String authHeader = "Bearer " + accessToken;
        apiService = RetrofitClient.getInstance().create(APIService.class);
        apiService.getAllTask(authHeader).enqueue(new Callback<List<Task>>() {
            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                if (response.isSuccessful()) {
                    taskList = response.body();
                    taskAdapter = new TaskAdapter(TaskActivity.this, taskList);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),
                            LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(taskAdapter);
                    taskAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {
                Log.d("TAG", "onFailure: " + t.getMessage());
            }
        });
    }
}
