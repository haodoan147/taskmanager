package com.example.task_management.activity.task;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.task_management.R;
import com.example.task_management.adapter.TaskAdapter;
import com.example.task_management.model.Category;
import com.example.task_management.model.MyProfile;
import com.example.task_management.model.Task;
import com.example.task_management.service.APIService;
import com.example.task_management.utils.RetrofitClient;
import com.example.task_management.utils.SharedPrefManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailTaskActivity extends AppCompatActivity {
    APIService apiService;
    List<Category> listCategory= new ArrayList<>();
    TextView tv_title,tv_dueDate,tv_category,tv_label,tv_des,tv_duration,tv_priority,tv_status;
    Integer idTask;
    Task task;
    ImageView btnBack;
    enum TaskPriority {
        NONE ,
        LOW ,
        MEDIUM ,
        HIGH ,
        URGENT,
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_task);
        Intent intent = getIntent();
        idTask =intent.getIntExtra("idTask", 24);
        initView();
    }

    private void initView() {
        tv_title = findViewById(R.id.tv_title);
        tv_dueDate = findViewById(R.id.tv_dueDate);
        tv_category = findViewById(R.id.tv_category);
        tv_label = findViewById(R.id.tv_label);
        tv_des = findViewById(R.id.tv_des);
        tv_duration = findViewById((R.id.tv_duration));
        tv_priority = findViewById((R.id.tv_priority));
        tv_status = findViewById((R.id.tv_status));
        getDetailTask();
        tv_status.setOnClickListener(view -> showPopUpStatusMenu(view));
        btnBack = findViewById(R.id.btn_back_to_context);
        btnBack.setOnClickListener(view -> finish());

    }
    private void getDetailTask(){

        apiService = RetrofitClient.getInstance().create(APIService.class);
        String accessToken = (SharedPrefManager.getInstance(getApplicationContext()).getAccessToken()).getAccessToken();
        String authHeader = "Bearer " + accessToken;
        Log.e("Error code 400",authHeader);
        getCategory();
        apiService.getDetailTask(authHeader,idTask).enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                if (response.isSuccessful()) {
                    task = response.body();
                    Log.e("Error code 400",task.getTitle());
                    tv_title.setText(task.getTitle());
                    tv_dueDate.setText(task.getDueDate().substring(0,10));
                    for (Category cate: listCategory) {
                        if(cate.getId() == task.getCategoryId()) {
                            tv_category.setText(cate.getName());
                            break;
                        }
                    }
                    tv_label.setText((("Nhãn: "+ (task.getLabels()).get(0).getName())));
                    tv_des.setText(task.getDescription());
                    tv_duration.setText("Hạn: " +task.getDuration() + "ngày");
                    tv_priority.setText(String.valueOf(TaskPriority.values()[task.getPriority()]));
                    tv_status.setText(task.getStatus());
                    switch (TaskPriority.values()[task.getPriority()])
                    {
                        case NONE:
                            tv_priority.setBackgroundResource(R.drawable.status_view_round_1);
                            break;
                        case LOW:
                            tv_priority.setBackgroundResource(R.drawable.status_view_round_2);
                            break;
                        case MEDIUM:
                            tv_priority.setBackgroundResource(R.drawable.status_view_round_3);
                            break;
                        case HIGH:
                            tv_priority.setBackgroundResource(R.drawable.status_view_round_4);
                            break;
                        case URGENT:
                            tv_priority.setBackgroundResource(R.drawable.status_view_round_5);
                            break;
                    }
                    switch (task.getStatus())
                    {
                        case "TODO":
                            tv_status.setBackgroundResource(R.drawable.status_view_round_1);
                            break;
                        case "IN_PROGRESS":
                            tv_status.setBackgroundResource(R.drawable.status_view_round_2);
                            break;
                        case "DONE":
                            tv_status.setBackgroundResource(R.drawable.status_view_round_3);
                            break;
                        case "POSTPONED":
                            tv_status.setBackgroundResource(R.drawable.status_view_round_4);
                            break;
                        case "CANCELED":
                            tv_status.setBackgroundResource(R.drawable.status_view_round_5);
                            break;
                    }
                }
                else{
                    try {
                        Log.e("Error code 400",response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            @Override
            public void onFailure(Call<Task> call, Throwable t) {
            }
        });
    }
    private void getCategory(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("ATAuthen", Context.MODE_PRIVATE);
        String accessToken = pref.getString("keyaccesstoken", "empty");
        String authHeader = "Bearer " + accessToken;
        APIService apiService = RetrofitClient.getInstance().create(APIService.class);
        apiService.getAllCategory(authHeader).enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful()) {
                    listCategory = response.body();
                }
            }
            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.d("TAG", "onFailure: " + t.getMessage());
            }
        });
    }
    public void showPopUpStatusMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.change_status_option, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_status_todo:
                    updateStatus("TODO");
                    tv_status.setText("TODO");
                    break;
                case R.id.menu_status_process:
                    updateStatus("IN_PROGRESS");
                    tv_status.setText("IN_PROGRESS");
                    break;
                case R.id.menu_status_done:
                    updateStatus("DONE");
                    tv_status.setText("DONE");
                    break;
                case R.id.menu_status_postponed:
                    updateStatus("POSTPONED");
                    tv_status.setText("POSTPONED");
                    break;
                case R.id.menu_status_cancled:
                    updateStatus("CANCELED");
                    tv_status.setText("CANCELED");
                    break;
            }
            return false;
        });
        popupMenu.show();
    }
    public void showPopUpMoreMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.task_option_popup, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menuDelete:
                    String accessToken = (SharedPrefManager.getInstance(getApplicationContext().getApplicationContext()).getAccessToken()).getAccessToken();
                    String authHeader = "Bearer " + accessToken;
                    APIService apiService = RetrofitClient.getInstance().create(APIService.class);
                    apiService.deleteTask(authHeader,getTaskId()).enqueue(new Callback<Task>() {
                        @Override
                        public void onResponse(Call<Task> call, Response<Task> response) {
                        }
                        @Override
                        public void onFailure(Call<Task> call, Throwable t) {

                        }
                    });
                    break;
            }
            return false;
        });
        popupMenu.show();
    }
    private void updateStatus(String status){
        String accessToken = (SharedPrefManager.getInstance(getApplicationContext().getApplicationContext()).getAccessToken()).getAccessToken();
        String authHeader = "Bearer " + accessToken;
        APIService apiService = RetrofitClient.getInstance().create(APIService.class);
        apiService.updateStatusTask(authHeader,idTask,status).enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
            }
            @Override
            public void onFailure(Call<Task> call, Throwable t) {

            }
        });
    }
}
