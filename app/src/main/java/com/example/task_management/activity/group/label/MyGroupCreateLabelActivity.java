package com.example.task_management.activity.group.label;

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
import androidx.appcompat.widget.Toolbar;

import com.example.task_management.R;
import com.example.task_management.model.Category;
import com.example.task_management.model.CreateCategory;
import com.example.task_management.model.CreateLabel;
import com.example.task_management.model.Label;
import com.example.task_management.service.APIService;
import com.example.task_management.utils.RetrofitClient;
import com.example.task_management.utils.SharedPrefManager;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyGroupCreateLabelActivity extends AppCompatActivity {
    EditText edittext_label,edittext_color;
    APIService apiService;
    Button btnSubmit;
    String authHeader ;
    ImageView btnBack;
    int idGroup;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_new_label);
        Intent intent = getIntent();
        idGroup = intent.getIntExtra("idGroup", 1);
        initView();
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createLabel();
            }
        });
    }
    private void createLabel() {
        final String labelName = edittext_label.getText().toString();
        final String labelColor = edittext_color.getText().toString();

        if (TextUtils.isEmpty(labelName)) {
            edittext_label.setError("Vui lòng nhập lại");
            edittext_label.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(labelColor)) {
            edittext_color.setError("Vui lòng nhập lại");
            edittext_color.requestFocus();
            return;
        }
        apiService = RetrofitClient.getInstance().create(APIService.class);
        CreateLabel newLabel = new CreateLabel(labelName, labelColor, idGroup);
        try{
            apiService.getCreateNewLabel(authHeader, newLabel).enqueue(new Callback<Label>() {
                @Override
                public void onResponse(Call<Label> call, Response<Label> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Create Cate Success", Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        try {
                            Log.v("Error code 400",response.errorBody().string());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                @Override
                public void onFailure(Call<Label> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Create Cate Failure", Toast.LENGTH_SHORT).show();
                }
            });
        }catch(Exception e){
            Log.e("TAG", String.valueOf(e));
        }

    }
    private void initView(){
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Tạo nhãn");
        String accessToken = (SharedPrefManager.getInstance(getApplicationContext()).getAccessToken()).getAccessToken();
        authHeader = "Bearer " + accessToken;
        btnSubmit = findViewById(R.id.btn_submit);
        edittext_label = findViewById(R.id.edittext_label);
        edittext_color = findViewById(R.id.edittext_color);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
