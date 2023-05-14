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

import com.example.task_management.R;
import com.example.task_management.activity.HomeActivity;
import com.example.task_management.activity.MyProfileActivity;
import com.example.task_management.activity.SignInActivity;
import com.example.task_management.activity.group.member.MyGroupMemberFragment;
import com.example.task_management.activity.group.task.MyGroupTaskFragment;
import com.example.task_management.activity.group_task.CateByGroupFragment;
import com.example.task_management.activity.group_task.CreateCategoryActivity;
import com.example.task_management.activity.task.CalendarActivity;
import com.example.task_management.activity.task.CreateTaskActivity;
import com.example.task_management.model.Category;
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
            taskList.clear();
            getAllTask("TODO");
            getAllTask("IN_PROGRESS");
            getAllTask("DONE");
            getAllTask("POSTPONED");
            getAllTask("CANCELED");
            getCategory();
            getMyGroupMembers();
            switch (item.getItemId()) {
                case R.id.task:
                    List<Task> newTaskList = taskList;
                    List<Category> newCateList = listCategory;
                    List<User> newMemberList = memberList;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Replace the current fragment with NewTaskFragment and pass the parameter
                            Fragment newFragment = new MyGroupTaskFragment();
                            Bundle args = new Bundle();
                            args.putSerializable("newTaskList", (Serializable) newTaskList);
                            args.putSerializable("newCateList", (Serializable) newCateList);
                            args.putSerializable("newMemberList", (Serializable) newMemberList);
                            newFragment.setArguments(args);
                            replaceFragment(newFragment);
                        }
                    }, 3000);
                    break;
                case R.id.cate:
                    replaceFragment(new CateByGroupFragment());
                    break;
                case R.id.member:
                    MyGroupMemberFragment fragment = new MyGroupMemberFragment();
                    Bundle args = new Bundle();
                    args.putInt("idGroup", idGroup);
                    fragment.setArguments(args);
                    replaceFragment(fragment);
                    break;
                case R.id.label:
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

        LinearLayout layoutTask = dialog.findViewById(R.id.layoutTask);
        LinearLayout layoutCate = dialog.findViewById(R.id.layoutCate);
        LinearLayout layoutGroup = dialog.findViewById(R.id.layoutGroup);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

        layoutTask.setOnClickListener(new View.OnClickListener() {
            Intent intent;
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                intent = new Intent(getApplicationContext(), CreateTaskActivity.class);
                startActivity(intent);

            }
        });

        layoutCate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                dialog.dismiss();
                intent = new Intent(getApplicationContext(), CreateCategoryActivity.class);
                startActivity(intent);

            }
        });

        layoutGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Toast.makeText(getApplicationContext(),"Go live is Clicked",Toast.LENGTH_SHORT).show();

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
