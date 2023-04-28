package com.example.task_management.activity.task;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task_management.R;
import com.example.task_management.activity.MyProfileActivity;
import com.example.task_management.adapter.TaskAdapter;
import com.example.task_management.model.Category;
import com.example.task_management.model.PaginationTask;
import com.example.task_management.model.Task;
import com.example.task_management.service.APIService;
import com.example.task_management.utils.RetrofitClient;
import com.example.task_management.utils.SharedPrefManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TaskAdapter taskAdapter;
    APIService apiService;
    List<Task> taskList;
    TextView appHeader;
    ImageView profileBtn,filterBtn;
    SearchView searchView;
    List<Category> listCategory = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task);
        anhXa();
        if(appHeader.getText() =="Home"){
            ImageView left_icon = findViewById(R.id.left_icon);
            left_icon.setVisibility(View.INVISIBLE);
        }
        getAllTask("TODO");
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
        filterBtn = findViewById(R.id.filter_icon);
        searchView = findViewById(R.id.searchView);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterListener(newText);
                return false;
            }
        });
    }


    private void getAllTask(String status){
        String accessToken = (SharedPrefManager.getInstance(getApplicationContext()).getAccessToken()).getAccessToken();
        String authHeader = "Bearer " + accessToken;
        getCategory();
        apiService = RetrofitClient.getInstance().create(APIService.class);
        apiService.getAllTask(authHeader,1,100,"asc", status,"priority", "").enqueue(new Callback<PaginationTask>() {
            @Override
            public void onResponse(Call<PaginationTask> call, Response<PaginationTask> response) {
                if (response.isSuccessful()) {
                    taskList = response.body().getData();
                    taskAdapter = new TaskAdapter(TaskActivity.this, taskList,listCategory);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),
                            LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(taskAdapter);
                    taskAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<PaginationTask> call, Throwable t) {
                Log.e("TAG", "onFailure: " + t.getMessage());
            }
        });
    }
    public void btnFilter_onClick(View view) {
        PopupMenu popupMenu = new PopupMenu(this, filterBtn);
        popupMenu.getMenuInflater().inflate(R.menu.menu_setting_popup,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menuTodo:
                        getAllTask("TODO");
                        break;
                    case R.id.menuInprocess:
                        getAllTask("IN_PROGRESS");
                        break;
                    case R.id.menuDone:
                        getAllTask("DONE");
                        break;
                    case R.id.menuPostPoned:
                        getAllTask("POSTPONED");
                        break;
                    case R.id.menuCanceled:
                        getAllTask("CANCELED");
                        break;

                }
                return false;
            }
        });
        popupMenu.show();
    }
    private void filterListener(String text){
        List<Task> list = new ArrayList<>();
        for (Task task:taskList){
            if(task.getTitle().toLowerCase().contains(text.toLowerCase())){
                list.add(task);
            }
        }
        taskAdapter.setListenerList(list);

    }
    private void getCategory(){
        String accessToken = (SharedPrefManager.getInstance(getApplicationContext()).getAccessToken()).getAccessToken();
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
}
