package com.example.task_management.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task_management.R;
import com.example.task_management.adapter.CategoryAdapter;
import com.example.task_management.model.AccessToken;
import com.example.task_management.model.Category;
import com.example.task_management.model.MyProfile;
import com.example.task_management.service.APIService;
import com.example.task_management.utils.RetrofitClient;
import com.example.task_management.utils.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyProfileActivity extends AppCompatActivity {
    TextView id, userName, userEmail, name;
    Button logOutBtn;
    APIService apiService;
    TextView appHeader;
    ImageView profileBtn;
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
        appHeader = findViewById((R.id.app_header));
        appHeader.setText("My profile");
        profileBtn = findViewById(R.id.right_icon);
        if(appHeader.getText() =="My profile"){
            profileBtn.setVisibility(View.INVISIBLE);
        }
    }
    private void getMyProfile(){

        apiService = RetrofitClient.getInstance().create(APIService.class);
        String accessToken = (SharedPrefManager.getInstance(getApplicationContext()).getAccessToken()).getAccessToken();
        String authHeader = "Bearer " + accessToken;
        apiService.getMyProfile(authHeader).enqueue(new Callback<MyProfile>() {
            @Override
            public void onResponse(Call<MyProfile> call, Response<MyProfile> response) {
                MyProfile myProfile = response.body();
                if (response.isSuccessful()) {
                    userName.setText(myProfile.getName());
                    userEmail.setText(myProfile.getEmail());
                    name.setText(myProfile.getName());
                }
            }
            @Override
            public void onFailure(Call<MyProfile> call, Throwable t) {
                Log.d("TAG", "onFailure: " + t.getMessage());
            }
        });
    }
}
