package com.example.task_management.activity.group.task;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.task_management.R;
import com.example.task_management.activity.HomeActivity;
import com.example.task_management.model.Category;
import com.example.task_management.model.CreateTask;
import com.example.task_management.model.Label;
import com.example.task_management.model.ResponseCate;
import com.example.task_management.model.ResponseLabel;
import com.example.task_management.model.Task;
import com.example.task_management.service.APIService;
import com.example.task_management.utils.RetrofitClient;
import com.example.task_management.utils.SharedPrefManager;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyGroupCreateTaskActivity extends AppCompatActivity {
    EditText edtTaskName, edtTaskDescription, edtTaskDuration;
    DatePicker deadlineDatePicker;
    ArrayList<String> labelItems  = new ArrayList<>();
    ArrayList<String> cateItems  = new ArrayList<>();
    ArrayList<String> priorityItems  = new ArrayList<>();
    AutoCompleteTextView edtTaskCate,edtTaskLabel,edtTaskPriority;
    ArrayAdapter<String> adapterItems;
    APIService apiService;
    Button btnSubmit;
    String authHeader ;
    List<Label> listLabel = new ArrayList<>();
    List<Category> listCategory = new ArrayList<>();
    ImageView btnBack;
    Toolbar toolbar;
    public enum Priority
    {
        NONE, LOW, MEDIUM, HIGH, URGENT
    }
    int idGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_new_task);
        Intent intent = getIntent();
        idGroup = intent.getIntExtra("idGroup", 1);
        String accessToken = (SharedPrefManager.getInstance(getApplicationContext()).getAccessToken()).getAccessToken();
        authHeader = "Bearer " + accessToken;
        getLabel();
        getCategory();
        initView();
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createtask();
            }
        });
    }
    private void createtask() {
        final String taskName = edtTaskName.getText().toString();
        final String taskDes = edtTaskDescription.getText().toString();
        final String taskCate = edtTaskCate.getText().toString();
        final String taskLabel = edtTaskLabel.getText().toString();
        final String taskPriority = edtTaskPriority.getText().toString();
        final String taskDuration = edtTaskDuration.getText().toString();
        final int taskCateID = getCateId();
        final int taskLabelID = getLabelId();
        final int taskPriorityID = getPriorityId();

        if (TextUtils.isEmpty(taskName)) {
            edtTaskName.setError("Vui lòng nhập lại");
            edtTaskName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(taskDes)) {
            edtTaskDescription.setError("Vui lòng nhập lại");
            edtTaskDescription.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(taskCate)) {
            edtTaskCate.setError("Vui lòng nhập lại");
            edtTaskCate.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(taskLabel)) {
            edtTaskLabel.setError("Vui lòng nhập lại");
            edtTaskLabel.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(taskPriority)) {
            edtTaskPriority.setError("Vui lòng nhập lại");
            edtTaskPriority.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(taskDuration)) {
            edtTaskDuration.setError("Vui lòng nhập lại");
            edtTaskDuration.requestFocus();
            return;
        }
        int day = deadlineDatePicker.getDayOfMonth();
        int month = deadlineDatePicker.getMonth();
        int year = deadlineDatePicker.getYear();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        Date dlDate = calendar.getTime();
        SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        isoDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String isoDate = isoDateFormat.format(dlDate);
        apiService = RetrofitClient.getInstance().create(APIService.class);
        Integer[] newlist = {taskLabelID};
        CreateTask newTask = new CreateTask(taskName, taskDes, isoDate, Integer.parseInt(taskDuration), Arrays.asList(newlist), taskCateID, taskPriorityID,idGroup);
        try{
            apiService.getCreateNewTask(authHeader, newTask).enqueue(new Callback<Task>() {
                @Override
                public void onResponse(Call<Task> call, Response<Task> response) {
                    if (response.isSuccessful()) {
                        Task task = response.body();
                        Toast.makeText(MyGroupCreateTaskActivity.this, "Create Task Success", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MyGroupCreateTaskActivity.this, HomeActivity.class);
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
                public void onFailure(Call<Task> call, Throwable t) {
                    Toast.makeText(MyGroupCreateTaskActivity.this, "Create Task Failure", Toast.LENGTH_SHORT).show();
                }
            });
        }catch(Exception e){
            Log.e("TAG", String.valueOf(e));
        }

    }
    private void initView(){
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Tạo task");
        priorityItems.add("NONE");
        priorityItems.add("LOW");
        priorityItems.add("MEDIUM");
        priorityItems.add("HIGH");
        priorityItems.add("URGENT");
        edtTaskName = findViewById(R.id.edittext_task);
        edtTaskDescription =  findViewById(R.id.edittext_description);
        btnSubmit = findViewById(R.id.btn_submit);
        edtTaskCate = findViewById(R.id.edittext_category);
        edtTaskLabel = findViewById(R.id.edittext_label);
        edtTaskPriority = findViewById(R.id.edittext_priority);
        edtTaskDuration  = findViewById(R.id.edittext_duration);
        deadlineDatePicker = findViewById(R.id.dlDatePicker);
        for (Category value: listCategory) {
            cateItems.add(value.getName());
        }
        adapterItems = new ArrayAdapter<String>(this,R.layout.dropdown_select_option,cateItems);
        edtTaskCate.setAdapter(adapterItems);
        adapterItems = new ArrayAdapter<String>(this,R.layout.dropdown_select_option,labelItems);
        edtTaskLabel.setAdapter(adapterItems);
        adapterItems = new ArrayAdapter<String>(this,R.layout.dropdown_select_option,priorityItems);
        edtTaskPriority.setAdapter(adapterItems);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void getLabel(){
        apiService = RetrofitClient.getInstance().create(APIService.class);
        apiService.getAllLabel(authHeader,1,100,"asc",idGroup).enqueue(new Callback<ResponseLabel>() {
            @Override
            public void onResponse(Call<ResponseLabel> call, Response<ResponseLabel> response) {
                if (response.isSuccessful()) {
                    listLabel.addAll(response.body().getData());
                    for (Label value: listLabel) {
                        labelItems.add(value.getName());
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseLabel> call, Throwable t) {
                Log.d("TAG", "onFailure: " + t.getMessage());
            }
        });
    }
    private void getCategory(){
        apiService = RetrofitClient.getInstance().create(APIService.class);
        apiService.getAllCategory(authHeader,1,100,"asc",idGroup).enqueue(new Callback<ResponseCate>() {
            @Override
            public void onResponse(Call<ResponseCate> call, Response<ResponseCate> response) {
                if (response.isSuccessful()) {
                    listCategory.addAll(response.body().getData());
                    for (Category value: listCategory) {
                        cateItems.add(value.getName());
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseCate> call, Throwable t) {
                Log.d("TAG", "onFailure: " + t.getMessage());
            }
        });
    }
    private Integer getLabelId(){
        int selectedId = 0;
        for (Label value: listLabel) {
            if(value.getName().contentEquals(edtTaskLabel.getText().toString()))
                selectedId = value.getId();
        }
        return selectedId;
    }
    private Integer getCateId(){
        int selectedId = 0;
        for (Category value: listCategory) {
            if(value.getName().contentEquals(edtTaskCate.getText().toString()))
                selectedId = value.getId();
        }
        return selectedId;
    }
    private Integer getPriorityId(){
        int selectedId = Priority.valueOf(edtTaskPriority.getText().toString()).ordinal();;
        return selectedId;
    }
}