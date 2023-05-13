package com.example.task_management.activity.group_task;

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
import com.example.task_management.model.Category;
import com.example.task_management.model.CreateCategory;
import com.example.task_management.service.APIService;
import com.example.task_management.utils.RetrofitClient;
import com.example.task_management.utils.SharedPrefManager;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateCategoryActivity extends AppCompatActivity {
    EditText edittext_category;
    ArrayList<String> priorityItems  = new ArrayList<>();
    AutoCompleteTextView edtTaskPriority;
    ArrayAdapter<String> adapterItems;
    APIService apiService;
    Button btnSubmit;
    String authHeader ;
    ImageView btnBack;
    public enum Priority
    {
        NONE, LOW, MEDIUM, HIGH, URGENT
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_new_cate);
        initView();
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createCate();
            }
        });
    }
    private void createCate() {
        final String taskName = edittext_category.getText().toString();
        final String taskPriority = edtTaskPriority.getText().toString();
        final int taskPriorityID = getPriorityId();

        if (TextUtils.isEmpty(taskName)) {
            edittext_category.setError("Vui lòng nhập lại");
            edittext_category.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(taskPriority)) {
            edtTaskPriority.setError("Vui lòng nhập lại");
            edtTaskPriority.requestFocus();
            return;
        }
        apiService = RetrofitClient.getInstance().create(APIService.class);
        CreateCategory newCate = new CreateCategory(taskName, taskPriorityID);
        try{
            apiService.getCreateNewCategory(authHeader, newCate).enqueue(new Callback<Category>() {
                @Override
                public void onResponse(Call<Category> call, Response<Category> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Create Cate Success", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), CategoryActivity.class);
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
                public void onFailure(Call<Category> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Create Cate Failure", Toast.LENGTH_SHORT).show();
                }
            });
        }catch(Exception e){
            Log.e("TAG", String.valueOf(e));
        }

    }
    private void initView(){
        String accessToken = (SharedPrefManager.getInstance(getApplicationContext()).getAccessToken()).getAccessToken();
        authHeader = "Bearer " + accessToken;
        priorityItems.add("NONE");
        priorityItems.add("LOW");
        priorityItems.add("MEDIUM");
        priorityItems.add("HIGH");
        priorityItems.add("URGENT");
        btnSubmit = findViewById(R.id.btn_submit);
        edittext_category = findViewById(R.id.edittext_category);
        edtTaskPriority = findViewById(R.id.edittext_priority);
        adapterItems = new ArrayAdapter<String>(this,R.layout.dropdown_select_option,priorityItems);
        edtTaskPriority.setAdapter(adapterItems);
        btnBack = findViewById(R.id.btn_back_to_context);
        btnBack.setOnClickListener(view -> finish());
    }
    private Integer getPriorityId(){
        int selectedId = Priority.valueOf(edtTaskPriority.getText().toString()).ordinal();;
        return selectedId;
    }
}
