package com.example.task_management.activity.task;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.task_management.R;
import com.example.task_management.activity.MyProfileActivity;
import com.example.task_management.adapter.TaskAdapter;
import com.example.task_management.model.Category;
import com.example.task_management.model.PaginationTask;
import com.example.task_management.model.Task;
import com.example.task_management.service.APIService;
import com.example.task_management.utils.RetrofitClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchTaskFragment extends Fragment {
    RecyclerView recyclerView;
    TaskAdapter taskAdapter;
    APIService apiService;
    List<Task> taskList= new ArrayList<>();
    TextView appHeader;
    ImageView profileBtn,filterBtn;
    SearchView searchView;
    List<Category> listCategory= new ArrayList<>();
    BottomNavigationView navigationView;
    ViewPager viewPager;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_task, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        getAllTask("TODO");
        filterBtn = view.findViewById(R.id.filter_icon);
        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFilter_onClick(v);
            }
        });
        searchView = view.findViewById(R.id.searchView);
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
        return view;
    }


    private void getAllTask(String status){
        SharedPreferences pref = getActivity().getSharedPreferences("ATAuthen", Context.MODE_PRIVATE);
        String accessToken = pref.getString("keyaccesstoken", "empty");
        String authHeader = "Bearer " + accessToken;
        Log.e("123", authHeader);
        getCategory();
        apiService = RetrofitClient.getInstance().create(APIService.class);
        apiService.getAllTask(authHeader,1,100,"asc",1, status,"priority", "").enqueue(new Callback<PaginationTask>() {
            @Override
            public void onResponse(Call<PaginationTask> call, Response<PaginationTask> response) {
                if (response.isSuccessful()) {
                    taskList = response.body().getData();
                    taskAdapter = new TaskAdapter(getActivity(), taskList,listCategory);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(),
                            LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(taskAdapter);
                    taskAdapter.notifyDataSetChanged();
                }else{
                    try {
                        Log.v("Error code 400",response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            @Override
            public void onFailure(Call<PaginationTask> call, Throwable t) {
                Log.e("TAG", "onFailure: " + t.getMessage());
            }
        });
    }
    public void btnFilter_onClick(View view) {
        PopupMenu popupMenu = new PopupMenu(getActivity(), filterBtn);
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
        if(!taskList.isEmpty()) {
            for (Task task : taskList) {
                if (task.getTitle().toLowerCase().contains(text.toLowerCase())) {
                    list.add(task);
                }
            }
            taskAdapter.setListenerList(list);
        }
    }
    private void getCategory(){
        SharedPreferences pref = getActivity().getSharedPreferences("ATAuthen",Context.MODE_PRIVATE);
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
}
