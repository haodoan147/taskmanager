package com.example.task_management.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.task_management.R;
import com.example.task_management.model.User;
import com.example.task_management.service.APIService;
import com.example.task_management.utils.RetrofitClient;
import com.example.task_management.utils.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyProfileActivity extends AppCompatActivity {
    TextView id, userName, userEmail, name;
    Button logOutBtn;
    APIService apiService;
    Toolbar toolbar;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_profile);
        initView();
        getMyProfile();
        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MyProfileActivity.this, "Test Clicked", Toast.LENGTH_SHORT).show();
                SharedPrefManager.getInstance(getApplicationContext()).logout();
                Intent intent = new Intent(MyProfileActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
    }
    private void initView() {
        id = findViewById(R.id.textViewId);
        userName = findViewById(R.id.textViewUsername);
        userEmail = findViewById(R.id.textViewEmail);
        name = findViewById(R.id.textViewName);
        logOutBtn = findViewById(R.id.btnLogout);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Thông tin cá nhân");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void getMyProfile(){

        apiService = RetrofitClient.getInstance().create(APIService.class);
        String accessToken = (SharedPrefManager.getInstance(getApplicationContext()).getAccessToken()).getAccessToken();
        String authHeader = "Bearer " + accessToken;
        apiService.getMyProfile(authHeader).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User user = response.body();
                if (response.isSuccessful()) {
                    userName.setText(user.getName());
                    userEmail.setText(user.getEmail());
                    name.setText(user.getName());
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
            }
        });
    }
}
