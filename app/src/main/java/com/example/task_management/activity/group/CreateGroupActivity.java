package com.example.task_management.activity.group;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.task_management.R;
import com.example.task_management.activity.category.CategoryActivity;
import com.example.task_management.activity.group.MyGroupActivity;
import com.example.task_management.model.Category;
import com.example.task_management.model.CreateCategory;
import com.example.task_management.model.Group;
import com.example.task_management.service.APIService;
import com.example.task_management.utils.RetrofitClient;
import com.example.task_management.utils.SharedPrefManager;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateGroupActivity extends AppCompatActivity {
    EditText edittext_name;
    APIService apiService;
    Button btnSubmit;
    String authHeader ;
    ImageView btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_new_group);
        initView();
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createGroup();
            }
        });
    }
    private void createGroup() {
        final String groupName = edittext_name.getText().toString();
        apiService = RetrofitClient.getInstance().create(APIService.class);
        try{
            apiService.createGroup(authHeader, groupName).enqueue(new Callback<Group>() {
                @Override
                public void onResponse(Call<Group> call, Response<Group> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Create Group Success", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MyGroupActivity.class);
                        startActivity(intent);
                    }else{
                        try {
                            Log.v("Error code 400",response.errorBody().string());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                @Override
                public void onFailure(Call<Group> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Create Group Failure", Toast.LENGTH_SHORT).show();
                }
            });
        }catch(Exception e){
            Log.e("TAG", String.valueOf(e));
        }

    }
    private void initView(){
        String accessToken = (SharedPrefManager.getInstance(getApplicationContext()).getAccessToken()).getAccessToken();
        authHeader = "Bearer " + accessToken;
        btnSubmit = findViewById(R.id.btn_submit);
        edittext_name= findViewById(R.id.edittext_name);
        btnBack = findViewById(R.id.btn_back_to_context);
        btnBack.setOnClickListener(view -> finish());
    }
}
