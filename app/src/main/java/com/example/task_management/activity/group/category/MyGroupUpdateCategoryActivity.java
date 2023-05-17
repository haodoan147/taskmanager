package com.example.task_management.activity.group.category;

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
import com.example.task_management.activity.group.task.MyGroupUpdateTaskActivity;
import com.example.task_management.model.Category;
import com.example.task_management.model.CreateCategory;
import com.example.task_management.model.Task;
import com.example.task_management.model.UpdateCate;
import com.example.task_management.service.APIService;
import com.example.task_management.utils.RetrofitClient;
import com.example.task_management.utils.SharedPrefManager;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyGroupUpdateCategoryActivity extends AppCompatActivity {
    EditText edittext_category;
    ArrayList<String> priorityItems  = new ArrayList<>();
    AutoCompleteTextView edtTaskPriority;
    ArrayAdapter<String> adapterItems;
    APIService apiService;
    Button btnSubmit;
    String authHeader ;
    ImageView btnBack;
    int idGroup;
    public enum Priority
    {
        NONE, LOW, MEDIUM, HIGH, URGENT
    }
    Toolbar toolbar;
    Category oldCate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_new_category);
        Intent intent = getIntent();
        oldCate = (Category) intent.getSerializableExtra("oldCate");
        idGroup = intent.getIntExtra("idGroup", 1);
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
        UpdateCate newCate = new UpdateCate(taskName, taskPriorityID);
        try{
            apiService.updateCate(authHeader,oldCate.getId(), newCate).enqueue(new Callback<Category>() {
                @Override
                public void onResponse(Call<Category> call, Response<Category> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Cập nhật danh mục thành công", Toast.LENGTH_SHORT).show();
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
                public void onFailure(Call<Category> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Cập nhật danh mục thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        }catch(Exception e){
            Log.e("TAG", String.valueOf(e));
        }

    }
    private void initView(){
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Cập nhật danh mục");
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
        edittext_category.setText(oldCate.getName());
        edtTaskPriority.setText(String.valueOf(MyGroupUpdateTaskActivity.Priority.values()[oldCate.getPriority()]));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private Integer getPriorityId(){
        int selectedId = MyGroupCreateCategoryActivity.Priority.valueOf(edtTaskPriority.getText().toString()).ordinal();;
        return selectedId;
    }
}
