package com.example.task_management.activity.group.group;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.task_management.R;
import com.example.task_management.activity.HomeActivity;
import com.example.task_management.activity.LoadingDialog;
import com.example.task_management.activity.MyProfileActivity;
import com.example.task_management.activity.SignInActivity;
import com.example.task_management.activity.group.category.MyGroupCreateCategoryActivity;
import com.example.task_management.activity.group.label.MyGroupCreateLabelActivity;
import com.example.task_management.activity.group.label.MyGroupLabelFragment;
import com.example.task_management.activity.group.member.MyGroupMemberFragment;
import com.example.task_management.activity.group.task.MyGroupTaskFragment;
import com.example.task_management.activity.group.category.MyGroupCategoryFragment;
import com.example.task_management.activity.task.CalendarActivity;
import com.example.task_management.activity.group.task.MyGroupCreateTaskActivity;
import com.example.task_management.model.Category;
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

public class ManageMyGroupActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;
    APIService apiService;
    String id, name, email;
    NavigationView navigationView;
    List<Task> taskList= new ArrayList<>();
    Integer idGroup;
    List<Category> listCategory= new ArrayList<>();
    List<User> memberList= new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_my_group_activity);
        LoadingDialog loadingDialog = new LoadingDialog(ManageMyGroupActivity.this);
        Intent intent = getIntent();
        idGroup = intent.getIntExtra("idGroup", 1);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        drawerLayout = findViewById(R.id.drawer_layout);
        View fab = findViewById(R.id.fab);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        getMyProfile();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        navigationView.bringToFront();
        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.task:
                    toolbar.setTitle("Quản lí công việc");
                    taskList.clear();
                    memberList.clear();
                    listCategory.clear();
                    getAllTask("TODO");
                    getAllTask("IN_PROGRESS");
                    getAllTask("DONE");
                    getAllTask("POSTPONED");
                    getAllTask("CANCELED");
                    getCategory();
                    getMyGroupMembers();
                    loadingDialog.startLoadingDialog();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Replace the current fragment with NewTaskFragment and pass the parameter
                            Fragment newFragment = new MyGroupTaskFragment();
                            Bundle args = new Bundle();
                            args.putSerializable("newTaskList", (Serializable) taskList);
                            args.putSerializable("newCateList", (Serializable) listCategory);
                            args.putSerializable("newMemberList", (Serializable) memberList);
                            args.putInt("idGroup", idGroup);
                            newFragment.setArguments(args);
                            replaceFragment(newFragment);
                            loadingDialog.dismissDialog();
                        }
                    }, 2000);
                    break;
                case R.id.cate:
                    toolbar.setTitle("Quản lí danh mục");
                    loadingDialog.startLoadingDialog();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MyGroupCategoryFragment cateFragment = new MyGroupCategoryFragment();
                            Bundle cateArgs = new Bundle();
                            cateArgs.putInt("idGroup", idGroup);
                            cateFragment.setArguments(cateArgs);
                            replaceFragment(cateFragment);
                            loadingDialog.dismissDialog();
                        }
                    }, 2000);
                    break;
                case R.id.member:
                    loadingDialog.startLoadingDialog();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toolbar.setTitle("Quản lí thành viên");
                            MyGroupMemberFragment memberFragment = new MyGroupMemberFragment();
                            Bundle memberArgs = new Bundle();
                            memberArgs.putInt("idGroup", idGroup);
                            memberFragment.setArguments(memberArgs);
                            replaceFragment(memberFragment);
                            loadingDialog.dismissDialog();
                        }
                    }, 2000);
                    break;
                case R.id.label:
                    loadingDialog.startLoadingDialog();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                                toolbar.setTitle("Quản lí nhãn");
                                MyGroupLabelFragment labelFragment = new MyGroupLabelFragment();
                                Bundle labelArgs = new Bundle();
                                labelArgs.putInt("idGroup", idGroup);
                                labelFragment.setArguments(labelArgs);
                                replaceFragment(labelFragment);
                                loadingDialog.dismissDialog();
                        }
                    }, 2000);
                    break;
            }
            return true;
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog();
            }
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
                intent = new Intent(getApplicationContext(), HomeActivity.class);
                finish();
                startActivity(intent);
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
    private void showBottomDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);

        LinearLayout layoutTask = dialog.findViewById(R.id.create_task);
        LinearLayout layoutCate = dialog.findViewById(R.id.create_cate);
        LinearLayout layoutLabel = dialog.findViewById(R.id.create_label);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

        layoutTask.setOnClickListener(new View.OnClickListener() {
            Intent intent;
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                intent = new Intent(getApplicationContext(), MyGroupCreateTaskActivity.class);
                intent.putExtra("idGroup", idGroup);
                startActivity(intent);

            }
        });

        layoutCate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                dialog.dismiss();
                intent = new Intent(getApplicationContext(), MyGroupCreateCategoryActivity.class);
                intent.putExtra("idGroup", idGroup);
                startActivity(intent);

            }
        });
        layoutLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                dialog.dismiss();
                intent = new Intent(getApplicationContext(), MyGroupCreateLabelActivity.class);
                intent.putExtra("idGroup", idGroup);
                startActivity(intent);

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }
    private void getMyProfile(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("ATAuthen", Context.MODE_PRIVATE);
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
    private void getAllTask(String status){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("ATAuthen",Context.MODE_PRIVATE);
        String accessToken = pref.getString("keyaccesstoken", "empty");
        String authHeader = "Bearer " + accessToken;
        apiService = RetrofitClient.getInstance().create(APIService.class);
        apiService.getAllTask(authHeader,1,100,"asc",idGroup, status,"priority", "").enqueue(new Callback<PaginationTask>() {
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
    private void getCategory(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("ATAuthen",Context.MODE_PRIVATE);
        String accessToken = pref.getString("keyaccesstoken", "empty");
        String authHeader = "Bearer " + accessToken;
        APIService apiService = RetrofitClient.getInstance().create(APIService.class);
        apiService.getAllCategory(authHeader,1,100,"asc",idGroup).enqueue(new Callback<ResponseCate>() {
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
    private void getMyGroupMembers(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("ATAuthen", Context.MODE_PRIVATE);
        String accessToken = pref.getString("keyaccesstoken", "empty");
        String authHeader = "Bearer " + accessToken;
        apiService = RetrofitClient.getInstance().create(APIService.class);
        apiService.getAllMembers(authHeader,idGroup).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    memberList = response.body();
                }else{
                    try {
                        Log.v("Error code 400",response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.e("TAG", "onFailure: " + t.getMessage());
            }
        });
    }
}
