package com.example.task_management.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.task_management.R;
import com.example.task_management.activity.group.group.MyGroupActivity;
import com.example.task_management.activity.member.group.MyGroupFragment;
import com.example.task_management.activity.member.task.TaskFragment;
import com.example.task_management.activity.task.CalendarActivity;
import com.example.task_management.activity.member.task.SearchTaskFragment;
import com.example.task_management.adapter.MemberGroupAdapter;
import com.example.task_management.model.Category;
import com.example.task_management.model.Group;
import com.example.task_management.model.ResponseCate;
import com.example.task_management.model.User;
import com.example.task_management.model.PaginationTask;
import com.example.task_management.model.Task;
import com.example.task_management.service.APIService;
import com.example.task_management.utils.RetrofitClient;
import com.example.task_management.utils.SharedPrefManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;
    APIService apiService;
    String id, name, email;
    NavigationView navigationView;
    List<Task> taskList= new ArrayList<>();
    List<Category> listCategory= new ArrayList<>();
    List<Group> listGroup= new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_activity);
        LoadingDialog loadingDialog = new LoadingDialog(HomeActivity.this);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        getMyProfile();
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        navigationView.bringToFront();

        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            taskList.clear();
            listCategory.clear();
            getMyGroups();
            switch (item.getItemId()) {
                case R.id.btm_home:
                    toolbar.setTitle("Quản lí công việc");
                    loadingDialog.startLoadingDialog();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Fragment newFragment = new TaskFragment();
                            Bundle args = new Bundle();
                            args.putSerializable("newTaskList", (Serializable) taskList);
                            args.putSerializable("newCateList", (Serializable) listCategory);
                            newFragment.setArguments(args);
                            replaceFragment(newFragment);
                            loadingDialog.dismissDialog();
                        }
                    }, 3000);
                    break;
                case R.id.btm_search:
                    toolbar.setTitle("Tìm kiếm");
                    loadingDialog.startLoadingDialog();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Fragment newFragment = new SearchTaskFragment();
                            Bundle args = new Bundle();
                            args.putSerializable("idGroup", (Serializable) 1);
                            newFragment.setArguments(args);
                            replaceFragment(newFragment);
                            loadingDialog.dismissDialog();
                        }
                    }, 3000);
                    break;
                case R.id.btm_group:
                    toolbar.setTitle("Nhóm");
                    loadingDialog.startLoadingDialog();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            replaceFragment(new MyGroupFragment());
                            loadingDialog.dismissDialog();
                        }
                    }, 3000);
                    break;
            }
            return true;
        });
    }
    private  void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.nav_home:
                Toast.makeText(getApplicationContext(), "Bạn đang ở trang chủ", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_profile:
                intent = new Intent(getApplicationContext(), MyProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_logout:
                SharedPrefManager.getInstance(getApplicationContext()).logout();
                intent = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_schedule:
                intent = new Intent(getApplicationContext(), CalendarActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_group:
                intent = new Intent(getApplicationContext(), MyGroupActivity.class);
                startActivity(intent);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    private void getMyProfile(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("ATAuthen",Context.MODE_PRIVATE);
        String accessToken = pref.getString("keyaccesstoken", "empty");
        apiService = RetrofitClient.getInstance().create(APIService.class);
        String authHeader = "Bearer " + accessToken;
        apiService.getMyProfile(authHeader).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User user = response.body();
                if (response.isSuccessful()) {
                    id = String.valueOf(user.getId());
                    name = "Hi, " + user.getName();
                    email = user.getEmail();
                    View headerLayout = navigationView.getHeaderView(0);
                    TextView baseName = headerLayout.findViewById(R.id.base_name);
                    TextView baseEmail = headerLayout.findViewById(R.id.base_email);
                    SharedPrefManager.getInstance(getApplicationContext()).userProfile(id,name,email);
                    baseName.setText(pref.getString("keyname", "empty"));
                    baseEmail.setText(pref.getString("keyemail", "empty"));;
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
            }
        });
    }
    private void getAllTask(String status,int groupId){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("ATAuthen",Context.MODE_PRIVATE);
        String accessToken = pref.getString("keyaccesstoken", "empty");
        String authHeader = "Bearer " + accessToken;
        apiService = RetrofitClient.getInstance().create(APIService.class);
        apiService.getAllTask(authHeader,1,100,"asc",groupId, status,"priority", "").enqueue(new Callback<PaginationTask>() {
            @Override
            public void onResponse(Call<PaginationTask> call, Response<PaginationTask> response) {
                if (response.isSuccessful()) {
                    taskList.addAll(response.body().getData());
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
    private void getCategory(int groupId){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("ATAuthen",Context.MODE_PRIVATE);
        String accessToken = pref.getString("keyaccesstoken", "empty");
        String authHeader = "Bearer " + accessToken;
        APIService apiService = RetrofitClient.getInstance().create(APIService.class);
        apiService.getAllCategory(authHeader,1,100,"asc",groupId).enqueue(new Callback<ResponseCate>() {
            @Override
            public void onResponse(Call<ResponseCate> call, Response<ResponseCate> response) {
                if (response.isSuccessful()) {
                    listCategory.addAll(response.body().getData());
                }else{
                    try {
                        Log.v("Error code 400",response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseCate> call, Throwable t) {
                Log.d("TAG", "onFailure: " + t.getMessage());
            }
        });
    }
    private void getMyGroups() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("ATAuthen", Context.MODE_PRIVATE);
        String accessToken = pref.getString("keyaccesstoken", "empty");
        String authHeader = "Bearer " + accessToken;
        apiService = RetrofitClient.getInstance().create(APIService.class);
        apiService.getMyGroups(authHeader).enqueue(new Callback<List<Group>>() {
            @Override
            public void onResponse(Call<List<Group>> call, Response<List<Group>> response) {
                if (response.isSuccessful()) {
                    listGroup = response.body();
                    for (Group group : listGroup) {
                        if (group.getRole().equals("group_member")) {
                            getAllTask("TODO",group.getId());
                            getAllTask("IN_PROGRESS",group.getId());
                            getAllTask("DONE",group.getId());
                            getAllTask("POSTPONED",group.getId());
                            getAllTask("CANCELED",group.getId());
                            getCategory(group.getId());
                        }
                    }
                } else {
                    try {
                        Log.v("Error code 400", response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Group>> call, Throwable t) {
                Log.e("TAG", "onFailure: " + t.getMessage());
            }
        });
    }
}
